package com.recipe_project.recipe.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleRecipeNotFoundException(RecipeNotFoundException ex) {
        logger.error("Recipe not found: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Recipe not found");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<Map<String,Object>> handleExternalApiException(ExternalApiException ex) {
        logger.error("External API error: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("error", "External API error");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(503).body(response);
    }

   @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Invalid argument: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Invalid argument");
        response.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleGenericException(Exception ex) {
        logger.error("An unexpected error occurred: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Internal server error");
        response.put("message", "An unexpected error occurred. Please try again later.");
        return ResponseEntity.status(500).body(response);

    }
}
