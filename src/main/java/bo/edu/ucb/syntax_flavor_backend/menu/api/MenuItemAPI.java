package bo.edu.ucb.syntax_flavor_backend.menu.api;

import java.util.List;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import bo.edu.ucb.syntax_flavor_backend.menu.bl.MenuBL;
import bo.edu.ucb.syntax_flavor_backend.menu.dto.MenuItemResponseDTO;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;
import io.swagger.v3.oas.annotations.Operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class MenuItemAPI {

    Logger LOGGER = LoggerFactory.getLogger(MenuItemAPI.class);

    @Autowired
    private final MenuBL menuBL;

    public MenuItemAPI(MenuBL menuBL) {
        this.menuBL = menuBL;
    }

    // Endpoint para obtener todos los platillos disponibles
    @Operation(summary = "Get all menu items", description = "Returns a list of all menu items available")
    @GetMapping("/menu/item")
    public ResponseEntity<SyntaxFlavorResponse<List<MenuItemResponseDTO>>> getAllMenuItems(HttpServletRequest request) {

        LOGGER.info("Endpoint GET /api/v1/menu/item");

        // FIXME: No es la mejor forma de manejar el token JWT.
        // TODO: Debería ser modularizado utilizando un middleware o función dedicada para la autenticación JWT.
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        SyntaxFlavorResponse<List<MenuItemResponseDTO>> sfrResponse = new SyntaxFlavorResponse<>();

        if (token == null || !token.startsWith("Bearer ")) {
            sfrResponse.setResponseCode("MEN-401");
            sfrResponse.setErrorMessage("Unauthorized: No token provided");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(sfrResponse);
        }

        try {
            token = token.substring(7); // Remove "Bearer " prefix
            String userId = JWT.decode(token).getSubject(); // Decode JWT and get subject (user ID)

            LOGGER.info("Token verified, userId: {}", userId);

            // FIXME: Llamadas a lógica de negocio deberían estar en el BL.
            // TODO: Mover esta operación al `bl`, ya que la responsabilidad de obtener los items debería delegarse.
            List<MenuItemResponseDTO> menuItems = menuBL.getMenuItems();
            sfrResponse.setResponseCode("MEN-000");
            sfrResponse.setPayload(menuItems);

            LOGGER.info("Returning menu items: {}", menuItems);
            return ResponseEntity.ok(sfrResponse);

        } catch (JWTDecodeException e) {
            LOGGER.error("Invalid token: {}", e.getMessage());
            sfrResponse.setResponseCode("MEN-401");
            sfrResponse.setErrorMessage("Unauthorized: Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(sfrResponse);
        } catch (Exception e) {
            LOGGER.error("Error retrieving menu items: {}", e.getMessage());
            sfrResponse.setResponseCode("MEN-600");
            sfrResponse.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfrResponse);
        }
    }

    @PatchMapping("/public/menu/item/{id}/image")
    public ResponseEntity<SyntaxFlavorResponse<String>> updateMenuItemImage(@PathVariable Integer id,
                                                                            @RequestPart("file") MultipartFile file) {
        LOGGER.info("Endpoint PATCH /api/v1/menu/item/{}/image", id);
        SyntaxFlavorResponse<String> sfrResponse = new SyntaxFlavorResponse<>();
        try {
            // FIXME: Sería preferible delegar esta lógica al `bl`.
            String imageUrl = menuBL.updateMenuItemImage(id, file);
            sfrResponse.setResponseCode("MEN-002");
            sfrResponse.setPayload(imageUrl);
            LOGGER.info("Endpoint successfully, returning image URL: {}", imageUrl);
            return ResponseEntity.ok(sfrResponse);
        } catch (Exception e) {
            LOGGER.error("Error updating menu item image: {}", e.getMessage());
            sfrResponse.setResponseCode("MEN-602");
            sfrResponse.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfrResponse);
        }
    }

    @GetMapping("/menu/item/{id}/image")
    public ResponseEntity<SyntaxFlavorResponse<Object>> getMenuItemImage(@PathVariable Integer id) {
        LOGGER.info("Endpoint GET /api/v1/menu/item/{}/image", id);
        SyntaxFlavorResponse<Object> sfrResponse = new SyntaxFlavorResponse<>();
        try {
            // FIXME: La obtención de la imagen también debería delegarse al `bl`.
            byte[] image = menuBL.getMenuItemImage(id);
            sfrResponse.setResponseCode("MEN-004");
            sfrResponse.setPayload(image);
            LOGGER.info("Endpoint successfully, returning image");
            return ResponseEntity.ok(sfrResponse);
        } catch (Exception e) {
            LOGGER.error("Error getting menu item image: {}", e.getMessage());
            sfrResponse.setResponseCode("MEN-604");
            sfrResponse.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfrResponse);
        }
    }
}
