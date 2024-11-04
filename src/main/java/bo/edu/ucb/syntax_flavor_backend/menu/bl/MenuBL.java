package bo.edu.ucb.syntax_flavor_backend.menu.bl;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

import bo.edu.ucb.syntax_flavor_backend.menu.dto.MenuItemRequestDTO;
import bo.edu.ucb.syntax_flavor_backend.menu.dto.MenuItemResponseDTO;
import bo.edu.ucb.syntax_flavor_backend.menu.entity.MenuItem;
import bo.edu.ucb.syntax_flavor_backend.menu.repository.MenuItemRepository;
import bo.edu.ucb.syntax_flavor_backend.service.MinioFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MenuBL {
    Logger LOGGER = LoggerFactory.getLogger(MenuBL.class);
    private static final BigDecimal MAX_MENU_ITEMS_PRICE = BigDecimal.valueOf(99999.9);

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MinioFileService minioFileService;

    public List<MenuItem> getMenuItemsFromIds(Set<Integer> menuItemsIdList) {
        LOGGER.info("Getting menu items from ids: {}", menuItemsIdList);
        List<MenuItem> menuItems = menuItemRepository.findAllById(menuItemsIdList);
        return menuItems;
    }

    public List<MenuItemResponseDTO> getMenuItems() throws RuntimeException {
        LOGGER.info("Getting all menu items");
        List<MenuItemResponseDTO> menuItemsResponse;
        try {
            List<MenuItem> menuItems = menuItemRepository.findAllByOrderByNameAsc();
            menuItemsResponse = menuItems.stream()
                    .map(menuItem -> new MenuItemResponseDTO(menuItem))
                    .collect(Collectors.toList());
            return menuItemsResponse;
        } catch (Exception e) {
            LOGGER.error("Error getting all menu items: {}", e.getMessage());
            throw new RuntimeException("Error getting all menu items: " + e.getMessage());
        }
    }

    public Page<MenuItemResponseDTO> getMenuItemsByPriceSortByName(BigDecimal minPrice, BigDecimal maxPrice, Integer pageNumber, Integer pageSize, boolean sortAScending) {
        LOGGER.info("Getting menu items by price between {} and {}", minPrice, maxPrice);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<MenuItem> menuItems;
        if (minPrice == null) {
            minPrice = BigDecimal.ZERO;
        }
        if (maxPrice == null) {
            maxPrice = MAX_MENU_ITEMS_PRICE;
        }
        if (!sortAScending) {
            menuItems = menuItemRepository.findByPriceBetweenOrderByNameDesc(minPrice, maxPrice, pageable);
        } else {
            menuItems = menuItemRepository.findByPriceBetweenOrderByNameAsc(minPrice, maxPrice, pageable);
        }
        LOGGER.info("Found {} menu items", menuItems.getTotalElements());
        return menuItems.map(MenuItemResponseDTO::new);
    }

    public String updateMenuItemImage(Integer id, MultipartFile file) {
        LOGGER.info("Updating menu item image for id: {}", id);
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty. Please provide a valid file.");
            }
            String contentType = file.getContentType();
            if (!isImage(contentType)) {
                throw new IllegalArgumentException("Invalid file type. Please upload an image.");
            }
            MenuItem menuItem = menuItemRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Menu item not found for id: " + id));
            String fileName = "menu_items/images/" + id + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String imageUrl = minioFileService.uploadFile(fileName, file.getBytes(), contentType);
            menuItem.setImageUrl(imageUrl);
            menuItemRepository.save(menuItem);
            LOGGER.info("Menu item image updated successfully for id: {}", id);
            return imageUrl;
        } catch (Exception e) {
            LOGGER.error("Error updating menu item image: {}", e.getMessage());
            throw new RuntimeException("Error updating menu item image: " + e.getMessage(), e);
        }
    }

    private boolean isImage(String contentType) {
        return contentType.equals("image/jpeg") || contentType.equals("image/png");
    }

    public byte[] getMenuItemImage(Integer id) {
        LOGGER.info("Getting menu item image for id: {}", id);
        try {
            MenuItem menuItem = menuItemRepository.findById(id).orElseThrow(() -> new RuntimeException("Menu item not found"));
            if (menuItem.getImageUrl() == null) throw new RuntimeException("Menu item does not have an image");
            String objectKey = "menu_items/images/" + menuItem.getId() + "/" + menuItem.getImageUrl().substring(menuItem.getImageUrl().lastIndexOf("/"));
            LOGGER.info("Getting image from Minio with object key: {}", objectKey);
            return minioFileService.getFile(objectKey);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error getting menu item image: {}", e.getMessage());
            throw new RuntimeException("Error getting menu item image: " + e.getMessage());
        }
    }

    public MenuItemResponseDTO disableMenuItem(Integer id) {
        LOGGER.info("Disabling menu item with id: {}", id);
        try {
            MenuItem menuItem = menuItemRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Menu item not found for id: " + id));
            menuItem.setStatus(false);
            menuItemRepository.save(menuItem);
            LOGGER.info("Menu item disabled successfully for id: {}", id);
            return new MenuItemResponseDTO(menuItem); // Return the updated menu item details
        } catch (Exception e) {
            LOGGER.error("Error disabling menu item: {}", e.getMessage());
            throw new RuntimeException("Error disabling menu item: " + e.getMessage(), e);
        }
    }

    public MenuItemResponseDTO enableMenuItem(Integer id) {
        LOGGER.info("Enabling menu item with id: {}", id);
        try {
            MenuItem menuItem = menuItemRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Menu item not found for id: " + id));
            menuItem.setStatus(true);
            menuItemRepository.save(menuItem);
            LOGGER.info("Menu item enabled successfully for id: {}", id);
            return new MenuItemResponseDTO(menuItem); // Return the updated menu item details
        } catch (Exception e) {
            LOGGER.error("Error enabling menu item: {}", e.getMessage());
            throw new RuntimeException("Error enabling menu item: " + e.getMessage(), e);
        }
    }

    public MenuItemResponseDTO createMenuItem(MenuItemRequestDTO menuItemRequest) {
        LOGGER.info("Creating menu item: {}", menuItemRequest);
        try {
            MenuItem menuItem = new MenuItem();
            menuItem.setName(menuItemRequest.getName());
            menuItem.setDescription(menuItemRequest.getDescription());
            menuItem.setPrice(menuItemRequest.getPrice());
            menuItem = menuItemRepository.save(menuItem);
            LOGGER.info("Menu item created successfully with id: {}", menuItem.getId());
            return new MenuItemResponseDTO(menuItem);
        } catch (Exception e) {
            LOGGER.error("Error creating menu item: {}", e.getMessage());
            throw new RuntimeException("Error creating menu item: " + e.getMessage(), e);
        }
    }

    public MenuItemResponseDTO updateMenuItem(Integer id, MenuItemRequestDTO menuItemRequest) {
        LOGGER.info("Updating menu item with id: {}", id);
        try {
            MenuItem menuItem = menuItemRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Menu item not found for id: " + id));
            menuItem.setName(menuItemRequest.getName());
            menuItem.setDescription(menuItemRequest.getDescription());
            menuItem.setPrice(menuItemRequest.getPrice());    
            menuItem.setUpdatedAt(new Date()); // Actualiza la fecha de modificación
            menuItem = menuItemRepository.save(menuItem);
            LOGGER.info("Menu item updated successfully for id: {}", id);
            return new MenuItemResponseDTO(menuItem);
        } catch (Exception e) {
            LOGGER.error("Error updating menu item: {}", e.getMessage());
            throw new RuntimeException("Error updating menu item: " + e.getMessage(), e);
        }
    }
}
