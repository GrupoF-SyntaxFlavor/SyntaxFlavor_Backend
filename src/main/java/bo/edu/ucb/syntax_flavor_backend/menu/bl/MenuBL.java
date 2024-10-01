package bo.edu.ucb.syntax_flavor_backend.menu.bl;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;

import bo.edu.ucb.syntax_flavor_backend.menu.dto.MenuItemResponseDTO;
import bo.edu.ucb.syntax_flavor_backend.menu.entity.MenuItem;
import bo.edu.ucb.syntax_flavor_backend.menu.repository.MenuItemRepository;
import bo.edu.ucb.syntax_flavor_backend.service.MinioFileService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MenuBL {
    
    Logger LOGGER = LoggerFactory.getLogger(MenuBL.class);

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MinioFileService minioFileService;

    public List<MenuItem> getMenuItemsFromIds(Set<Integer> menuItemsIdList) {
        LOGGER.info("Getting menu items from ids: {}", menuItemsIdList);
        List<MenuItem> menuItems = menuItemRepository.findAllById(menuItemsIdList);
        return menuItems;
    }

    public List<MenuItemResponseDTO> getMenuItems()  throws RuntimeException{
        LOGGER.info("Getting all menu items");
        List<MenuItemResponseDTO> menuItemsResponse;
        try {
            List<MenuItem> menuItems = menuItemRepository.findAllByOrderByNameAsc();
            // Mapear los MenuItems a MenuItemResponseDTOs
            menuItemsResponse = menuItems.stream()
                .map(menuItem -> new MenuItemResponseDTO(menuItem))
                .collect(Collectors.toList()
                );
            return menuItemsResponse;
        } catch (Exception e) {
            LOGGER.error("Error getting all menu items: {}", e.getMessage());
            throw new RuntimeException("Error getting all menu items: " + e.getMessage());
        }
    }

    public String updateMenuItemImage(Integer id, MultipartFile file) {
        LOGGER.info("Updating menu item image for id: {}", id);
        try {
            // Verificar que el archivo no esté vacío
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty. Please provide a valid file.");
            }

            // Validar que el archivo sea una imagen
            String contentType = file.getContentType();
            if (!isImage(contentType)) {
                throw new IllegalArgumentException("Invalid file type. Please upload an image.");
            }

            // Obtener el elemento del menú por ID
            MenuItem menuItem = menuItemRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Menu item not found for id: " + id));

            // Generar un nombre de archivo único
            String fileName = "menu_items/images/" + id + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // Subir la imagen a Minio y obtener la URL
            String imageUrl = minioFileService.uploadFile(fileName, file.getBytes(), contentType);

            // Actualizar la URL de la imagen en el objeto MenuItem
            menuItem.setImageUrl(imageUrl);
            menuItemRepository.save(menuItem);

            LOGGER.info("Menu item image updated successfully for id: {}", id);
            return imageUrl;
        } catch (Exception e) {
            LOGGER.error("Error updating menu item image: {}", e.getMessage());
            throw new RuntimeException("Error updating menu item image: " + e.getMessage(), e);
        }
    }

    // Método auxiliar para validar si el tipo de contenido es una imagen
    private boolean isImage(String contentType) {
        return contentType.equals("image/jpeg") || contentType.equals("image/png");
    }


    public byte[] getMenuItemImage(Integer id) {
        LOGGER.info("Getting menu item image for id: {}", id);
        try {
            MenuItem menuItem = menuItemRepository.findById(id).orElseThrow(() -> new RuntimeException("Menu item not found"));
            if(menuItem.getImageUrl() == null) throw new RuntimeException("Menu item does not have an image");
            return minioFileService.getFile(menuItem.getImageUrl());
        } catch (Exception e) {
            LOGGER.error("Error getting menu item image: {}", e.getMessage());
            throw new RuntimeException("Error getting menu item image: " + e.getMessage());
        }
    }
}
