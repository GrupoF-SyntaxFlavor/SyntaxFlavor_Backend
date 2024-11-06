package bo.edu.ucb.syntax_flavor_backend.menu.api;

import java.util.List;
import java.math.BigDecimal;

import bo.edu.ucb.syntax_flavor_backend.user.bl.UserBL;
import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import bo.edu.ucb.syntax_flavor_backend.menu.bl.MenuBL;
import bo.edu.ucb.syntax_flavor_backend.menu.dto.MenuItemRequestDTO;
import bo.edu.ucb.syntax_flavor_backend.menu.dto.MenuItemResponseDTO;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1")
public class MenuItemAPI {
    Logger LOGGER = LoggerFactory.getLogger(MenuItemAPI.class);

    @Autowired
    private final MenuBL menuBL;

    @Autowired
    private final UserBL userBL;

    public MenuItemAPI(MenuBL menuBL, UserBL userBL) {
        this.menuBL = menuBL;
        this.userBL = userBL;
    }

    // Endpoint para obtener todos los platillos disponibles
    @Operation(summary = "Get all menu items", description = "Returns a list of all menu items available")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Menu items retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Error retrieving menu items")
        }
    )
    @GetMapping("/menu/item/all")
    public ResponseEntity<SyntaxFlavorResponse<List<MenuItemResponseDTO>>> getAllMenuItems(HttpServletRequest request) {
        LOGGER.info("Endpoint GET /api/v1/menu/item"); 
        SyntaxFlavorResponse<List<MenuItemResponseDTO>> sfrResponse = new SyntaxFlavorResponse<>();
        try {
            List<MenuItemResponseDTO> menuItems = menuBL.getMenuItems();
            sfrResponse.setResponseCode("MEN-000");
            sfrResponse.setPayload(menuItems);
            LOGGER.info("Returning menu items: {}", menuItems);
            return ResponseEntity.ok(sfrResponse);
        } catch (Exception e) {
            LOGGER.error("Error retrieving menu items: {}", e.getMessage());
            sfrResponse.setResponseCode("MEN-600");
            sfrResponse.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfrResponse);
        }
    }

    @Operation(summary = "Get menu items by price", description = "Returns a list of menu items within a price range this endpoint implements pagination, sorting and filtering")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Menu items retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Error retrieving menu items")
        }
    )
    @GetMapping("/menu/item")
    public ResponseEntity<SyntaxFlavorResponse<Page<MenuItemResponseDTO>>> getMenuItemsByPrice(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "true") boolean sortAscending) {
        LOGGER.info("Endpoint GET /api/v1/menu/item");
        SyntaxFlavorResponse<Page<MenuItemResponseDTO>> sfrResponse = new SyntaxFlavorResponse<>();
        try {
            Page<MenuItemResponseDTO> menuItems = menuBL.getMenuItemsByPriceSortByName(minPrice, maxPrice, pageNumber, pageSize, sortAscending);
            sfrResponse.setResponseCode("MEN-000");
            sfrResponse.setPayload(menuItems);
            LOGGER.info("Returning menu items: {}", menuItems.getContent());
            return ResponseEntity.ok(sfrResponse);
        } catch (Exception e) {
            LOGGER.error("Error getting menu items by price: {}", e.getMessage());
            sfrResponse.setResponseCode("MEN-600");
            sfrResponse.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfrResponse);
        }
    }
    
    @Operation(summary = "Replace an item's image", description = "Replaces an item's image with a new one, both objects still exist in the storage")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Image updated successfully"),
            @ApiResponse(responseCode = "500", description = "Error updating image")
        }
    )
    @PatchMapping("/public/menu/item/{id}/image")
    public ResponseEntity<SyntaxFlavorResponse<String>> updateMenuItemImage(@PathVariable Integer id,
                                                                            @RequestPart("file") MultipartFile file) {
        LOGGER.info("Endpoint PATCH /api/v1/menu/item/{}/image", id);
        SyntaxFlavorResponse<String> sfrResponse = new SyntaxFlavorResponse<>();
        try {
            String imageUrl = menuBL.updateMenuItemImage(id, file);
            sfrResponse.setResponseCode("MEN-002");
            sfrResponse.setPayload(imageUrl);
            LOGGER.info("Endpoint successfully, returning image URL: {}", imageUrl);
            return ResponseEntity.ok(sfrResponse);
        } catch (Exception e) {
            // TODO: Be more detailed about the error.
            LOGGER.error("Error updating menu item image: {}", e.getMessage());
            sfrResponse.setResponseCode("MEN-602");
            sfrResponse.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfrResponse);
        }
    }

    @Operation(summary = "Get an item's image", description = "Returns an item's image")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Image retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Error retrieving image")
        }
    )
    @GetMapping("/menu/item/{id}/image")
    public ResponseEntity<SyntaxFlavorResponse<Object>> getMenuItemImage(@PathVariable Integer id) {
        LOGGER.info("Endpoint GET /api/v1/menu/item/{}/image", id);
        SyntaxFlavorResponse<Object> sfrResponse = new SyntaxFlavorResponse<>();
        try {
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

    @Operation(summary = "Disable menu item", description = "Disables a menu item by id")
    @PatchMapping("/menu/item/{id}/disable")
    public ResponseEntity<SyntaxFlavorResponse<MenuItemResponseDTO>> disableMenuItem(
            @PathVariable Integer id,
            HttpServletRequest request) {
        LOGGER.info("Endpoint PATCH /api/v1/menu/item/{}/disable", id);

        String kcUserId = (String) request.getAttribute("kcUserId");
        User user = userBL.findUserByKcUserId(kcUserId);
        // FIXME: this is not the best way to do it :)
        if (user == null) {
            LOGGER.error("User with kcUserId {} not found", kcUserId);
            SyntaxFlavorResponse<MenuItemResponseDTO> sfrResponse = new SyntaxFlavorResponse<>();
            sfrResponse.setResponseCode("ORD-601");
            sfrResponse.setErrorMessage("User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(sfrResponse);
        }

        SyntaxFlavorResponse<MenuItemResponseDTO> sfrResponse = new SyntaxFlavorResponse<>();
        try {
            MenuItemResponseDTO menuItemResponseDTO = menuBL.disableMenuItem(id);
            sfrResponse.setResponseCode("MEN-005");
            sfrResponse.setPayload(menuItemResponseDTO);
            LOGGER.info("Menu item {} disabled successfully", id);
            return ResponseEntity.ok(sfrResponse);
        } catch (Exception e) {
            LOGGER.error("Error disabling menu item: {}", e.getMessage());
            sfrResponse.setResponseCode("MEN-605");
            sfrResponse.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfrResponse);
        }
    }

    @Operation(summary = "Enable menu item", description = "Enables a menu item by id")
    @PatchMapping("/menu/item/{id}/enable")
    public ResponseEntity<SyntaxFlavorResponse<MenuItemResponseDTO>> enableMenuItem(
            @PathVariable Integer id,
            HttpServletRequest request) {
        LOGGER.info("Endpoint PATCH /api/v1/menu/item/{}/enable", id);

        String kcUserId = (String) request.getAttribute("kcUserId");
        User user = userBL.findUserByKcUserId(kcUserId);
        // FIXME: this is not the best way to do it :)
        if (user == null) {
            LOGGER.error("User with kcUserId {} not found", kcUserId);
            SyntaxFlavorResponse<MenuItemResponseDTO> sfrResponse = new SyntaxFlavorResponse<>();
            sfrResponse.setResponseCode("ORD-601");
            sfrResponse.setErrorMessage("User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(sfrResponse);
        }

        SyntaxFlavorResponse<MenuItemResponseDTO> sfrResponse = new SyntaxFlavorResponse<>();
        try {
            MenuItemResponseDTO menuItemResponseDTO = menuBL.enableMenuItem(id);
            sfrResponse.setResponseCode("MEN-005");
            sfrResponse.setPayload(menuItemResponseDTO);
            LOGGER.info("Menu item {} enabled successfully", id);
            return ResponseEntity.ok(sfrResponse);
        } catch (Exception e) {
            LOGGER.error("Error enabling menu item: {}", e.getMessage());
            sfrResponse.setResponseCode("MEN-605");
            sfrResponse.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfrResponse);
        }
    }

    @Operation(summary = "Update menu item", description = "Update a menu item with the provided details, the image is optional")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "Menu item created successfully"),
            @ApiResponse(responseCode = "500", description = "Error creating menu item")
        }
    )
    @PutMapping("/menu/item/{id}")
    public ResponseEntity<SyntaxFlavorResponse<MenuItemResponseDTO>> updateMenuItem(
            @PathVariable Integer id,
            @RequestBody MenuItemRequestDTO menuItemDetails) {
        LOGGER.info("Endpoint PUT /api/v1/menu/item/{}", id);
        SyntaxFlavorResponse<MenuItemResponseDTO> sfrResponse = new SyntaxFlavorResponse<>();
        try {
            MenuItemResponseDTO updatedMenuItem = menuBL.updateMenuItem(id, menuItemDetails);
            sfrResponse.setResponseCode("MEN-002");
            sfrResponse.setPayload(updatedMenuItem);
            LOGGER.info("Menu item {} updated successfully", id);
            return ResponseEntity.ok(sfrResponse);
        } catch (Exception e) {
            LOGGER.error("Error updating menu item: {}", e.getMessage());
            sfrResponse.setResponseCode("MEN-601");
            sfrResponse.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfrResponse);
        }
    }

    // deleting a menu item
    @Operation(summary = "Delete menu item", description = "Deletes a menu item by id")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Menu item deleted successfully"),
            @ApiResponse(responseCode = "500", description = "Error deleting menu item")
        }
    )
    @DeleteMapping("/menu/item/{id}")
    public ResponseEntity<SyntaxFlavorResponse<MenuItemResponseDTO>> deleteMenuItem(
            @PathVariable Integer id,
            HttpServletRequest request) {
        LOGGER.info("Endpoint DELETE /api/v1/menu/item/{}", id);

        String kcUserId = (String) request.getAttribute("kcUserId");
        User user = userBL.findUserByKcUserId(kcUserId);
        if (user == null) {
            LOGGER.error("User with kcUserId {} not found", kcUserId);
            SyntaxFlavorResponse<MenuItemResponseDTO> sfrResponse = new SyntaxFlavorResponse<>();
            sfrResponse.setResponseCode("ORD-601");
            sfrResponse.setErrorMessage("User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(sfrResponse);
        }

        SyntaxFlavorResponse<MenuItemResponseDTO> sfrResponse = new SyntaxFlavorResponse<>();
        try {
            Boolean resp = menuBL.deleteMenuItem(id);
            if(resp){
                LOGGER.info("Menu item {} deleted successfully", id);
            }
            sfrResponse.setResponseCode("MEN-005");
            sfrResponse.setPayload(null);
            LOGGER.info("Menu item {} deleted successfully", id);
            return ResponseEntity.ok(sfrResponse);
        } catch (ResponseStatusException e) {
            // Manejo de conflicto por relaciones existentes (código 409)
            LOGGER.warn("Conflict deleting menu item with id {}: {}", id, e.getReason());
            sfrResponse.setResponseCode("MEN-409");  // Código de error personalizado
            sfrResponse.setErrorMessage(e.getReason());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(sfrResponse);
        }catch (Exception e) {
            LOGGER.error("Error deleting menu item: {}", e.getMessage());
            sfrResponse.setResponseCode("MEN-605");
            sfrResponse.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfrResponse);
        }
    }

}
