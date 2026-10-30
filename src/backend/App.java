import io.javalin.Javalin;
import javax.sql.DataSource;

import controllers.OrderController;
import services.OrderService;
import repository.OrderRepository;
import database.Database;

public class App {
    public static void main(String[] args) {
        DataSource pool = Database.dataSource();

        OrderRepository orderRepository = new OrderRepository(pool);

        OrderService orderService = new OrderService(orderRepository);

        OrderController orderController = new OrderController(orderService);

        Javalin app = Javalin.create(config -> {
            orderController.registerRoutes(config);
        });

        app.start(8080);
    }
}
