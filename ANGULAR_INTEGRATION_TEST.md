# Angular Frontend Integration Testing Guide

## Prerequisites
1. Start the Spring Boot backend on `http://localhost:8080`
2. Ensure your Angular frontend is running on `http://localhost:4200`

## API Testing Commands

### 1. Test Get All Recipes
```powershell
# Using PowerShell Invoke-RestMethod
Invoke-RestMethod -Uri "http://localhost:8080/api/recipes" -Method GET | ConvertTo-Json -Depth 4
```

### 2. Test Search Recipes (matches Angular parameter 'q')
```powershell
# Search for pizza recipes
Invoke-RestMethod -Uri "http://localhost:8080/api/recipes/search?q=pizza" -Method GET | ConvertTo-Json -Depth 4

# Search for Italian cuisine
Invoke-RestMethod -Uri "http://localhost:8080/api/recipes/search?q=italian" -Method GET | ConvertTo-Json -Depth 4

# Empty search (should return all recipes)
Invoke-RestMethod -Uri "http://localhost:8080/api/recipes/search" -Method GET | ConvertTo-Json -Depth 4
```

### 3. Test Get Recipe by ID
```powershell
# Get recipe with ID 1
Invoke-RestMethod -Uri "http://localhost:8080/api/recipes/1" -Method GET | ConvertTo-Json -Depth 4
```

### 4. Test Manual Data Loading
```powershell
# Trigger loading from external API
Invoke-RestMethod -Uri "http://localhost:8080/api/recipes/load" -Method POST | ConvertTo-Json -Depth 4
```

### 5. Test Error Handling
```powershell
# Test non-existent recipe (should return 404)
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/recipes/999" -Method GET
} catch {
    Write-Host "Expected 404 error: $($_.Exception.Response.StatusCode)"
}
```

## Angular Service Validation

### Updated Angular Service (should work perfectly)
```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class RecipeService {
  private apiUrl = 'http://localhost:8080/api/recipes';

  constructor(private http: HttpClient) { }

  // ✅ This will work - matches backend GET /api/recipes
  getAllRecipes(): Observable<Recipe[]> {
    return this.http.get<Recipe[]>(this.apiUrl)
      .pipe(
        catchError(this.handleError)
      );
  }

  // ✅ This will work - backend now accepts 'q' parameter
  searchRecipes(query: string): Observable<Recipe[]> {
    const params = new HttpParams().set('q', query);
    
    return this.http.get<Recipe[]>(`${this.apiUrl}/search`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  // ✅ This will work - matches backend GET /api/recipes/{id}
  getRecipeById(id: number | string): Observable<Recipe> { 
    return this.http.get<Recipe>(`${this.apiUrl}/${id}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  // ✅ Additional method for manual data loading
  loadRecipesFromApi(): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/load`, {})
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
      
      // Handle backend error response format
      if (error.error && error.error.message) {
        errorMessage = error.error.message;
      }
    }
    
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}
```

### Updated Recipe Interface (place in your Angular project)
```typescript
export interface Recipe {
    id: number;
    name: string;
    cuisine: string;
    tags: string[];
    cookTimeMinutes: number;
    image: string;
    rating: number;
    
    // Additional fields that backend now provides
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

## Component Usage Examples

### Recipe List Component
```typescript
export class RecipeListComponent implements OnInit {
  recipes: Recipe[] = [];
  loading = false;
  error: string | null = null;

  constructor(private recipeService: RecipeService) { }

  ngOnInit() {
    this.loadAllRecipes();
  }

  loadAllRecipes() {
    this.loading = true;
    this.error = null;
    
    this.recipeService.getAllRecipes().subscribe({
      next: (recipes) => {
        this.recipes = recipes;
        this.loading = false;
      },
      error: (error) => {
        this.error = error.message;
        this.loading = false;
      }
    });
  }

  searchRecipes(query: string) {
    if (!query.trim()) {
      this.loadAllRecipes();
      return;
    }

    this.loading = true;
    this.error = null;
    
    this.recipeService.searchRecipes(query).subscribe({
      next: (recipes) => {
        this.recipes = recipes;
        this.loading = false;
      },
      error: (error) => {
        this.error = error.message;
        this.loading = false;
      }
    });
  }
}
```

### Recipe Detail Component
```typescript
export class RecipeDetailComponent implements OnInit {
  recipe: Recipe | null = null;
  loading = false;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private recipeService: RecipeService
  ) { }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadRecipe(id);
    }
  }

  loadRecipe(id: string | number) {
    this.loading = true;
    this.error = null;
    
    this.recipeService.getRecipeById(id).subscribe({
      next: (recipe) => {
        this.recipe = recipe;
        this.loading = false;
      },
      error: (error) => {
        this.error = error.message;
        this.loading = false;
      }
    });
  }
}
```

## Expected API Responses

### GET /api/recipes
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
    "ingredients": ["Pizza dough", "Tomato sauce", "Fresh mozzarella cheese", "Fresh basil leaves", "Olive oil", "Salt and pepper to taste"],
    "instructions": ["Preheat the oven to 475°F (245°C).", "Roll out the pizza dough and spread tomato sauce evenly.", "Top with slices of fresh mozzarella and fresh basil leaves.", "Drizzle with olive oil and season with salt and pepper.", "Bake in the preheated oven for 12-15 minutes or until the crust is golden brown.", "Slice and serve hot."],
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

### GET /api/recipes/search?q=pizza
```json
[
  {
    "id": 1,
    "name": "Classic Margherita Pizza",
    "cuisine": "Italian",
    "tags": ["Pizza", "Italian"],
    // ... rest of recipe data
  }
]
```

### POST /api/recipes/load
```json
{
  "message": "Successfully loaded recipes from external API",
  "count": 50
}
```

## Troubleshooting

### Common Issues and Solutions

1. **CORS Errors**
   - ✅ Already configured: `@CrossOrigin(origins = "http://localhost:4200")`

2. **404 on Search**
   - ✅ Fixed: Backend now accepts `q` parameter instead of `query`

3. **Missing Fields in Response**
   - ✅ Fixed: All fields from external API are now included

4. **Type Mismatches**
   - ✅ Fixed: Java types align with TypeScript interface

5. **Search Not Working**
   - ✅ Enhanced: Now searches name, cuisine, tags, and ingredients

## Verification Checklist

- ✅ Backend accepts `q` parameter for search
- ✅ All Recipe fields are included in response
- ✅ CORS is configured for Angular frontend
- ✅ Error responses follow consistent format
- ✅ Optional fields are properly handled
- ✅ Data types match between backend and frontend
- ✅ Search functionality enhanced (name, cuisine, tags, ingredients)
- ✅ Manual data loading endpoint available

Your Angular frontend should now work seamlessly with the Spring Boot backend!
