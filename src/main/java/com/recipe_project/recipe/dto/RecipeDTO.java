package com.recipe_project.recipe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO {

    private Long id;
    private String name;
    
    @JsonProperty("cookTimeMinutes")
    private Integer cookTimeMinutes;
    
    @JsonProperty("prepTimeMinutes")
    private Integer prepTimeMinutes;
    
    private Integer servings;
    private String difficulty;
    
    private List<String> tags;
    private List<String> ingredients;
    private List<String> instructions;
    
    private String cuisine;
    
    @JsonProperty("image")
    private String image;
      private Double rating;
    private Integer reviewCount;
    private Integer caloriesPerServing;
    private Integer userId;
    private List<String> mealType;

    // Remove redundant getters/setters since @Data provides them
}
