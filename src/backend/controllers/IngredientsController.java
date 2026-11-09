package controllers;

import java.sql.SQLException;
import java.util.List;

import io.javalin.http.Context;
import io.javalin.config.JavalinConfig;

import models.Ingredient;
import services.IngredientService;

public class IngredientsController {
    private static final String FOREIGN_KEY_VIOLATION = "23503";

    private final IngredientService ingredientService;

    public IngredientsController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    public void registerRoutes(JavalinConfig config) {
        config.routes.post("/ingredients", this::createIngredient);
        config.routes.get("/ingredients", this::getIngredients);
        config.routes.get("/ingredients/{ingredientID}", this::getIngredientByID);
        config.routes.put("/ingredients/{ingredientID}", this::updateIngredient);
        config.routes.delete("/ingredients/{ingredientID}", this::deleteIngredient);
    }

    // POST /ingredients
    public void createIngredient(Context ctx) throws SQLException {
        Ingredient ingredient = ctx.bodyAsClass(Ingredient.class);

        Ingredient created = ingredientService.createIngredient(ingredient);
        ctx.status(201).json(created);
    }

    // GET /ingredients
    public void getIngredients(Context ctx) throws SQLException {
        List<Ingredient> ingredients = ingredientService.getAllIngredients();
        ctx.json(ingredients);
    }

    // GET /ingredients/{ingredientID}
    public void getIngredientByID(Context ctx) throws SQLException {
        int ingredientID = ctx.pathParamAsClass("ingredientID", Integer.class).get();

        Ingredient ingredient = ingredientService.getIngredient(ingredientID);
        if (ingredient == null) {
            ctx.status(404).result("Ingredient not found");
            return;
        }
        ctx.json(ingredient);
    }

    // PUT /ingredients/{ingredientID}
    public void updateIngredient(Context ctx) throws SQLException {
        int ingredientID = ctx.pathParamAsClass("ingredientID", Integer.class).get();
        Ingredient ingredient = ctx.bodyAsClass(Ingredient.class);

        boolean updated = ingredientService.updateIngredient(ingredientID, ingredient);
        if (!updated) {
            ctx.status(404).result("Ingredient not found");
            return;
        }
        ctx.status(200).result("Ingredient updated");
    }

    // DELETE /ingredients/{ingredientID}
    public void deleteIngredient(Context ctx) throws SQLException {
        int ingredientID = ctx.pathParamAsClass("ingredientID", Integer.class).get();

        boolean deleted;
        try {
            deleted = ingredientService.deleteIngredient(ingredientID);
        } catch (SQLException e) {
            if (FOREIGN_KEY_VIOLATION.equals(e.getSQLState())) {
                ctx.status(409).result("Ingredient is used by one or more menu items");
                return;
            }
            throw e;
        }
        if (!deleted) {
            ctx.status(404).result("Ingredient not found");
            return;
        }
        ctx.status(204);
    }
}
