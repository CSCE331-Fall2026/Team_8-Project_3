import io.javalin.Javalin;
import javax.sql.DataSource;

import controllers.IngredientsController;
import controllers.OrderController;
import database.Database;
import repository.IngredientRepository;
import repository.OrderRepository;
import services.IngredientService;
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

        IngredientRepository ingredientRepository = new IngredientRepository(pool);
        IngredientService ingredientService = new IngredientService(ingredientRepository);
        IngredientsController ingredientsController = new IngredientsController(ingredientService);

        Javalin app = Javalin.create(config -> {
            orderController.registerRoutes(config);
            ingredientsController.registerRoutes(config);
        });

        app.start(8080);
    }
}
