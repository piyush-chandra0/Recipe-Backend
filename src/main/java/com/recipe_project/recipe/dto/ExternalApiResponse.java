package com.recipe_project.recipe.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExternalApiResponse {
    private List<RecipeDTO> recipes;
    private int total;
    private int skip;
    private int limit;
}
