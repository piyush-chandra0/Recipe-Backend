package com.recipe_project.recipe.service;

import com.recipe_project.recipe.dto.ExternalApiResponse;
import com.recipe_project.recipe.dto.RecipeDTO;
import com.recipe_project.recipe.entity.Recipe;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        testRecipeEntity = new Recipe();

        testRecipeEntity.setId(1L);
        testRecipeEntity.setName("Test Recipe");
        testRecipeEntity.setCuisine("Test Cuisine");
        testRecipeEntity.setCookingTime(30);
        testRecipeEntity.setTags(List.of("tag1", "tag2"));
        testRecipeEntity.setImageUrl("test_image_url");
        testRecipeEntity.setRating(4.5);

        testRecipeDTO = new RecipeDTO();
        testRecipeDTO.setId(1L);
        testRecipeDTO.setName("Test Recipe");
        testRecipeDTO.setCuisine("Test Cuisine");
        testRecipeDTO.setCookingTime(30);
        testRecipeDTO.setTags(List.of("tag1", "tag2"));
        testRecipeDTO.setImageUrl("test_image_url");
        testRecipeDTO.setRating(4.5);

        testRecipeEntityList = List.of(testRecipeEntity);
        testRecipeDTOList = List.of(testRecipeDTO);
        testExternalApiResponse = new ExternalApiResponse();
        testExternalApiResponse.setRecipes(testRecipeDTOList);
    }

    @Test
    void testLoadRecipesFromExternalApi() {
         when(externalApiService.fetchAllRecipes()).thenReturn(testExternalApiResponse);
         when(recipeMapper.toEntity(any())).thenReturn(testRecipeEntity);
         when(recipeRepository.saveAll(anyList())).thenReturn(testRecipeEntityList);

         int loadedCount = recipeService.loadRecipesFromExternalApi();

         assertEquals(1, loadedCount);
         verify(recipeRepository).deleteAll();
         verify(recipeRepository).saveAll(anyList());
    }

    @Test
    void testSearchRecipesWithValidQuery() {
        String query = "Italian";
        when(recipeRepository.findByNameContainingIgnoreCaseOrCuisineContainingIgnoreCase(query))
                .thenReturn(testRecipeEntityList);
        when(recipeMapper.toDTO(any())).thenReturn(testRecipeDTO);

        List<RecipeDTO> result = recipeService.searchRecipes(query);

        assertEquals(1, result.size());
        assertEquals(testRecipeDTO, result.get(0));
        verify(validationUtil).validateSearchQuery(query);
    }

    @Test
    void testSearchRecipesWithEmptyQuery() {
        String query = "";
        when(recipeRepository.findAll()).thenReturn(testRecipeEntityList);
        when(recipeMapper.toDTO(any())).thenReturn(testRecipeDTO);

        List<RecipeDTO> result = recipeService.searchRecipes(query);

        assertEquals(1, result.size());
        assertEquals(testRecipeDTO, result.get(0));
        verify(validationUtil).validateSearchQuery(query);
    }

    @Test
    void getAllRecipeById_Success() {
        Long recipeId = 1L;
        when(recipeRepository.findById(recipeId)).thenReturn(java.util.Optional.of(testRecipeEntity));
        when(recipeMapper.toDTO(testRecipeEntity)).thenReturn(testRecipeDTO);

        RecipeDTO result = recipeService.getRecipeById(recipeId);

        assertEquals(testRecipeDTO, result);
        verify(recipeRepository).findById(recipeId);
    }

    @Test
    void getAllRecipies_Success() {
        when(recipeRepository.findAll()).thenReturn(testRecipeEntityList);
        when(recipeMapper.toDTO(any())).thenReturn(testRecipeDTO);

        List<RecipeDTO> result = recipeService.getAllRecipes();

        assertEquals(1, result.size());
        assertEquals(testRecipeDTO, result.get(0));
        verify(recipeRepository).findAll();
    }




}
