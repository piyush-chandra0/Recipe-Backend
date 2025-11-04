package com.recipe_project.recipe.util;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ValidationUtil {    public void validateSearchQuery(String query) {
        if(query != null && StringUtils.hasText(query)) {
            String trimmedQuery = query.trim();
            if(trimmedQuery.length() < 2) {
                throw new IllegalArgumentException("Search query must be at least 2 characters long.");
            }
            if(trimmedQuery.length() > 100) {
                throw new IllegalArgumentException("Search query must not exceed 100 characters.");
            }
        }
        // Allow empty/null queries to return all recipes
    }

    public void validateRecipeId(Long id) {
        if(id == null || id <= 0) {
            throw new IllegalArgumentException("Recipe ID must be a positive number.");
        }
    }
}
