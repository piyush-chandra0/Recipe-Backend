package com.recipe_project.recipe.service;

import com.recipe_project.recipe.dto.ExternalApiResponse;
import com.recipe_project.recipe.exception.ExternalApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.time.Duration;

@Service
public class ExternalApiService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalApiService.class);

    private static WebClient webClient;

    @Value("${external.api.recipes.timeout:30s}")
    private Duration timeout;

    public ExternalApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Retryable(
        value = { Exception.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )    public ExternalApiResponse fetchAllRecipes() {
        logger.info("Fetching all recipes from external API");

        try {
            return webClient.get()
                .uri("/recipes?limit=0")
                .retrieve()
                .bodyToMono(ExternalApiResponse.class)
                .timeout(timeout)
                .doOnSuccess(response -> logger.info("Successfully fetched recipes from external API"))
                .doOnError(error -> logger.error("Error fetching recipes: {}", error.getMessage()))
                .block();
        }
        catch(WebClientException e) {
            logger.error("WebClient error while fetching recipes: {}", e.getMessage());
            throw new ExternalApiException("Failed to fetch recipes from external API", e);
        }
        catch (Exception e) {
            logger.error("Failed to fetch recipes: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch recipes from external API", e);
        }
    }


}
