# Quick Start Guide - Angular Integration

## First Time Setup

### Option 1: Full Maven Build (Recommended)

This will automatically install Node.js, npm, and build everything:

```bash
mvn clean package
```

Then run:
```bash
java -jar target/api-0.0.1-SNAPSHOT.jar
```

Access the Angular app at: `http://localhost:8080/app/`

### Option 2: Manual Frontend Development

For active frontend development:

1. **Install npm dependencies**:
   ```bash
   cd frontend
   npm install
   ```

2. **Build the Angular app**:
   ```bash
   npm run build:prod
   ```

3. **Run Spring Boot**:
   ```bash
   cd ..
   mvn spring-boot:run
   ```

Access at: `http://localhost:8080/app/`

## Development Workflow

### For Frontend Changes

Use the Angular dev server with proxy:

```bash
cd frontend
npm start
```

This will:
- Start Angular dev server at `http://localhost:4200`
- Proxy API calls to `http://localhost:8080`
- Auto-reload on file changes

Make sure your Spring Boot app is running on port 8080.

### For Backend Changes

Just restart Spring Boot. The Angular app is static after build.

## Verify Installation

1. **Check if Angular app built correctly**:
   - Look for files in `src/main/resources/static/app/`
   - Should contain: `index.html`, `main-*.js`, `polyfills-*.js`, etc.

2. **Test the API connection**:
   - Visit `http://localhost:8080/api/status`
   - Should return JSON: `{"status":"ok","message":"Secret Santa API is running",...}`

3. **Test Angular app**:
   - Visit `http://localhost:8080/app/`
   - Should see "Welcome to Secret Santa!" with API status

## Troubleshooting

### "No message available - StackOverflowError"

This was fixed! The issue was an infinite redirect loop. The fix:
- `AngularWebConfig.java` now properly handles static resources and client-side routing
- `/app/**` paths are served as static resources with fallback to index.html

### Angular App Not Found (404)

1. Make sure you built the Angular app:
   ```bash
   cd frontend
   npm install
   npm run build:prod
   ```

2. Check that files exist in `src/main/resources/static/app/`

3. Rebuild the Spring Boot app to include the static files

### API Calls Failing

1. Check SecurityConfig allows `/api/**` requests
2. Verify Spring Boot is running
3. Check browser console for CORS errors

### Maven Build Fails

1. Clear everything:
   ```bash
   mvn clean
   cd frontend
   rd /s /q node_modules
   del package-lock.json
   cd ..
   ```

2. Rebuild:
   ```bash
   mvn package
   ```

## What Was Created

### Java Files
- `ApiController.java` - REST API endpoints for Angular
- `AngularController.java` - Convenience redirect from /ng to /app/
- `AngularWebConfig.java` - Configures static resource handling and Angular routing
- `SecurityConfig.java` - Updated to allow /app/** and /api/** access

### Angular Files
- Complete Angular 17 application in `frontend/` directory
- Includes example API integration
- Production build outputs to `src/main/resources/static/app/`

### Configuration
- `pom.xml` - Added frontend-maven-plugin for automatic builds
- Angular proxy configuration for development

## Next Steps

1. Start building your Angular components in `frontend/src/app/`
2. Add REST API endpoints in `ApiController.java`
3. Update `SecurityConfig.java` if you need authentication for certain routes
4. Gradually migrate features from JSP to Angular
