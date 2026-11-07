package services;

import java.sql.SQLException;
import java.util.List;

import models.Ingredient;
import repository.IngredientRepository;

public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public Ingredient createIngredient(Ingredient ingredient) throws SQLException {
        return ingredientRepository.createIngredient(ingredient);
    }

    public Ingredient getIngredient(int id) throws SQLException {
        return ingredientRepository.getIngredientById(id);
    }

    public List<Ingredient> getAllIngredients() throws SQLException {
        return ingredientRepository.getAllIngredients();
    }

    public boolean updateIngredient(int id, Ingredient ingredient) throws SQLException {
        return ingredientRepository.updateIngredient(id, ingredient);
    }

    public boolean deleteIngredient(int id) throws SQLException {
        return ingredientRepository.deleteIngredient(id);
    }
}
