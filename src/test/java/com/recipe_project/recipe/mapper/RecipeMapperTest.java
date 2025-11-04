package com.recipe_project.recipe.mapper;

import com.recipe_project.recipe.dto.RecipeDTO;
import com.recipe_project.recipe.entity.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RecipeMapperTest {

    private RecipeMapper recipeMapper;
    private Recipe testRecipeEntity;
    private RecipeDTO testRecipeDTO;

    @BeforeEach
    void setUp() {
        recipeMapper = new RecipeMapper();

        // Create test Recipe entity
        testRecipeEntity = Recipe.builder()
                .id(1L)
                .name("Test Recipe")
                .cuisine("Italian")
                .cookTimeMinutes(30)
                .prepTimeMinutes(15)
                .servings(4)
                .difficulty("Easy")
                .tags(List.of("tag1", "tag2"))
                .ingredients(List.of("ingredient1", "ingredient2"))
                .instructions(List.of("step1", "step2"))
                .image("test_image_url")
                .rating(4.5)
                .reviewCount(10)
                .caloriesPerServing(250)
                .userId(123)
                .mealType(List.of("Dinner"))
                .build();

        // Create test RecipeDTO
        testRecipeDTO = RecipeDTO.builder()
                .id(1L)
                .name("Test Recipe")
                .cuisine("Italian")
                .cookTimeMinutes(30)
                .prepTimeMinutes(15)
                .servings(4)
                .difficulty("Easy")
                .tags(List.of("tag1", "tag2"))
                .ingredients(List.of("ingredient1", "ingredient2"))
                .instructions(List.of("step1", "step2"))
                .image("test_image_url")
                .rating(4.5)
                .reviewCount(10)
                .caloriesPerServing(250)
                .userId(123)
                .mealType(List.of("Dinner"))
                .build();
    }

    @Test
    void toDTO_Success() {
        RecipeDTO result = recipeMapper.toDTO(testRecipeEntity);

        assertNotNull(result);
        assertEquals(testRecipeEntity.getId(), result.getId());
        assertEquals(testRecipeEntity.getName(), result.getName());
        assertEquals(testRecipeEntity.getCuisine(), result.getCuisine());
        assertEquals(testRecipeEntity.getCookTimeMinutes(), result.getCookTimeMinutes());
        assertEquals(testRecipeEntity.getPrepTimeMinutes(), result.getPrepTimeMinutes());
        assertEquals(testRecipeEntity.getServings(), result.getServings());
        assertEquals(testRecipeEntity.getDifficulty(), result.getDifficulty());
        assertEquals(testRecipeEntity.getTags(), result.getTags());
        assertEquals(testRecipeEntity.getIngredients(), result.getIngredients());
        assertEquals(testRecipeEntity.getInstructions(), result.getInstructions());
        assertEquals(testRecipeEntity.getImage(), result.getImage());
        assertEquals(testRecipeEntity.getRating(), result.getRating());
        assertEquals(testRecipeEntity.getReviewCount(), result.getReviewCount());
        assertEquals(testRecipeEntity.getCaloriesPerServing(), result.getCaloriesPerServing());
        assertEquals(testRecipeEntity.getUserId(), result.getUserId());
        assertEquals(testRecipeEntity.getMealType(), result.getMealType());
    }

    @Test
    void toDTO_NullInput() {
        RecipeDTO result = recipeMapper.toDTO(null);
        assertNull(result);
    }

    @Test
    void toDTO_PartialData() {
        Recipe partialRecipe = Recipe.builder()
                .id(2L)
                .name("Partial Recipe")
                .cuisine("Mexican")
                .build();

        RecipeDTO result = recipeMapper.toDTO(partialRecipe);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Partial Recipe", result.getName());
        assertEquals("Mexican", result.getCuisine());
        assertNull(result.getCookTimeMinutes());
        assertNull(result.getPrepTimeMinutes());
        assertNull(result.getServings());
        assertNull(result.getDifficulty());
        assertNull(result.getTags());
        assertNull(result.getIngredients());
        assertNull(result.getInstructions());
        assertNull(result.getImage());
        assertNull(result.getRating());
        assertNull(result.getReviewCount());
        assertNull(result.getCaloriesPerServing());
        assertNull(result.getUserId());
        assertNull(result.getMealType());
    }

    @Test
    void toEntity_Success() {
        Recipe result = recipeMapper.toEntity(testRecipeDTO);

        assertNotNull(result);
        assertEquals(testRecipeDTO.getId(), result.getId());
        assertEquals(testRecipeDTO.getName(), result.getName());
        assertEquals(testRecipeDTO.getCuisine(), result.getCuisine());
        assertEquals(testRecipeDTO.getCookTimeMinutes(), result.getCookTimeMinutes());
        assertEquals(testRecipeDTO.getPrepTimeMinutes(), result.getPrepTimeMinutes());
        assertEquals(testRecipeDTO.getServings(), result.getServings());
        assertEquals(testRecipeDTO.getDifficulty(), result.getDifficulty());
        assertEquals(testRecipeDTO.getTags(), result.getTags());
        assertEquals(testRecipeDTO.getIngredients(), result.getIngredients());
        assertEquals(testRecipeDTO.getInstructions(), result.getInstructions());
        assertEquals(testRecipeDTO.getImage(), result.getImage());
        assertEquals(testRecipeDTO.getRating(), result.getRating());
        assertEquals(testRecipeDTO.getReviewCount(), result.getReviewCount());
        assertEquals(testRecipeDTO.getCaloriesPerServing(), result.getCaloriesPerServing());
        assertEquals(testRecipeDTO.getUserId(), result.getUserId());
        assertEquals(testRecipeDTO.getMealType(), result.getMealType());
    }

    @Test
    void toEntity_NullInput() {
        Recipe result = recipeMapper.toEntity(null);
        assertNull(result);
    }

    @Test
    void toEntity_PartialData() {
        RecipeDTO partialDTO = RecipeDTO.builder()
                .id(3L)
                .name("Partial DTO")
                .cuisine("Thai")
                .rating(3.8)
                .build();

        Recipe result = recipeMapper.toEntity(partialDTO);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Partial DTO", result.getName());
        assertEquals("Thai", result.getCuisine());
        assertEquals(3.8, result.getRating());
        assertNull(result.getCookTimeMinutes());
        assertNull(result.getPrepTimeMinutes());
        assertNull(result.getServings());
        assertNull(result.getDifficulty());
        assertNull(result.getTags());
        assertNull(result.getIngredients());
        assertNull(result.getInstructions());
        assertNull(result.getImage());
        assertNull(result.getReviewCount());
        assertNull(result.getCaloriesPerServing());
        assertNull(result.getUserId());
        assertNull(result.getMealType());
    }

    @Test
    void toDTO_WithEmptyLists() {
        Recipe recipeWithEmptyLists = Recipe.builder()
                .id(4L)
                .name("Recipe with Empty Lists")
                .tags(List.of())
                .ingredients(List.of())
                .instructions(List.of())
                .mealType(List.of())
                .build();

        RecipeDTO result = recipeMapper.toDTO(recipeWithEmptyLists);

        assertNotNull(result);
        assertEquals(4L, result.getId());
        assertEquals("Recipe with Empty Lists", result.getName());
        assertTrue(result.getTags().isEmpty());
        assertTrue(result.getIngredients().isEmpty());
        assertTrue(result.getInstructions().isEmpty());
        assertTrue(result.getMealType().isEmpty());
    }

    @Test
    void toEntity_WithEmptyLists() {
        RecipeDTO dtoWithEmptyLists = RecipeDTO.builder()
                .id(5L)
                .name("DTO with Empty Lists")
                .tags(List.of())
                .ingredients(List.of())
                .instructions(List.of())
                .mealType(List.of())
                .build();

        Recipe result = recipeMapper.toEntity(dtoWithEmptyLists);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("DTO with Empty Lists", result.getName());
        assertTrue(result.getTags().isEmpty());
        assertTrue(result.getIngredients().isEmpty());
        assertTrue(result.getInstructions().isEmpty());
        assertTrue(result.getMealType().isEmpty());
    }

    @Test
    void roundTripMapping_EntityToDTOToEntity() {
        // Convert entity to DTO
        RecipeDTO dto = recipeMapper.toDTO(testRecipeEntity);
        
        // Convert DTO back to entity
        Recipe roundTripEntity = recipeMapper.toEntity(dto);

        // Verify all fields are preserved
        assertEquals(testRecipeEntity.getId(), roundTripEntity.getId());
        assertEquals(testRecipeEntity.getName(), roundTripEntity.getName());
        assertEquals(testRecipeEntity.getCuisine(), roundTripEntity.getCuisine());
        assertEquals(testRecipeEntity.getCookTimeMinutes(), roundTripEntity.getCookTimeMinutes());
        assertEquals(testRecipeEntity.getPrepTimeMinutes(), roundTripEntity.getPrepTimeMinutes());
        assertEquals(testRecipeEntity.getServings(), roundTripEntity.getServings());
        assertEquals(testRecipeEntity.getDifficulty(), roundTripEntity.getDifficulty());
        assertEquals(testRecipeEntity.getTags(), roundTripEntity.getTags());
        assertEquals(testRecipeEntity.getIngredients(), roundTripEntity.getIngredients());
        assertEquals(testRecipeEntity.getInstructions(), roundTripEntity.getInstructions());
        assertEquals(testRecipeEntity.getImage(), roundTripEntity.getImage());
        assertEquals(testRecipeEntity.getRating(), roundTripEntity.getRating());
        assertEquals(testRecipeEntity.getReviewCount(), roundTripEntity.getReviewCount());
        assertEquals(testRecipeEntity.getCaloriesPerServing(), roundTripEntity.getCaloriesPerServing());
        assertEquals(testRecipeEntity.getUserId(), roundTripEntity.getUserId());
        assertEquals(testRecipeEntity.getMealType(), roundTripEntity.getMealType());
    }

    @Test
    void roundTripMapping_DTOToEntityToDTO() {
        // Convert DTO to entity
        Recipe entity = recipeMapper.toEntity(testRecipeDTO);
        
        // Convert entity back to DTO
        RecipeDTO roundTripDTO = recipeMapper.toDTO(entity);

        // Verify all fields are preserved
        assertEquals(testRecipeDTO.getId(), roundTripDTO.getId());
        assertEquals(testRecipeDTO.getName(), roundTripDTO.getName());
        assertEquals(testRecipeDTO.getCuisine(), roundTripDTO.getCuisine());
        assertEquals(testRecipeDTO.getCookTimeMinutes(), roundTripDTO.getCookTimeMinutes());
        assertEquals(testRecipeDTO.getPrepTimeMinutes(), roundTripDTO.getPrepTimeMinutes());
        assertEquals(testRecipeDTO.getServings(), roundTripDTO.getServings());
        assertEquals(testRecipeDTO.getDifficulty(), roundTripDTO.getDifficulty());
        assertEquals(testRecipeDTO.getTags(), roundTripDTO.getTags());
        assertEquals(testRecipeDTO.getIngredients(), roundTripDTO.getIngredients());
        assertEquals(testRecipeDTO.getInstructions(), roundTripDTO.getInstructions());
        assertEquals(testRecipeDTO.getImage(), roundTripDTO.getImage());
        assertEquals(testRecipeDTO.getRating(), roundTripDTO.getRating());
        assertEquals(testRecipeDTO.getReviewCount(), roundTripDTO.getReviewCount());
        assertEquals(testRecipeDTO.getCaloriesPerServing(), roundTripDTO.getCaloriesPerServing());
        assertEquals(testRecipeDTO.getUserId(), roundTripDTO.getUserId());
        assertEquals(testRecipeDTO.getMealType(), roundTripDTO.getMealType());
    }
}
