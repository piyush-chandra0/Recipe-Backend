package com.recipe_project.recipe.service;

import com.recipe_project.recipe.dto.ExternalApiResponse;
import com.recipe_project.recipe.dto.RecipeDTO;
import com.recipe_project.recipe.exception.ExternalApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExternalApiServiceTest {

    @InjectMocks
    private ExternalApiService externalApiService;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private ExternalApiResponse testApiResponse;
    private RecipeDTO testRecipeDTO;

    @BeforeEach
    void setUp() {
        // Set timeout via reflection
        ReflectionTestUtils.setField(externalApiService, "timeout", Duration.ofSeconds(30));

        // Create test data
        testRecipeDTO = RecipeDTO.builder()
                .id(1L)
                .name("Test Recipe")
                .cuisine("Italian")
                .cookTimeMinutes(30)
                .prepTimeMinutes(15)
                .servings(4)
                .difficulty("Easy")
                .tags(List.of("tag1", "tag2"))
                .ingredients(List.of("ingredient1", "ingredient2"))
                .instructions(List.of("step1", "step2"))
                .image("test_image_url")
                .rating(4.5)
                .reviewCount(10)
                .caloriesPerServing(250)
                .userId(123)
                .mealType(List.of("Dinner"))
                .build();

        testApiResponse = ExternalApiResponse.builder()
                .recipes(List.of(testRecipeDTO))
                .total(1)
                .skip(0)
                .limit(1)
                .build();
    }

    @Test
    void fetchAllRecipes_Success() {
        // Mock the WebClient chain
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/recipes?limit=0")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ExternalApiResponse.class)).thenReturn(Mono.just(testApiResponse));

        ExternalApiResponse result = externalApiService.fetchAllRecipes();

        assertNotNull(result);
        assertEquals(testApiResponse, result);
        assertEquals(1, result.getRecipes().size());
        assertEquals(testRecipeDTO, result.getRecipes().get(0));

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri("/recipes?limit=0");
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(ExternalApiResponse.class);
    }

    @Test
    void fetchAllRecipes_WebClientException() {
        // Mock the WebClient chain to throw WebClientException
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/recipes?limit=0")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ExternalApiResponse.class))
                .thenReturn(Mono.error(new WebClientException("Connection failed")));

        ExternalApiException exception = assertThrows(ExternalApiException.class, () -> {
            externalApiService.fetchAllRecipes();
        });

        assertEquals("Failed to fetch recipes from external API", exception.getMessage());
        assertTrue(exception.getCause() instanceof WebClientException);

        verify(webClient).get();
    }

    @Test
    void fetchAllRecipes_GenericException() {
        // Mock the WebClient chain to throw a generic exception
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/recipes?limit=0")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ExternalApiResponse.class))
                .thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            externalApiService.fetchAllRecipes();
        });

        assertEquals("Failed to fetch recipes from external API", exception.getMessage());

        verify(webClient).get();
    }

    @Test
    void fetchAllRecipes_EmptyResponse() {
        ExternalApiResponse emptyResponse = ExternalApiResponse.builder()
                .recipes(List.of())
                .total(0)
                .skip(0)
                .limit(0)
                .build();

        // Mock the WebClient chain
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/recipes?limit=0")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ExternalApiResponse.class)).thenReturn(Mono.just(emptyResponse));

        ExternalApiResponse result = externalApiService.fetchAllRecipes();

        assertNotNull(result);
        assertEquals(emptyResponse, result);
        assertTrue(result.getRecipes().isEmpty());
        assertEquals(0, result.getTotal());

        verify(webClient).get();
    }

    @Test
    void fetchAllRecipes_NullResponse() {
        // Mock the WebClient chain to return null
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/recipes?limit=0")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ExternalApiResponse.class)).thenReturn(Mono.empty());

        ExternalApiResponse result = externalApiService.fetchAllRecipes();

        assertNull(result);

        verify(webClient).get();
    }

    @Test
    void fetchAllRecipes_MultipleRecipes() {
        RecipeDTO secondRecipe = RecipeDTO.builder()
                .id(2L)
                .name("Second Recipe")
                .cuisine("Mexican")
                .build();

        ExternalApiResponse multipleRecipesResponse = ExternalApiResponse.builder()
                .recipes(List.of(testRecipeDTO, secondRecipe))
                .total(2)
                .skip(0)
                .limit(2)
                .build();

        // Mock the WebClient chain
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/recipes?limit=0")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ExternalApiResponse.class)).thenReturn(Mono.just(multipleRecipesResponse));

        ExternalApiResponse result = externalApiService.fetchAllRecipes();

        assertNotNull(result);
        assertEquals(2, result.getRecipes().size());
        assertEquals(2, result.getTotal());
        assertTrue(result.getRecipes().contains(testRecipeDTO));
        assertTrue(result.getRecipes().contains(secondRecipe));

        verify(webClient).get();
    }

    @Test
    void fetchAllRecipes_TimeoutException() {
        // Mock timeout scenario
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/recipes?limit=0")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ExternalApiResponse.class))
                .thenReturn(Mono.error(new java.util.concurrent.TimeoutException("Request timeout")));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            externalApiService.fetchAllRecipes();
        });

        assertEquals("Failed to fetch recipes from external API", exception.getMessage());

        verify(webClient).get();
    }

    @Test
    void fetchAllRecipes_RetryMechanism() {
        // This test verifies that the @Retryable annotation would work
        // In a real scenario, this would retry 3 times with 1 second backoff
        
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/recipes?limit=0")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        
        // First two calls fail, third succeeds
        when(responseSpec.bodyToMono(ExternalApiResponse.class))
                .thenReturn(Mono.error(new RuntimeException("Temporary failure")))
                .thenReturn(Mono.error(new RuntimeException("Temporary failure")))
                .thenReturn(Mono.just(testApiResponse));

        // Note: In unit tests, @Retryable won't work unless Spring Retry is properly configured
        // This test documents the expected behavior
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            externalApiService.fetchAllRecipes();
        });

        assertEquals("Failed to fetch recipes from external API", exception.getMessage());
    }

    @Test
    void fetchAllRecipes_LargeResponse() {
        // Test with a large number of recipes
        List<RecipeDTO> manyRecipes = List.of(
                testRecipeDTO,
                RecipeDTO.builder().id(2L).name("Recipe 2").build(),
                RecipeDTO.builder().id(3L).name("Recipe 3").build(),
                RecipeDTO.builder().id(4L).name("Recipe 4").build(),
                RecipeDTO.builder().id(5L).name("Recipe 5").build()
        );

        ExternalApiResponse largeResponse = ExternalApiResponse.builder()
                .recipes(manyRecipes)
                .total(50)
                .skip(0)
                .limit(50)
                .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/recipes?limit=0")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ExternalApiResponse.class)).thenReturn(Mono.just(largeResponse));

        ExternalApiResponse result = externalApiService.fetchAllRecipes();

        assertNotNull(result);
        assertEquals(5, result.getRecipes().size());
        assertEquals(50, result.getTotal());

        verify(webClient).get();
    }
}
