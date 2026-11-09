package controllers;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import models.MenuItem;
import models.MenuItemType;
import services.MenuService;

import java.sql.SQLException;
import java.util.List;

public class MenuController {
    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    public void registerRoutes(JavalinConfig config) {
        config.routes.post("/menu", this::createMenuItem);
        config.routes.get("/menu/{menuItemID}", this::getMenuItemByID);
        config.routes.get("/menu", this::getMenuItems);
        config.routes.put("/menu/{menuItemID}", this::updateMenuItem);
        config.routes.delete("/menu/{menuItemID}", this::deleteMenuItem);
    }

    // POST /menu
    public void createMenuItem(Context ctx) throws SQLException {
        MenuItem menuItem = ctx.bodyAsClass(MenuItem.class);

        MenuItem created = menuService.createMenuItem(menuItem);
        ctx.status(201).json(created);
    }

    // GET /menu/{menuItemID}
    public void getMenuItemByID(Context ctx) throws SQLException {
        int menuItemID = ctx.pathParamAsClass("menuItemID", Integer.class).get();

        MenuItem menuItem = menuService.getMenuItemByID(menuItemID);
        if (menuItem == null) {
            ctx.status(404).result("Menu Item not found");
            return;
        }
        ctx.json(menuItem);
    }

    // GET /menu
    public void getMenuItems(Context ctx) throws SQLException {
        List<MenuItem> menuItems = menuService.getMenuItems();
        ctx.json(menuItems);
    }

    // PUT /menu/{menuItemID}
    public void updateMenuItem(Context ctx) throws SQLException {
        int menuItemID = ctx.pathParamAsClass("menuItemID", Integer.class).get();
        MenuItem menuItem = ctx.bodyAsClass(MenuItem.class);

        boolean updated = menuService.updateMenuItem(menuItemID, menuItem);
        if (!updated) {
            ctx.status(404).result("Menu item not found");
            return;
        }
        ctx.status(200).result("Menu item updated");
    }

    // DELETE /menu/{menuItemID}
    public void deleteMenuItem(Context ctx) throws SQLException {
        int menuItemID = ctx.pathParamAsClass("menuItemID", Integer.class).get();

        boolean deleted = menuService.deleteMenuItem(menuItemID);
        if (!deleted) {
            ctx.status(404).result("Menu item not found");
            return;
        }
        ctx.status(204);
    }
}

