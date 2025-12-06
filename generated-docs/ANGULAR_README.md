# Secret Santa - Angular Frontend Integration

This project now includes an embedded Angular application that runs within the Spring Boot application.

## Project Structure

```
frontend/                           # Angular application root
├── src/                           # Angular source code
│   ├── app/                       # Angular components
│   │   ├── app.component.ts      # Main app component
│   │   ├── app.component.html    # Main app template
│   │   ├── app.component.css     # Main app styles
│   │   ├── app.module.ts         # Main app module
│   │   └── app-routing.module.ts # Angular routing
│   ├── index.html                # Main HTML file
│   ├── main.ts                   # Angular bootstrap
│   └── styles.css                # Global styles
├── angular.json                  # Angular CLI configuration
├── package.json                  # Node dependencies
└── tsconfig.json                 # TypeScript configuration
```

## Build Process

The Angular application is automatically built during the Maven build process using the `frontend-maven-plugin`.

### Maven Build Steps:
1. **Install Node.js and npm** - Downloads and installs Node.js v20.10.0 and npm 10.2.5
2. **Install npm dependencies** - Runs `npm install` to install all Angular dependencies
3. **Build Angular app** - Runs `npm run build:prod` to build the Angular app
4. **Output** - The built Angular files are placed in `src/main/resources/static/app/`

### Building the Application

To build the entire application (Spring Boot + Angular):
```bash
mvn clean package
```

This will:
- Compile the Java code
- Build the Angular application
- Package everything into a single JAR file

## Development

### Development with Angular CLI (recommended for frontend development)

1. **Install dependencies** (first time only):
   ```bash
   cd frontend
   npm install
   ```

2. **Run Angular development server**:
   ```bash
   cd frontend
   npm start
   ```
   The Angular app will be available at `http://localhost:4200`

3. **Configure proxy** (optional): Create `frontend/proxy.conf.json` to proxy API calls to your Spring Boot backend:
   ```json
   {
     "/api": {
       "target": "http://localhost:8080",
       "secure": false
     }
   }
   ```
   Then update `package.json` start script:
   ```json
   "start": "ng serve --proxy-config proxy.conf.json"
   ```

### Development with Spring Boot

1. Build the Angular app first:
   ```bash
   cd frontend
   npm install
   npm run build:prod
   ```

2. Run Spring Boot:
   ```bash
   mvn spring-boot:run
   ```

The Angular app will be available at `http://localhost:8080/app/`

## Accessing the Angular Application

Once the application is running:
- **Angular App**: `http://localhost:8080/app/` or `http://localhost:8080/ng` (redirects to /app/)
- **Spring Boot JSP Views**: `http://localhost:8080/` (existing functionality)

The `/app/` path serves the Angular application with client-side routing support. Any routes defined in Angular (like `/app/about`, `/app/gifts`, etc.) will be handled by the Angular router.

## Adding New Features

### Creating Angular Components

```bash
cd frontend
npx ng generate component components/my-component
```

### Creating Angular Services

```bash
cd frontend
npx ng generate service services/my-service
```

### Adding Routes

Edit `frontend/src/app/app-routing.module.ts`:
```typescript
const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'about', component: AboutComponent }
];
```

## API Integration

To call Spring Boot REST APIs from Angular:

1. **Create a service**:
   ```typescript
   import { HttpClient } from '@angular/common/http';
   
   @Injectable({
     providedIn: 'root'
   })
   export class SantaService {
     constructor(private http: HttpClient) {}
     
     getGifts() {
       return this.http.get('/api/gifts');
     }
   }
   ```

2. **Use the service in components**:
   ```typescript
   export class MyComponent {
     constructor(private santaService: SantaService) {}
     
     ngOnInit() {
       this.santaService.getGifts().subscribe(data => {
         console.log(data);
       });
     }
   }
   ```

## Security Considerations

- The `/app/**` route is configured to be publicly accessible in `SecurityConfig.java`
- Modify `SecurityConfig.java` if you need to add authentication to the Angular routes
- API endpoints should implement proper authentication and authorization

## Troubleshooting

### Build Issues

If Maven build fails:
1. Clear the frontend node_modules: `cd frontend && rm -rf node_modules`
2. Clear Maven target: `mvn clean`
3. Rebuild: `mvn package`

### Angular Development Server Issues

If `npm start` fails:
1. Delete `node_modules` and `package-lock.json`
2. Run `npm install` again
3. Try `npm start`

## Production Deployment

The production build is optimized and minified automatically. The JAR file contains:
- All Spring Boot application code
- Compiled Angular application in `/static/app/`
- All necessary dependencies

Deploy the JAR file as usual:
```bash
java -jar target/api-0.0.1-SNAPSHOT.jar
```

## Notes

- The Angular app is served from `/app/` to avoid conflicts with existing JSP views
- Both the Angular app and JSP views can coexist
- You can gradually migrate features from JSP to Angular
- The frontend-maven-plugin caches Node.js installation in the `target/` directory
