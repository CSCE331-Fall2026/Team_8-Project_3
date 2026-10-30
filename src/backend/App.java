import java.sql.Connection;

import io.javalin.Javalin;

import controllers.OrderController;
import database.Database;
import repository.OrderRepository;
import services.OrderService;

public class App {
    public static void main(String[] args) {
        Connection conn = new Database().connectDb();
        if (conn == null) {
            System.err.println("Could not connect to the database, shutting down.");
            System.exit(1);
        }

        OrderRepository orderRepository = new OrderRepository(conn);
        OrderService orderService = new OrderService(orderRepository);
        OrderController orderController = new OrderController(orderService);

        Javalin.create(config -> {
            config.routes.get("/orders", orderController::getOrders);
            config.routes.post("/orders", orderController::placeOrder);
            config.routes.get("/orders/{orderID}", orderController::getOrder);
            config.routes.put("/orders/{orderID}", orderController::updateOrder);
            config.routes.delete("/orders/{orderID}", orderController::deleteOrder);
        }).start(8080);
    }
}
