package services;

import models.MenuItem;
import repository.MenuRepository;

import java.sql.SQLException;
import java.util.List;

public class MenuService {
    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public MenuItem createMenuItem(MenuItem menuItem) throws SQLException {
        return menuRepository.insertMenuItem(menuItem);
    }

    public MenuItem getMenuItemByID(int id) throws SQLException {
        return menuRepository.getMenuItemByID(id);
    }

    public List<MenuItem> getMenuItems() throws SQLException {
        return menuRepository.getMenuItems();
    }

    public boolean updateMenuItem(int id, MenuItem menuItem) throws SQLException {
        return menuRepository.updateMenuItem(id, menuItem);
    }

    public boolean deleteMenuItem(int id) throws SQLException {
        return menuRepository.deleteMenuItem(id);
    }
}
