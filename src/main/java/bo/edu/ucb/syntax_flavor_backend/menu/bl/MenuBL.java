package bo.edu.ucb.syntax_flavor_backend.menu.bl;

import java.util.Set;
import java.util.List;

import bo.edu.ucb.syntax_flavor_backend.menu.entity.MenuItem;
import bo.edu.ucb.syntax_flavor_backend.menu.repository.MenuItemRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MenuBL {
    
    Logger LOGGER = LoggerFactory.getLogger(MenuBL.class);

    @Autowired
    private MenuItemRepository menuItemRepository;

    public List<MenuItem> getMenuItemsFromIds(Set<Integer> menuItemsIdList) {
        LOGGER.info("Getting menu items from ids: {}", menuItemsIdList);
        List<MenuItem> menuItems = menuItemRepository.findAllById(menuItemsIdList);
        return menuItems;
    }
}
