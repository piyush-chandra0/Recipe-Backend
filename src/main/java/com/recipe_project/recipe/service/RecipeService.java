package com.recipe_project.recipe.service;

import com.recipe_project.recipe.dto.ExternalApiResponse;
import com.recipe_project.recipe.dto.RecipeDTO;
import com.recipe_project.recipe.entity.Recipe;
import com.recipe_project.recipe.exception.RecipeNotFoundException;
import com.recipe_project.recipe.mapper.RecipeMapper;
import com.recipe_project.recipe.repository.RecipeRepository;
import com.recipe_project.recipe.util.ValidationUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecipeService {

    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);

    private final RecipeRepository recipeRepository;
    private final ExternalApiService externalApiService;
    private final RecipeMapper recipeMapper;
    private final ValidationUtil validationUtil;

    public RecipeService(RecipeRepository recipeRepository, ExternalApiService externalApiService, RecipeMapper recipeMapper, ValidationUtil validationUtil) {
        this.recipeRepository = recipeRepository;
        this.externalApiService = externalApiService;
        this.recipeMapper = recipeMapper;
        this.validationUtil = validationUtil;
        logger.info("RecipeService initialized");
    }

    public int loadRecipesFromExternalApi(){
        logger.info("Starting to load recipes from external API");

        ExternalApiResponse response = externalApiService.fetchAllRecipes();

        if(response == null || response.getRecipes() == null) {
            logger.error("Failed to fetch recipes from external API");
            return 0;
        }

        List<Recipe> recipes = response.getRecipes().stream()
                .map(recipeMapper::toEntity)
                .collect(Collectors.toList());

        recipeRepository.deleteAll();
        List<Recipe> savedRecipes = recipeRepository.saveAll(recipes);
        logger.info("Successfully loaded {} recipes from external API", savedRecipes.size());
        return savedRecipes.size();
    }    @Transactional
    public List<RecipeDTO> searchRecipes(String query) {
        logger.debug("Searching recipes with query: {}", query);

        List<Recipe> recipes;

        if(StringUtils.hasText(query)) {
            validationUtil.validateSearchQuery(query);
            recipes = recipeRepository.findByNameContainingIgnoreCaseOrCuisineContainingIgnoreCase(
                    query.trim());
            logger.debug("Found {} recipes matching query: {}", recipes.size(), query);
        } else {
            logger.debug("Empty search query provided, returning all recipes");
            recipes = recipeRepository.findAll();
        }

        return recipes.stream()
                .map(recipeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RecipeDTO getRecipeById(Long id) {
        validationUtil.validateRecipeId(id);
        logger.debug("Fetching recipe with ID: {}", id);

        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found with ID: " + id));

        return recipeMapper.toDTO(recipe);
    }

    @Transactional
    public List<RecipeDTO> getAllRecipes() {
        logger.debug("Fetching all recipes");
        List<Recipe> recipes = recipeRepository.findAll();
        return recipes.stream()
                .map(recipeMapper::toDTO)
                .collect(Collectors.toList());
    }


}
