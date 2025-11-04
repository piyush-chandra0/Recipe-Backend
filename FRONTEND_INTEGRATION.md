# Frontend-Backend API Compatibility Guide

## Overview
This document outlines the exact API contract between the Angular 19 frontend and Spring Boot backend for the Recipe application.

## API Endpoints

### 1. Get All Recipes
**Frontend Call:**
```typescript
getAllRecipes(): Observable<any> {
  return this.http.get<any>(this.apiUrl);
}
```

**Backend Endpoint:**
```
GET http://localhost:8080/api/recipes
```

**Response Format:**
```json
[
  {
    "id": 1,
    "name": "Classic Margherita Pizza",
    "cuisine": "Italian",
    "tags": ["Pizza", "Italian"],
    "cookTimeMinutes": 15,
    "image": "https://cdn.dummyjson.com/recipe-images/1.webp",
    "rating": 4.6,
    "ingredients": ["Pizza dough", "Tomato sauce", ...],
    "instructions": ["Preheat the oven...", ...],
    "prepTimeMinutes": 20,
    "servings": 4,
    "difficulty": "Easy",
    "caloriesPerServing": 300,
    "userId": 166,
    "reviewCount": 98,
    "mealType": ["Dinner"]
  }
]
```

### 2. Search Recipes
**Frontend Call:**
```typescript
searchRecipes(query: string): Observable<any> {
  const params = new HttpParams().set('q', query);
  return this.http.get<any>(`${this.apiUrl}/search`, { params });
}
```

**Backend Endpoint:**
```
GET http://localhost:8080/api/recipes/search?q={searchTerm}
```

**Parameters:**
- `q` (string, optional): Search term for recipe name, cuisine, tags, or ingredients

**Response Format:** Same as Get All Recipes

### 3. Get Recipe by ID
**Frontend Call:**
```typescript
getRecipeById(id: number | string): Observable<Recipe> {
  return this.http.get<Recipe>(`${this.apiUrl}/${id}`);
}
```

**Backend Endpoint:**
```
GET http://localhost:8080/api/recipes/{id}
```

**Response Format:** Single Recipe object (same structure as above)

### 4. Manual Data Load (Additional Backend Endpoint)
**Backend Only:**
```
POST http://localhost:8080/api/recipes/load
```

**Response Format:**
```json
{
  "message": "Successfully loaded recipes from external API",
  "count": 50
}
```

## Data Model Alignment

### Complete Recipe Interface (TypeScript)
```typescript
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
```

### Backend RecipeDTO (Java)
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO {
    private Long id;
    private String name;
    private Integer cookTimeMinutes;
    private Integer prepTimeMinutes;
    private Integer servings;
    private String difficulty;
    private List<String> tags;
    private List<String> ingredients;
    private List<String> instructions;
    private String cuisine;
    private String image;
    private Double rating;
    private Integer reviewCount;
    private Integer caloriesPerServing;
    private Integer userId;
    private List<String> mealType;
}
```

## Error Handling

### Backend Error Response Format
```json
{
  "error": "Recipe not found",
  "message": "Recipe not found with ID: 999"
}
```

### Frontend Error Handling
```typescript
private handleError(error: HttpErrorResponse): Observable<never> {
  let errorMessage = 'An unknown error occurred';
  
  if (error.error instanceof ErrorEvent) {
    // Client-side error
    errorMessage = `Error: ${error.error.message}`;
  } else {
    // Server-side error
    errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
  }
  
  console.error(errorMessage);
  return throwError(() => new Error(errorMessage));
}
```

## Key Changes Made for Compatibility

1. **Search Parameter**: Changed from `query` to `q` to match frontend expectation
2. **Field Alignment**: Added missing fields (`userId`, `mealType`) to backend
3. **Data Types**: Ensured numeric fields use appropriate types (Long/Integer in Java, number in TypeScript)
4. **Optional Fields**: Made appropriate fields optional in TypeScript interface
5. **Error Responses**: Standardized error response format

## Testing the Integration

### Frontend Service Test
```typescript
// Test in Angular component or service
this.recipeService.getAllRecipes().subscribe(
  recipes => console.log('All recipes:', recipes),
  error => console.error('Error:', error)
);

this.recipeService.searchRecipes('pizza').subscribe(
  recipes => console.log('Search results:', recipes),
  error => console.error('Search error:', error)
);

this.recipeService.getRecipeById(1).subscribe(
  recipe => console.log('Recipe details:', recipe),
  error => console.error('Recipe error:', error)
);
```

### Backend Testing URLs
```
GET http://localhost:8080/api/recipes
GET http://localhost:8080/api/recipes/search?q=pizza
GET http://localhost:8080/api/recipes/1
POST http://localhost:8080/api/recipes/load
```

## CORS Configuration
The backend is already configured for Angular frontend:
```java
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
```

This allows your Angular development server to communicate with the Spring Boot backend without CORS issues.
