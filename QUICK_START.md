# Recipe Backend - Quick Start Guide

## Overview
Spring Boot backend for Recipe application with Angular 19 frontend integration.

## Starting the Application

### Option 1: Using Maven Wrapper (Recommended)
```powershell
cd "c:\Users\jchand43\LnD\Backend\recipe"
.\mvnw.cmd spring-boot:run
```

### Option 2: Using IDE
1. Open the project in IntelliJ IDEA or Eclipse
2. Run `RecipeApplication.java` main method
3. Application will start on `http://localhost:8080`

### Option 3: Build and Run JAR
```powershell
cd "c:\Users\jchand43\LnD\Backend\recipe"
.\mvnw.cmd clean package
java -jar target\recipe-0.0.1-SNAPSHOT.jar
```

## Application Features

### ✅ Automatic Data Loading
- Fetches 50 recipes from DummyJSON API on startup
- Stores in H2 in-memory database
- Handles external API failures gracefully

### ✅ REST API Endpoints
- `GET /api/recipes` - Get all recipes
- `GET /api/recipes/search?q={query}` - Search recipes
- `GET /api/recipes/{id}` - Get recipe by ID
- `POST /api/recipes/load` - Manual data reload

### ✅ Enhanced Search
- Searches across: name, cuisine, tags, ingredients
- Case-insensitive
- Supports partial matches

### ✅ Complete Recipe Data
- All fields from DummyJSON API
- Ingredients, instructions, nutritional info
- User ratings and reviews

## Database Access

### H2 Console
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:recipedb`
- Username: `sa`
- Password: (empty)

### Database Tables
- `recipes` - Main recipe data
- `recipe_tags` - Recipe tags
- `recipe_ingredients` - Recipe ingredients  
- `recipe_instructions` - Step-by-step instructions
- `recipe_meal_types` - Meal type classifications

## API Testing

### Quick Tests (PowerShell)
```powershell
# Test if server is running
Invoke-RestMethod -Uri "http://localhost:8080/api/recipes" -Method GET | Select-Object -First 1

# Search for pizza
Invoke-RestMethod -Uri "http://localhost:8080/api/recipes/search?q=pizza" -Method GET

# Get specific recipe
Invoke-RestMethod -Uri "http://localhost:8080/api/recipes/1" -Method GET

# Reload data
Invoke-RestMethod -Uri "http://localhost:8080/api/recipes/load" -Method POST
```

## Configuration

### Key Properties (application.properties)
```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:h2:mem:recipedb
spring.h2.console.enabled=true

# External API
external.api.recipes.base-url=https://dummyjson.com
external.api.recipes.timeout=30s

# Logging
logging.level.com.recipe_project=INFO
```

## Angular Frontend Integration

### CORS Configuration
✅ Pre-configured for `http://localhost:4200`

### API Endpoints Match Frontend Expectations
✅ Search parameter: `q` (not `query`)
✅ Response format: Direct array for lists
✅ Error format: `{error: string, message: string}`

### Sample Angular Service Call
```typescript
// This will work perfectly with your backend
this.http.get<Recipe[]>('http://localhost:8080/api/recipes')
this.http.get<Recipe[]>('http://localhost:8080/api/recipes/search?q=pizza')
this.http.get<Recipe>('http://localhost:8080/api/recipes/1')
```

## Monitoring & Logs

### Application Logs
- Startup: Data loading progress
- Requests: API call logging
- Errors: Comprehensive error details

### Health Check
```powershell
# Simple health check
Invoke-RestMethod -Uri "http://localhost:8080/api/recipes" -Method GET | Measure-Object | Select-Object Count
```

## Troubleshooting

### Common Issues

1. **Port 8080 in use**
   ```properties
   # Change in application.properties
   server.port=8081
   ```

2. **External API timeout**
   ```properties
   # Increase timeout in application.properties
   external.api.recipes.timeout=60s
   ```

3. **No recipes loaded**
   ```powershell
   # Manually trigger reload
   Invoke-RestMethod -Uri "http://localhost:8080/api/recipes/load" -Method POST
   ```

4. **CORS issues**
   - Already configured for Angular on port 4200
   - Check Angular is running on correct port

## Development Notes

### Code Quality Features
- ✅ Lombok for reduced boilerplate
- ✅ Comprehensive error handling
- ✅ Consistent logging (SLF4J)
- ✅ Validation utilities
- ✅ Retry logic for external API
- ✅ Transaction management

### Testing
- Unit tests in `src/test/java`
- Integration test framework ready
- H2 in-memory database for testing

## Next Steps

1. **Start the Backend**: `.\mvnw.cmd spring-boot:run`
2. **Verify API**: Test endpoints with PowerShell commands above
3. **Connect Frontend**: Your Angular app should work immediately
4. **Monitor Logs**: Check console for any issues

Your Recipe backend is now fully compatible with Angular 19 frontend and ready for production use! 🚀
