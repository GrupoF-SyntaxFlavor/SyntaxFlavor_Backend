package bo.edu.ucb.syntax_flavor_backend.menu.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import bo.edu.ucb.syntax_flavor_backend.menu.bl.MenuBL;
import bo.edu.ucb.syntax_flavor_backend.menu.dto.MenuItemResponseDTO;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;
import io.swagger.v3.oas.annotations.Operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/menu")
public class MenuItemAPI {

    Logger LOGGER = LoggerFactory.getLogger(MenuItemAPI.class);

    @Autowired
    private final MenuBL menuBL;

    public MenuItemAPI(MenuBL menuBL) {
        this.menuBL = menuBL;
    }

    // Endpoint para obtener todos los platillos disponibles
    @Operation(summary = "Get all menu items", description = "Returns a list of all menu items available")
    @GetMapping("/item")
    public ResponseEntity<SyntaxFlavorResponse<List<MenuItemResponseDTO>>> getAllMenuItems() {
        LOGGER.info("Endpoint GET /api/v1/menu/item");
        SyntaxFlavorResponse<List<MenuItemResponseDTO>> sfrResponse = new SyntaxFlavorResponse<>();
        try {
            List<MenuItemResponseDTO> menuItems = menuBL.getMenuItems();
            sfrResponse.setResponseCode("MEN-000");
            sfrResponse.setPayload(menuItems);
            LOGGER.info("Endpoint successfully, returning menu items: {}", menuItems);
            return ResponseEntity.ok(sfrResponse);
        } catch (Exception e) {
            LOGGER.error("Error retrieving menu items: {}", e.getMessage());
            sfrResponse.setResponseCode("MEN-600");
            sfrResponse.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfrResponse);
        }
    }
}
