# Login Endpoint Implementation

## Overview
A JWT-based authentication system has been added to the Secret Santa API to support secure login for the Angular frontend while maintaining backward compatibility with the existing JSP-based authentication.

## Features Implemented

### 1. **JWT Authentication**
- Token-based authentication using JSON Web Tokens (JWT)
- Tokens expire after 24 hours (configurable)
- Stateless authentication for API endpoints
- Secure token generation and validation

### 2. **Login Endpoint**
**URL:** `POST /api/auth/login`

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "john_doe",
  "displayName": "John Doe",
  "email": "john@example.com"
}
```

**Error Response (401 Unauthorized):**
```json
{
  "message": "Invalid username or password",
  "status": 401
}
```

### 3. **Logout Endpoint**
**URL:** `POST /api/auth/logout`

**Response (200 OK):**
```json
{
  "message": "Logged out successfully"
}
```

### 4. **Get Current User**
**URL:** `GET /api/auth/me`

**Headers:**
```
Authorization: Bearer <token>
```

**Success Response (200 OK):**
```json
{
  "userName": "john_doe",
  "displayName": "John Doe",
  "email": "john@example.com",
  "enabled": true
}
```

## Files Created/Modified

### New Files Created:

1. **DTOs (Data Transfer Objects):**
   - `src/main/java/com/secretsanta/api/dto/LoginRequest.java` - Login request model
   - `src/main/java/com/secretsanta/api/dto/LoginResponse.java` - Login response model
   - `src/main/java/com/secretsanta/api/dto/ErrorResponse.java` - Error response model

2. **Security Components:**
   - `src/main/java/com/secretsanta/api/security/JwtTokenProvider.java` - JWT token generation and validation
   - `src/main/java/com/secretsanta/api/security/JwtAuthenticationFilter.java` - JWT authentication filter

3. **Controller:**
   - `src/main/java/com/secretsanta/api/controller/AuthController.java` - Authentication endpoints

### Modified Files:

1. **pom.xml**
   - Added JWT dependencies (jjwt-api, jjwt-impl, jjwt-jackson)

2. **SecurityConfig.java**
   - Added AuthenticationManager bean
   - Configured JWT authentication filter
   - Configured public endpoints for login/logout
   - Maintained session-based authentication for JSP pages

3. **application.properties**
   - Added JWT secret key configuration
   - Added JWT expiration time configuration

## Configuration

### JWT Settings (application.properties)
```properties
# JWT Configuration
jwt.secret=MySecretKeyForSecretSantaApplicationThatIsLongEnoughForHS256Algorithm
jwt.expiration=86400000  # 24 hours in milliseconds
```

**Security Note:** In production, the JWT secret should be stored in environment variables or a secure vault, not in the properties file.

## Security Configuration

### Public Endpoints (No Authentication Required)
- `/api/auth/login` - Login endpoint
- `/api/auth/logout` - Logout endpoint
- `/api/status` - API status check
- `/api/health` - Health check
- `/app/**` - Angular application files
- Static resources (CSS, JS, images, fonts)

### Protected Endpoints (Authentication Required)
- `/api/**` - All other API endpoints require JWT token
- JSP pages require session-based authentication

## Usage Example

### 1. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password"
  }'
```

### 2. Access Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/protected-endpoint \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 3. Get Current User
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 4. Logout
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## Angular Integration

To use the login endpoint in your Angular application:

1. **Create an Auth Service:**
```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = '/api/auth';

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, { username, password });
  }

  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, {});
  }

  getCurrentUser(): Observable<any> {
    return this.http.get(`${this.apiUrl}/me`);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  setToken(token: string): void {
    localStorage.setItem('token', token);
  }

  removeToken(): void {
    localStorage.removeItem('token');
  }
}
```

2. **Create an HTTP Interceptor:**
```typescript
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();
    
    if (token) {
      const cloned = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
      return next.handle(cloned);
    }
    
    return next.handle(req);
  }
}
```

## Testing

### Manual Testing
1. Start the application
2. Send a POST request to `/api/auth/login` with valid credentials
3. Copy the token from the response
4. Use the token in the `Authorization` header for subsequent requests

### Integration with Existing System
- The login endpoint works alongside the existing form-based login for JSP pages
- JSP pages continue to use session-based authentication
- API endpoints use JWT token authentication
- Both authentication methods share the same UserDetailsService

## Troubleshooting

### Common Issues

1. **401 Unauthorized on login**
   - Verify credentials are correct
   - Check that the user exists in the database
   - Verify password encoding matches

2. **Token validation fails**
   - Ensure JWT secret is configured correctly
   - Check token hasn't expired
   - Verify token format (should start with "Bearer ")

3. **JSP pages not loading**
   - The session management was restored to default (stateful) to support JSP pages
   - Form login continues to work as before

## Next Steps

1. **Production Deployment:**
   - Move JWT secret to environment variables
   - Configure appropriate token expiration times
   - Set up HTTPS for secure token transmission

2. **Enhancements:**
   - Implement refresh token mechanism
   - Add rate limiting for login attempts
   - Implement password reset via API
   - Add user registration endpoint

## Dependencies

```xml
<!-- JWT Support -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```
