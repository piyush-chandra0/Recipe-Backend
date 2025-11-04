package com.recipe_project.recipe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_project.recipe.dto.RecipeDTO;
import com.recipe_project.recipe.exception.RecipeNotFoundException;
import com.recipe_project.recipe.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
public class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    @Autowired
    private ObjectMapper objectMapper;

    private RecipeDTO testRecipeDTO;
    private List<RecipeDTO> testRecipeList;

    @BeforeEach
    void setUp() {
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

        testRecipeList = List.of(testRecipeDTO);
    }

    @Test
    void getAllRecipes_Success() throws Exception {
        when(recipeService.getAllRecipes()).thenReturn(testRecipeList);

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Recipe")))
                .andExpect(jsonPath("$[0].cuisine", is("Italian")))
                .andExpect(jsonPath("$[0].cookTimeMinutes", is(30)))
                .andExpect(jsonPath("$[0].rating", is(4.5)));

        verify(recipeService).getAllRecipes();
    }

    @Test
    void getAllRecipes_EmptyList() throws Exception {
        when(recipeService.getAllRecipes()).thenReturn(List.of());

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(recipeService).getAllRecipes();
    }

    @Test
    void searchRecipes_WithQuery_Success() throws Exception {
        when(recipeService.searchRecipes("pizza")).thenReturn(testRecipeList);        mockMvc.perform(get("/api/recipes/search")
                        .param("q", "pizza"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Recipe")));

        verify(recipeService).searchRecipes("pizza");
    }

    @Test
    void searchRecipes_WithoutQuery_Success() throws Exception {
        when(recipeService.searchRecipes(null)).thenReturn(testRecipeList);

        mockMvc.perform(get("/api/recipes/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(recipeService).searchRecipes(null);
    }

    @Test
    void searchRecipes_EmptyQuery_Success() throws Exception {
        when(recipeService.searchRecipes("")).thenReturn(testRecipeList);

        mockMvc.perform(get("/api/recipes/search")
                        .param("q", ""))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(recipeService).searchRecipes("");
    }

    @Test
    void searchRecipes_NoResults() throws Exception {
        when(recipeService.searchRecipes("nonexistent")).thenReturn(List.of());        mockMvc.perform(get("/api/recipes/search")
                        .param("q", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(recipeService).searchRecipes("nonexistent");
    }

    @Test
    void getRecipeById_Success() throws Exception {
        when(recipeService.getRecipeById(1L)).thenReturn(testRecipeDTO);

        mockMvc.perform(get("/api/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Recipe")))
                .andExpect(jsonPath("$.cuisine", is("Italian")));

        verify(recipeService).getRecipeById(1L);
    }

    @Test
    void getRecipeById_NotFound() throws Exception {
        when(recipeService.getRecipeById(999L))
                .thenThrow(new RecipeNotFoundException("Recipe not found with ID: 999"));        mockMvc.perform(get("/api/recipes/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Recipe not found")))
                .andExpect(jsonPath("$.message", is("Recipe not found with ID: 999")));

        verify(recipeService).getRecipeById(999L);
    }

    @Test
    void getRecipeById_InvalidId() throws Exception {
        when(recipeService.getRecipeById(anyLong()))
                .thenThrow(new IllegalArgumentException("Recipe ID must be a positive number."));

        mockMvc.perform(get("/api/recipes/0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Invalid argument")))
                .andExpect(jsonPath("$.message", is("Recipe ID must be a positive number.")));
    }

    @Test
    void loadRecipesFromApi_Success() throws Exception {
        when(recipeService.loadRecipesFromExternalApi()).thenReturn(50);

        mockMvc.perform(post("/api/recipes/load"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Successfully loaded recipes from external API")))
                .andExpect(jsonPath("$.count", is(50)));

        verify(recipeService).loadRecipesFromExternalApi();
    }

    @Test
    void loadRecipesFromApi_Failure() throws Exception {
        when(recipeService.loadRecipesFromExternalApi())
                .thenThrow(new RuntimeException("Failed to fetch recipes from external API"));        mockMvc.perform(post("/api/recipes/load"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Failed to load recipes")))
                .andExpect(jsonPath("$.message", is("Failed to fetch recipes from external API")));

        verify(recipeService).loadRecipesFromExternalApi();
    }

    @Test
    void searchRecipes_ValidationError() throws Exception {
        when(recipeService.searchRecipes(anyString()))
                .thenThrow(new IllegalArgumentException("Search query must be at least 2 characters long."));

        mockMvc.perform(get("/api/recipes/search")
                        .param("q", "a"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Invalid argument")))
                .andExpect(jsonPath("$.message", is("Search query must be at least 2 characters long.")));
    }
}
