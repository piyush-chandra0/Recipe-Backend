/**
 * TypeScript interface for Recipe data structure
 * This matches the backend RecipeDTO exactly
 * Place this in your Angular frontend project
 */
export interface Recipe {
    id: number;
    name: string;
    cuisine: string;
    tags: string[];
    cookTimeMinutes: number;
    image: string;
    rating: number;
    
    // Additional fields from DummyJSON API
    ingredients?: string[];
    instructions?: string[];
    prepTimeMinutes?: number;
    servings?: number;
    difficulty?: string;
    caloriesPerServing?: number;
    userId?: number;
    reviewCount?: number;
    mealType?: string[];
}

/**
 * API Response interfaces
 */
export interface ApiResponse<T> {
    data?: T;
    error?: string;
    message?: string;
}

export interface RecipeSearchResponse {
    recipes?: Recipe[];
    total?: number;
    skip?: number;
    limit?: number;
}

/**
 * Error response from backend
 */
export interface ErrorResponse {
    error: string;
    message: string;
}
