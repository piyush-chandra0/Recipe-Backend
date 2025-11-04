package com.recipe_project.recipe.config;

import com.recipe_project.recipe.service.RecipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private final RecipeService recipeService;
    
    public DataInitializer(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        logger.info("Starting data initialization from external API");
        try {
            int loadedRecipes = recipeService.loadRecipesFromExternalApi();
            logger.info("Successfully initialized {} recipes from external API", loadedRecipes);
        } catch (Exception e) {
            logger.error("Error loading recipes during startup: {}", e.getMessage(), e);
            // Don't fail startup if external API is unavailable
        }
    }


}
