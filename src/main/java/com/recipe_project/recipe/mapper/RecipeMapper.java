package com.recipe_project.recipe.mapper;

import com.recipe_project.recipe.dto.RecipeDTO;
import com.recipe_project.recipe.entity.Recipe;
import org.springframework.stereotype.Component;

@Component
public class RecipeMapper {

    public RecipeDTO toDTO(Recipe recipe){
        if (recipe == null) {
            return null;
        }
        return RecipeDTO.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .cuisine(recipe.getCuisine())
                .tags(recipe.getTags())
                .ingredients(recipe.getIngredients())
                .instructions(recipe.getInstructions())
                .cookTimeMinutes(recipe.getCookTimeMinutes())
                .prepTimeMinutes(recipe.getPrepTimeMinutes())
                .servings(recipe.getServings())
                .difficulty(recipe.getDifficulty())
                .rating(recipe.getRating())
                .reviewCount(recipe.getReviewCount())
                .caloriesPerServing(recipe.getCaloriesPerServing())
                .userId(recipe.getUserId())
                .mealType(recipe.getMealType())
                .image(recipe.getImage())                .build();
    }

    public Recipe toEntity(RecipeDTO recipeDTO) {
        if (recipeDTO == null) {
            return null;
        }

        return Recipe.builder()
                .id(recipeDTO.getId())
                .name(recipeDTO.getName())
                .cuisine(recipeDTO.getCuisine())
                .tags(recipeDTO.getTags())
                .ingredients(recipeDTO.getIngredients())
                .instructions(recipeDTO.getInstructions())
                .cookTimeMinutes(recipeDTO.getCookTimeMinutes())
                .prepTimeMinutes(recipeDTO.getPrepTimeMinutes())
                .servings(recipeDTO.getServings())
                .difficulty(recipeDTO.getDifficulty())
                .rating(recipeDTO.getRating())                .reviewCount(recipeDTO.getReviewCount())
                .caloriesPerServing(recipeDTO.getCaloriesPerServing())
                .userId(recipeDTO.getUserId())
                .mealType(recipeDTO.getMealType())
                .image(recipeDTO.getImage())
                .build();
    }
}
}
