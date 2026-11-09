import io.javalin.Javalin;
import javax.sql.DataSource;

import controllers.OrderController;
import controllers.MenuController;
import database.Database;
import repository.MenuRepository;
import repository.OrderRepository;
import services.MenuService;
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

        MenuRepository menuRepository = new MenuRepository(pool);
        MenuService menuService = new MenuService(menuRepository);
        MenuController menuController = new MenuController(menuService);

        Javalin app = Javalin.create(config -> {
            orderController.registerRoutes(config);
            menuController.registerRoutes(config);
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(rule -> rule.allowHost("http://localhost:5173"));
            });
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            app.stop();
            Database.close();
        }));

        app.start(8080);
    }
}
