import com.google.firebase.FirebaseApp;
import io.javalin.Javalin;
import java.util.Arrays;
import javax.sql.DataSource;

import controllers.IngredientsController;
import controllers.OrderController;
import controllers.MenuController;
import controllers.UsersController;
import database.Database;
import repository.IngredientRepository;
import repository.MenuRepository;
import repository.OrderRepository;
import repository.UsersRepository;
import services.IngredientService;
import services.MenuService;
import services.OrderService;
import services.UsersService;

public class App {
    public static void main(String[] args) {
        DataSource pool = null;

        try {
            pool = Database.dataSource();
        } catch (ExceptionInInitializerError e) {
            System.err.println("Error connecting to database: " + e.getCause().getMessage());
            System.exit(1);
        }

        /*try {
            FirebaseApp.initializeApp();
        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            System.err.println("Error initializing FirebaseApp: " + cause.getMessage());
            System.exit(1);
        }*/

        OrderRepository orderRepository = new OrderRepository(pool);
        OrderService orderService = new OrderService(orderRepository);
        OrderController orderController = new OrderController(orderService);

        MenuRepository menuRepository = new MenuRepository(pool);
        MenuService menuService = new MenuService(menuRepository);
        MenuController menuController = new MenuController(menuService);

        IngredientRepository ingredientRepository = new IngredientRepository(pool);
        IngredientService ingredientService = new IngredientService(ingredientRepository);
        IngredientsController ingredientsController = new IngredientsController(ingredientService);

        UsersRepository usersRepository = new UsersRepository(pool);
        UsersService usersService = new UsersService(usersRepository);
        UsersController usersController = new UsersController(usersService);

        Javalin app = Javalin.create(config -> {
            orderController.registerRoutes(config);
            menuController.registerRoutes(config);
            ingredientsController.registerRoutes(config);
            usersController.registerRoutes(config);
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
