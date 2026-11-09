package repository;

import models.MenuItem;
import models.MenuItemType;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuRepository {
    private static final String COLUMNS = "menu_item_id, menu_item_name::text AS menu_item_name, "
            + "premium_charge, active, menu_item_type::text AS menu_item_type";

    private final DataSource pool;

    public MenuRepository(DataSource pool) {
        this.pool = pool;
    }

    public MenuItem insertMenuItem(MenuItem menuItem) throws SQLException {
        try (Connection conn = pool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO menu_items (menu_item_name, premium_charge, active, menu_item_type) "
                            + "VALUES (?, COALESCE(?, 0), COALESCE(?, TRUE), ?::menu_item_type) "
                            + "RETURNING " + COLUMNS);
            stmt.setString(1, menuItem.menuItemName());
            stmt.setObject(2, menuItem.premiumCharge(), Types.DOUBLE);
            stmt.setObject(3, menuItem.isActive(), Types.BOOLEAN);
            MenuItemType type = menuItem.menuItemType();
            stmt.setString(4, type == null ? null : type.dbValue());
            return query(stmt).get(0);
        }
    }

    public MenuItem getMenuItemByID(int id) throws SQLException {
        try (Connection conn = pool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT " + COLUMNS + " FROM menu_items WHERE menu_item_id = ?");
            stmt.setInt(1, id);
            List<MenuItem> menuItems = query(stmt);
            return menuItems.isEmpty() ? null : menuItems.get(0);
        }
    }

    public List<MenuItem> getMenuItems() throws SQLException {
        try (Connection conn = pool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT " + COLUMNS + " FROM menu_items ORDER BY menu_item_name");
            return query(stmt);
        }
    }

    // Fields left null in the request keep their current value
    public boolean updateMenuItem(int id, MenuItem menuItem) throws SQLException {
        try (Connection conn = pool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE menu_items SET menu_item_name = COALESCE(?, menu_item_name), "
                            + "premium_charge = COALESCE(?, premium_charge), "
                            + "active = COALESCE(?, active), "
                            + "menu_item_type = COALESCE(?::menu_item_type, menu_item_type) "
                            + "WHERE menu_item_id = ?");
            stmt.setString(1, menuItem.menuItemName());
            stmt.setObject(2, menuItem.premiumCharge(), Types.DOUBLE);
            stmt.setObject(3, menuItem.isActive(), Types.BOOLEAN);
            MenuItemType type = menuItem.menuItemType();
            stmt.setString(4, type == null ? null : type.dbValue());
            stmt.setInt(5, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteMenuItem(int id) throws SQLException {
        try (Connection conn = pool.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE menu_items SET active = FALSE WHERE menu_item_id = ?");
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private List<MenuItem> query(PreparedStatement stmt) throws SQLException {
        List<MenuItem> menuItems = new ArrayList<>();
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            menuItems.add(new MenuItem(
                    rs.getInt("menu_item_id"),
                    rs.getString("menu_item_name"),
                    rs.getDouble("premium_charge"),
                    rs.getBoolean("active"),
                    MenuItemType.fromDb(rs.getString("menu_item_type"))));
        }
        stmt.close();
        return menuItems;
    }
}
