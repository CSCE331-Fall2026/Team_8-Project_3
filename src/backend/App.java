import io.javalin.Javalin;
import java.util.Arrays;
import javax.sql.DataSource;

import controllers.IngredientsController;
import controllers.OrderController;
import controllers.MenuController;
import database.Database;
import repository.InMemoryIngredientRepository;
import repository.InMemoryMenuRepository;
import repository.InMemoryOrderRepository;
import repository.IngredientRepository;
import repository.MenuRepository;
import repository.OrderRepository;
import services.IngredientService;
import services.MenuService;
import services.OrderService;

public class App {
    public static void main(String[] args) {
        OrderRepository orderRepository;
        MenuRepository menuRepository;
        IngredientRepository ingredientRepository;

        if ("true".equalsIgnoreCase(System.getenv("IN_MEMORY_DB")) || !Database.isConfigured()) {
            System.out.println("No database configured: using in-memory sample data. Changes are lost on restart.");
            orderRepository = new InMemoryOrderRepository();
            menuRepository = new InMemoryMenuRepository();
            ingredientRepository = new InMemoryIngredientRepository();
        } else {
            DataSource pool = null;
            try {
                pool = Database.dataSource();
            } catch (RuntimeException e) {
                System.err.println("Error connecting to database: " + e.getMessage());
                System.exit(1);
            }
            orderRepository = new OrderRepository(pool);
            menuRepository = new MenuRepository(pool);
            ingredientRepository = new IngredientRepository(pool);
        }

        OrderService orderService = new OrderService(orderRepository);
        OrderController orderController = new OrderController(orderService);

        MenuService menuService = new MenuService(menuRepository);
        MenuController menuController = new MenuController(menuService);

        IngredientService ingredientService = new IngredientService(ingredientRepository);
        IngredientsController ingredientsController = new IngredientsController(ingredientService);

        Javalin app = Javalin.create(config -> {
            orderController.registerRoutes(config);
            menuController.registerRoutes(config);
            ingredientsController.registerRoutes(config);
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(rule -> rule.allowHost("http://localhost:5173", allowedOrigins()));
            });
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            app.stop();
            Database.close();
        }));

        // Render supplies PORT and requires binding to 0.0.0.0
        String port = System.getenv("PORT");
        app.start("0.0.0.0", port == null || port.isBlank() ? 8080 : Integer.parseInt(port));
    }

    // Comma-separated deployed frontend origins, e.g. https://team8.onrender.com
    private static String[] allowedOrigins() {
        String origins = System.getenv("CORS_ORIGINS");
        if (origins == null || origins.isBlank()) {
            return new String[0];
        }
        return Arrays.stream(origins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);
    }
}
