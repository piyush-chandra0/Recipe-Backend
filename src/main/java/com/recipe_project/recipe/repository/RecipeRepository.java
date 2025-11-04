package com.recipe_project.recipe.repository;

import com.recipe_project.recipe.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe,Long> {    @Query("SELECT DISTINCT r FROM Recipe r LEFT JOIN r.tags t LEFT JOIN r.ingredients i WHERE " +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(r.cuisine) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(t) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(i) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Recipe> findByNameContainingIgnoreCaseOrCuisineContainingIgnoreCase(@Param("query") String query);
}
