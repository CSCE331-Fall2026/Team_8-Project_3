import io.javalin.Javalin;

import controllers.OrderController;
import services.OrderService;
import database.Database;

public class App {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            OrderController.registerRoutes(config);
        }).start(8080);
    }
}

// TODO: Register Routes, Connect database