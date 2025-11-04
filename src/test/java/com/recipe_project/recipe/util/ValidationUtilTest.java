package com.recipe_project.recipe.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilTest {

    private ValidationUtil validationUtil;

    @BeforeEach
    void setUp() {
        validationUtil = new ValidationUtil();
    }

    // Tests for validateSearchQuery method

    @Test
    void validateSearchQuery_ValidQuery_Success() {
        String validQuery = "pizza";
        
        assertDoesNotThrow(() -> {
            validationUtil.validateSearchQuery(validQuery);
        });
    }

    @Test
    void validateSearchQuery_ValidLongQuery_Success() {
        String validLongQuery = "This is a valid search query that is longer than 2 characters";
        
        assertDoesNotThrow(() -> {
            validationUtil.validateSearchQuery(validLongQuery);
        });
    }

    @Test
    void validateSearchQuery_ExactlyTwoCharacters_Success() {
        String twoCharQuery = "ab";
        
        assertDoesNotThrow(() -> {
            validationUtil.validateSearchQuery(twoCharQuery);
        });
    }

    @Test
    void validateSearchQuery_ExactlyHundredCharacters_Success() {
        String hundredCharQuery = "a".repeat(100);
        
        assertDoesNotThrow(() -> {
            validationUtil.validateSearchQuery(hundredCharQuery);
        });
    }

    @Test
    void validateSearchQuery_QueryWithSpaces_Success() {
        String queryWithSpaces = "chicken curry";
        
        assertDoesNotThrow(() -> {
            validationUtil.validateSearchQuery(queryWithSpaces);
        });
    }

    @Test
    void validateSearchQuery_QueryWithLeadingTrailingSpaces_Success() {
        String queryWithSpaces = "  italian cuisine  ";
        
        assertDoesNotThrow(() -> {
            validationUtil.validateSearchQuery(queryWithSpaces);
        });
    }

    @Test
    void validateSearchQuery_TooShort_ThrowsException() {
        String shortQuery = "a";
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validationUtil.validateSearchQuery(shortQuery);
        });
        
        assertEquals("Search query must be at least 2 characters long.", exception.getMessage());
    }

    @Test
    void validateSearchQuery_EmptyAfterTrim_DoesNotThrow() {
        String emptyQuery = "";
        
        // Based on the updated logic, empty queries should not throw exception
        assertDoesNotThrow(() -> {
            validationUtil.validateSearchQuery(emptyQuery);
        });
    }

    @Test
    void validateSearchQuery_NullQuery_DoesNotThrow() {
        String nullQuery = null;
        
        // Based on the updated logic, null queries should not throw exception
        assertDoesNotThrow(() -> {
            validationUtil.validateSearchQuery(nullQuery);
        });
    }

    @Test
    void validateSearchQuery_WhitespaceOnly_DoesNotThrow() {
        String whitespaceQuery = "   ";
        
        // Based on the updated logic, whitespace-only queries should not throw exception
        assertDoesNotThrow(() -> {
            validationUtil.validateSearchQuery(whitespaceQuery);
        });
    }

    @Test
    void validateSearchQuery_OneCharacterAfterTrim_ThrowsException() {
        String oneCharAfterTrim = "  a  ";
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validationUtil.validateSearchQuery(oneCharAfterTrim);
        });
        
        assertEquals("Search query must be at least 2 characters long.", exception.getMessage());
    }

    @Test
    void validateSearchQuery_TooLong_ThrowsException() {
        String longQuery = "a".repeat(101);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validationUtil.validateSearchQuery(longQuery);
        });
        
        assertEquals("Search query must not exceed 100 characters.", exception.getMessage());
    }

    @Test
    void validateSearchQuery_LongQueryAfterTrim_ThrowsException() {
        String longQueryWithSpaces = "  " + "a".repeat(101) + "  ";
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validationUtil.validateSearchQuery(longQueryWithSpaces);
        });
        
        assertEquals("Search query must not exceed 100 characters.", exception.getMessage());
    }

    // Tests for validateRecipeId method

    @Test
    void validateRecipeId_ValidId_Success() {
        Long validId = 1L;
        
        assertDoesNotThrow(() -> {
            validationUtil.validateRecipeId(validId);
        });
    }

    @Test
    void validateRecipeId_LargeValidId_Success() {
        Long largeValidId = 999999L;
        
        assertDoesNotThrow(() -> {
            validationUtil.validateRecipeId(largeValidId);
        });
    }

    @Test
    void validateRecipeId_NullId_ThrowsException() {
        Long nullId = null;
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validationUtil.validateRecipeId(nullId);
        });
        
        assertEquals("Recipe ID must be a positive number.", exception.getMessage());
    }

    @Test
    void validateRecipeId_ZeroId_ThrowsException() {
        Long zeroId = 0L;
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validationUtil.validateRecipeId(zeroId);
        });
        
        assertEquals("Recipe ID must be a positive number.", exception.getMessage());
    }

    @Test
    void validateRecipeId_NegativeId_ThrowsException() {
        Long negativeId = -1L;
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validationUtil.validateRecipeId(negativeId);
        });
        
        assertEquals("Recipe ID must be a positive number.", exception.getMessage());
    }

    @Test
    void validateRecipeId_LargeNegativeId_ThrowsException() {
        Long largeNegativeId = -999999L;
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validationUtil.validateRecipeId(largeNegativeId);
        });
        
        assertEquals("Recipe ID must be a positive number.", exception.getMessage());
    }

    // Edge case tests

    @Test
    void validateSearchQuery_SpecialCharacters_Success() {
        String specialCharQuery = "café & pizza!";
        
        assertDoesNotThrow(() -> {
            validationUtil.validateSearchQuery(specialCharQuery);
        });
    }

    @Test
    void validateSearchQuery_UnicodeCharacters_Success() {
        String unicodeQuery = "café français";
        
        assertDoesNotThrow(() -> {
            validationUtil.validateSearchQuery(unicodeQuery);
        });
    }

    @Test
    void validateSearchQuery_Numbers_Success() {
        String numberQuery = "recipe123";
        
        assertDoesNotThrow(() -> {
            validationUtil.validateSearchQuery(numberQuery);
        });
    }

    @Test
    void validateRecipeId_MaxLongValue_Success() {
        Long maxId = Long.MAX_VALUE;
        
        assertDoesNotThrow(() -> {
            validationUtil.validateRecipeId(maxId);
        });
    }

    @Test
    void validateRecipeId_MinLongValue_ThrowsException() {
        Long minId = Long.MIN_VALUE;
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validationUtil.validateRecipeId(minId);
        });
        
        assertEquals("Recipe ID must be a positive number.", exception.getMessage());
    }
}
