package com.recipe_project.recipe.controller;

import com.recipe_project.recipe.dto.RecipeDTO;
import com.recipe_project.recipe.service.RecipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/api/recipes")
@Validated
public class RecipeController {

    private static final Logger logger = LoggerFactory.getLogger(RecipeController.class);

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
        logger.info("RecipeController initialized");
    }    @GetMapping("/search")
    public ResponseEntity<List<RecipeDTO>> searchRecipes(@RequestParam(name = "q", required = false) String query) {
        logger.info("Searching recipes with query: {}", query);
        List<RecipeDTO> recipes = recipeService.searchRecipes(query);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable Long id) {
        logger.info("Fetching recipe with ID: {}", id);
        RecipeDTO recipe = recipeService.getRecipeById(id);
        if (recipe == null) {
            logger.warn("Recipe with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recipe);
    }    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getAllRecipes() {
        logger.info("Fetching all recipes");
        List<RecipeDTO> recipes = recipeService.getAllRecipes();
        return ResponseEntity.ok(recipes);
    }

    @PostMapping("/load")
    public ResponseEntity<Map<String, Object>> loadRecipesFromApi() {
        logger.info("Manual trigger to load recipes from external API");
        try {
            int loadedCount = recipeService.loadRecipesFromExternalApi();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully loaded recipes from external API");
            response.put("count", loadedCount);
            logger.info("Successfully loaded {} recipes", loadedCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to load recipes from external API: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Failed to load recipes");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
