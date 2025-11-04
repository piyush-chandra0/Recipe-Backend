package com.recipe_project.recipe.service;

import com.recipe_project.recipe.dto.ExternalApiResponse;
import com.recipe_project.recipe.dto.RecipeDTO;
import com.recipe_project.recipe.entity.Recipe;
import com.recipe_project.recipe.exception.RecipeNotFoundException;
import com.recipe_project.recipe.mapper.RecipeMapper;
import com.recipe_project.recipe.repository.RecipeRepository;
import com.recipe_project.recipe.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {
    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private ExternalApiService externalApiService;

    @Mock
    private RecipeMapper recipeMapper;

    @Mock
    private ValidationUtil validationUtil;

    private Recipe testRecipeEntity;
    private RecipeDTO testRecipeDTO;
    private List<RecipeDTO> testRecipeDTOList;
    private ExternalApiResponse testExternalApiResponse;
    private List<Recipe> testRecipeEntityList;
    @BeforeEach
    void setUp() {
        // Create test Recipe entity with all fields
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

        // Create test RecipeDTO with all fields
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

        testRecipeEntityList = List.of(testRecipeEntity);
        testRecipeDTOList = List.of(testRecipeDTO);
        testExternalApiResponse = ExternalApiResponse.builder()
                .recipes(testRecipeDTOList)
                .total(1)
                .skip(0)
                .limit(1)
                .build();
    }    // Test loadRecipesFromExternalApi - Success case
    @Test
    void loadRecipesFromExternalApi_Success() {
        when(externalApiService.fetchAllRecipes()).thenReturn(testExternalApiResponse);
        when(recipeMapper.toEntity(any(RecipeDTO.class))).thenReturn(testRecipeEntity);
        when(recipeRepository.saveAll(anyList())).thenReturn(testRecipeEntityList);

        int loadedCount = recipeService.loadRecipesFromExternalApi();

        assertEquals(1, loadedCount);
        verify(recipeRepository).deleteAll();
        verify(recipeRepository).saveAll(anyList());
        verify(externalApiService).fetchAllRecipes();
        verify(recipeMapper).toEntity(any(RecipeDTO.class));
    }

    // Test loadRecipesFromExternalApi - Null response case
    @Test
    void loadRecipesFromExternalApi_NullResponse() {
        when(externalApiService.fetchAllRecipes()).thenReturn(null);

        int loadedCount = recipeService.loadRecipesFromExternalApi();

        assertEquals(0, loadedCount);
        verify(externalApiService).fetchAllRecipes();
        verify(recipeRepository, never()).deleteAll();
        verify(recipeRepository, never()).saveAll(anyList());
    }

    // Test loadRecipesFromExternalApi - Null recipes list
    @Test
    void loadRecipesFromExternalApi_NullRecipesList() {
        ExternalApiResponse nullRecipesResponse = ExternalApiResponse.builder()
                .recipes(null)
                .total(0)
                .build();
        when(externalApiService.fetchAllRecipes()).thenReturn(nullRecipesResponse);

        int loadedCount = recipeService.loadRecipesFromExternalApi();

        assertEquals(0, loadedCount);
        verify(externalApiService).fetchAllRecipes();
        verify(recipeRepository, never()).deleteAll();
        verify(recipeRepository, never()).saveAll(anyList());
    }

    // Test searchRecipes - Valid query
    @Test
    void searchRecipes_ValidQuery() {
        String query = "Italian";
        when(recipeRepository.findByNameContainingIgnoreCaseOrCuisineContainingIgnoreCase(query))
                .thenReturn(testRecipeEntityList);
        when(recipeMapper.toDTO(any(Recipe.class))).thenReturn(testRecipeDTO);

        List<RecipeDTO> result = recipeService.searchRecipes(query);

        assertEquals(1, result.size());
        assertEquals(testRecipeDTO, result.get(0));
        verify(validationUtil).validateSearchQuery(query);
        verify(recipeRepository).findByNameContainingIgnoreCaseOrCuisineContainingIgnoreCase(query);
    }

    // Test searchRecipes - Empty query returns all recipes
    @Test
    void searchRecipes_EmptyQuery_ReturnsAll() {
        String query = "";
        when(recipeRepository.findAll()).thenReturn(testRecipeEntityList);
        when(recipeMapper.toDTO(any(Recipe.class))).thenReturn(testRecipeDTO);

        List<RecipeDTO> result = recipeService.searchRecipes(query);

        assertEquals(1, result.size());
        assertEquals(testRecipeDTO, result.get(0));
        verify(recipeRepository).findAll();
        verify(recipeRepository, never()).findByNameContainingIgnoreCaseOrCuisineContainingIgnoreCase(anyString());
    }

    // Test searchRecipes - Null query returns all recipes
    @Test
    void searchRecipes_NullQuery_ReturnsAll() {
        String query = null;
        when(recipeRepository.findAll()).thenReturn(testRecipeEntityList);
        when(recipeMapper.toDTO(any(Recipe.class))).thenReturn(testRecipeDTO);

        List<RecipeDTO> result = recipeService.searchRecipes(query);

        assertEquals(1, result.size());
        assertEquals(testRecipeDTO, result.get(0));
        verify(recipeRepository).findAll();
        verify(validationUtil, never()).validateSearchQuery(anyString());
    }

    // Test searchRecipes - Whitespace query returns all recipes
    @Test
    void searchRecipes_WhitespaceQuery_ReturnsAll() {
        String query = "   ";
        when(recipeRepository.findAll()).thenReturn(testRecipeEntityList);
        when(recipeMapper.toDTO(any(Recipe.class))).thenReturn(testRecipeDTO);

        List<RecipeDTO> result = recipeService.searchRecipes(query);

        assertEquals(1, result.size());
        assertEquals(testRecipeDTO, result.get(0));
        verify(recipeRepository).findAll();
    }

    // Test searchRecipes - No results found
    @Test
    void searchRecipes_NoResults() {
        String query = "NonExistent";
        when(recipeRepository.findByNameContainingIgnoreCaseOrCuisineContainingIgnoreCase(query))
                .thenReturn(List.of());

        List<RecipeDTO> result = recipeService.searchRecipes(query);

        assertTrue(result.isEmpty());
        verify(validationUtil).validateSearchQuery(query);
        verify(recipeRepository).findByNameContainingIgnoreCaseOrCuisineContainingIgnoreCase(query);
    }

    // Test getRecipeById - Success
    @Test
    void getRecipeById_Success() {
        Long recipeId = 1L;
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(testRecipeEntity));
        when(recipeMapper.toDTO(testRecipeEntity)).thenReturn(testRecipeDTO);

        RecipeDTO result = recipeService.getRecipeById(recipeId);

        assertEquals(testRecipeDTO, result);
        verify(validationUtil).validateRecipeId(recipeId);
        verify(recipeRepository).findById(recipeId);
        verify(recipeMapper).toDTO(testRecipeEntity);
    }

    // Test getRecipeById - Recipe not found
    @Test
    void getRecipeById_NotFound() {
        Long recipeId = 999L;
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class, () -> {
            recipeService.getRecipeById(recipeId);
        });

        verify(validationUtil).validateRecipeId(recipeId);
        verify(recipeRepository).findById(recipeId);
        verify(recipeMapper, never()).toDTO(any());
    }

    // Test getRecipeById - Invalid ID (null)
    @Test
    void getRecipeById_InvalidId() {
        Long recipeId = null;
        doThrow(new IllegalArgumentException("Recipe ID must be a positive number."))
                .when(validationUtil).validateRecipeId(recipeId);

        assertThrows(IllegalArgumentException.class, () -> {
            recipeService.getRecipeById(recipeId);
        });

        verify(validationUtil).validateRecipeId(recipeId);
        verify(recipeRepository, never()).findById(any());
    }

    // Test getAllRecipes - Success
    @Test
    void getAllRecipes_Success() {
        when(recipeRepository.findAll()).thenReturn(testRecipeEntityList);
        when(recipeMapper.toDTO(any(Recipe.class))).thenReturn(testRecipeDTO);

        List<RecipeDTO> result = recipeService.getAllRecipes();

        assertEquals(1, result.size());
        assertEquals(testRecipeDTO, result.get(0));
        verify(recipeRepository).findAll();
        verify(recipeMapper).toDTO(testRecipeEntity);
    }

    // Test getAllRecipes - Empty database
    @Test
    void getAllRecipes_EmptyDatabase() {
        when(recipeRepository.findAll()).thenReturn(List.of());

        List<RecipeDTO> result = recipeService.getAllRecipes();

        assertTrue(result.isEmpty());
        verify(recipeRepository).findAll();
        verify(recipeMapper, never()).toDTO(any());
    }

    // Test getAllRecipes - Multiple recipes
    @Test
    void getAllRecipes_MultipleRecipes() {
        Recipe secondRecipe = Recipe.builder()
                .id(2L)
                .name("Second Recipe")
                .cuisine("Mexican")
                .build();
        
        RecipeDTO secondRecipeDTO = RecipeDTO.builder()
                .id(2L)
                .name("Second Recipe")
                .cuisine("Mexican")
                .build();

        List<Recipe> multipleRecipes = List.of(testRecipeEntity, secondRecipe);
        when(recipeRepository.findAll()).thenReturn(multipleRecipes);
        when(recipeMapper.toDTO(testRecipeEntity)).thenReturn(testRecipeDTO);
        when(recipeMapper.toDTO(secondRecipe)).thenReturn(secondRecipeDTO);

        List<RecipeDTO> result = recipeService.getAllRecipes();

        assertEquals(2, result.size());
        assertTrue(result.contains(testRecipeDTO));
        assertTrue(result.contains(secondRecipeDTO));
        verify(recipeRepository).findAll();
        verify(recipeMapper, times(2)).toDTO(any(Recipe.class));
    }

    // Test searchRecipes with validation exception
    @Test
    void searchRecipes_ValidationException() {
        String invalidQuery = "a"; // Too short
        doThrow(new IllegalArgumentException("Search query must be at least 2 characters long."))
                .when(validationUtil).validateSearchQuery(invalidQuery);

        assertThrows(IllegalArgumentException.class, () -> {
            recipeService.searchRecipes(invalidQuery);
        });

        verify(validationUtil).validateSearchQuery(invalidQuery);
        verify(recipeRepository, never()).findByNameContainingIgnoreCaseOrCuisineContainingIgnoreCase(anyString());
        verify(recipeRepository, never()).findAll();
    }

    // Test loadRecipesFromExternalApi with empty recipes list
    @Test
    void loadRecipesFromExternalApi_EmptyRecipesList() {
        ExternalApiResponse emptyResponse = ExternalApiResponse.builder()
                .recipes(List.of())
                .total(0)
                .build();
        when(externalApiService.fetchAllRecipes()).thenReturn(emptyResponse);
        when(recipeRepository.saveAll(anyList())).thenReturn(List.of());

        int loadedCount = recipeService.loadRecipesFromExternalApi();

        assertEquals(0, loadedCount);
        verify(recipeRepository).deleteAll();
        verify(recipeRepository).saveAll(List.of());
        verify(recipeMapper, never()).toEntity(any());
    }




}
