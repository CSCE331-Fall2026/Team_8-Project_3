import io.javalin.Javalin;
import javax.sql.DataSource;

import controllers.OrderController;
import database.Database;
import repository.OrderRepository;
import services.OrderService;

public class App {
    public static void main(String[] args) {
        DataSource pool = Database.dataSource();

        if (pool == null) {
            System.err.println("Could not connect to the database, shutting down.");
            System.exit(1);
        }

        OrderRepository orderRepository = new OrderRepository(pool);
        OrderService orderService = new OrderService(orderRepository);
        OrderController orderController = new OrderController(orderService);

        Javalin app = Javalin.create(config -> {
            config.routes.get("/orders", orderController::getOrders);
            config.routes.post("/orders", orderController::placeOrder);
            config.routes.get("/orders/{orderID}", orderController::getOrder);
            config.routes.put("/orders/{orderID}", orderController::updateOrder);
            config.routes.delete("/orders/{orderID}", orderController::deleteOrder);
        });

        app.start(8080);
    }
}
