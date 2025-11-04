package com.recipe_project.recipe.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Indexed;

import java.util.List;

@Entity
@Table(name = "recipes")
@Indexed
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer cookTimeMinutes;
    
    private Integer prepTimeMinutes;
    
    private Integer servings;
    
    private String difficulty;
    
    @ElementCollection
    @CollectionTable(name = "recipe_tags", joinColumns = @JoinColumn(name = "recipe_id"))
    private List<String> tags;

    @ElementCollection
    @CollectionTable(name = "recipe_ingredients", joinColumns = @JoinColumn(name = "recipe_id"))
    private List<String> ingredients;

    @ElementCollection
    @CollectionTable(name = "recipe_instructions", joinColumns = @JoinColumn(name = "recipe_id"))
    private List<String> instructions;

    private String cuisine;

    private String image;

        private Double rating;
    
    private Integer reviewCount;
    
    private Integer caloriesPerServing;
    
    private Integer userId;
    
    @ElementCollection
    @CollectionTable(name = "recipe_meal_types", joinColumns = @JoinColumn(name = "recipe_id"))
    private List<String> mealType;

    // Remove redundant getters/setters since @Data provides them
}
