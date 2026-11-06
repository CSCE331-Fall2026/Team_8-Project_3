import io.javalin.Javalin;
import javax.sql.DataSource;

import controllers.OrderController;
import database.Database;
import repository.OrderRepository;
import services.OrderService;

public class App {
    public static void main(String[] args) {
        DataSource pool = null;

        try {
            pool = Database.dataSource();
        } catch (ExceptionInInitializerError e) {
            System.err.println("Error connecting to database: " + e.getCause().getMessage());
            System.exit(1);
        }

        OrderRepository orderRepository = new OrderRepository(pool);
        OrderService orderService = new OrderService(orderRepository);
        OrderController orderController = new OrderController(orderService);

        Javalin app = Javalin.create(config -> {
            orderController.registerRoutes(config);
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(rule -> rule.allowHost("http://localhost:5173"));
            });
        });

        app.start(8080);
    }
}
