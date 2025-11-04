package com.recipe_project.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe_project.recipe.dto.RecipeDTO;
import com.recipe_project.recipe.entity.Recipe;
import com.recipe_project.recipe.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = {
        "external.api.recipes.base-url=http://localhost:8080/mock",
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
public class RecipeApplicationIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Clear database and add test data
        recipeRepository.deleteAll();
        
        Recipe testRecipe1 = Recipe.builder()
                .name("Italian Pasta")
                .cuisine("Italian")
                .cookTimeMinutes(20)
                .prepTimeMinutes(10)
                .servings(2)
                .difficulty("Easy")
                .tags(List.of("pasta", "italian", "dinner"))
                .ingredients(List.of("pasta", "tomato sauce", "cheese"))
                .instructions(List.of("Boil pasta", "Add sauce", "Serve"))
                .image("pasta.jpg")
                .rating(4.5)
                .reviewCount(100)
                .caloriesPerServing(300)
                .userId(1)
                .mealType(List.of("Dinner"))
                .build();

        Recipe testRecipe2 = Recipe.builder()
                .name("Mexican Tacos")
                .cuisine("Mexican")
                .cookTimeMinutes(15)
                .prepTimeMinutes(10)
                .servings(4)
                .difficulty("Medium")
                .tags(List.of("tacos", "mexican", "lunch"))
                .ingredients(List.of("tortillas", "meat", "vegetables"))
                .instructions(List.of("Cook meat", "Warm tortillas", "Assemble"))
                .image("tacos.jpg")
                .rating(4.2)
                .reviewCount(75)
                .caloriesPerServing(250)
                .userId(2)
                .mealType(List.of("Lunch"))
                .build();

        recipeRepository.saveAll(List.of(testRecipe1, testRecipe2));
    }

    @Test
    void getAllRecipes_IntegrationTest() throws Exception {
        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", anyOf(is("Italian Pasta"), is("Mexican Tacos"))))
                .andExpect(jsonPath("$[1].name", anyOf(is("Italian Pasta"), is("Mexican Tacos"))));
    }

    @Test
    void searchRecipes_ByName_IntegrationTest() throws Exception {        mockMvc.perform(get("/api/recipes/search")
                        .param("q", "pasta"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Italian Pasta")))
                .andExpect(jsonPath("$[0].cuisine", is("Italian")));
    }

    @Test
    void searchRecipes_ByCuisine_IntegrationTest() throws Exception {
        mockMvc.perform(get("/api/recipes/search")
                        .param("q", "mexican"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Mexican Tacos")))
                .andExpect(jsonPath("$[0].cuisine", is("Mexican")));
    }

    @Test
    void searchRecipes_ByIngredient_IntegrationTest() throws Exception {
        mockMvc.perform(get("/api/recipes/search")
                        .param("q", "cheese"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Italian Pasta")));
    }

    @Test
    void searchRecipes_ByTag_IntegrationTest() throws Exception {
        mockMvc.perform(get("/api/recipes/search")
                        .param("q", "dinner"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Italian Pasta")));
    }

    @Test
    void searchRecipes_NoResults_IntegrationTest() throws Exception {
        mockMvc.perform(get("/api/recipes/search")
                        .param("q", "sushi"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchRecipes_EmptyQuery_ReturnsAll_IntegrationTest() throws Exception {
        mockMvc.perform(get("/api/recipes/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getRecipeById_Success_IntegrationTest() throws Exception {
        // Get the first recipe ID from database
        List<Recipe> recipes = recipeRepository.findAll();
        Long recipeId = recipes.get(0).getId();

        mockMvc.perform(get("/api/recipes/" + recipeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(recipeId.intValue())))
                .andExpect(jsonPath("$.name", anyOf(is("Italian Pasta"), is("Mexican Tacos"))))
                .andExpect(jsonPath("$.cuisine", anyOf(is("Italian"), is("Mexican"))))
                .andExpect(jsonPath("$.cookTimeMinutes", anyOf(is(20), is(15))))
                .andExpect(jsonPath("$.prepTimeMinutes", is(10)))
                .andExpect(jsonPath("$.ingredients", notNullValue()))
                .andExpect(jsonPath("$.instructions", notNullValue()))
                .andExpect(jsonPath("$.tags", notNullValue()))
                .andExpect(jsonPath("$.mealType", notNullValue()));
    }

    @Test
    void getRecipeById_NotFound_IntegrationTest() throws Exception {
        mockMvc.perform(get("/api/recipes/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Recipe not found")))
                .andExpect(jsonPath("$.message", containsString("Recipe not found with ID: 999")));
    }

    @Test
    void getRecipeById_InvalidId_IntegrationTest() throws Exception {
        mockMvc.perform(get("/api/recipes/0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Invalid argument")))
                .andExpect(jsonPath("$.message", is("Recipe ID must be a positive number.")));
    }

    @Test
    void searchRecipes_ValidationError_IntegrationTest() throws Exception {
        mockMvc.perform(get("/api/recipes/search")
                        .param("q", "a"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Invalid argument")))
                .andExpect(jsonPath("$.message", is("Search query must be at least 2 characters long.")));
    }

    @Test
    void searchRecipes_TooLongQuery_IntegrationTest() throws Exception {
        String longQuery = "a".repeat(101);
        
        mockMvc.perform(get("/api/recipes/search")
                        .param("q", longQuery))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Invalid argument")))
                .andExpect(jsonPath("$.message", is("Search query must not exceed 100 characters.")));
    }

    @Test
    void cors_PreflightRequest_IntegrationTest() throws Exception {
        mockMvc.perform(options("/api/recipes")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("GET")));
    }

    @Test
    void databasePersistence_IntegrationTest() throws Exception {
        // Verify data persists correctly
        List<Recipe> allRecipes = recipeRepository.findAll();
        assertEquals(2, allRecipes.size());

        Recipe italianPasta = allRecipes.stream()
                .filter(r -> "Italian Pasta".equals(r.getName()))
                .findFirst()
                .orElseThrow();

        assertNotNull(italianPasta.getId());
        assertEquals("Italian", italianPasta.getCuisine());
        assertEquals(3, italianPasta.getIngredients().size());
        assertEquals(3, italianPasta.getInstructions().size());
        assertEquals(3, italianPasta.getTags().size());
        assertTrue(italianPasta.getTags().contains("pasta"));
        assertTrue(italianPasta.getIngredients().contains("cheese"));
    }
}
