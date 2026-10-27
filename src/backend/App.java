import io.javalin.Javalin;

import controllers.CashierController;
import services.CashierService;
import database.Database;

public class App {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            cashierController.registerRoutes(config);
        }).start(8080);
    }
}

// TODO: Register Routes, Connect database