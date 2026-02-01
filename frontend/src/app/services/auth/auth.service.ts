import { Injectable } from "@angular/core";

/**
 * Service to manage JWT token storage and retrieval
 */
@Injectable({
  providedIn: "root",
})
export class AuthService {
  private readonly TOKEN_KEY = "jwt_token";

  /**
   * Save JWT token to localStorage
   */
  setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  /**
   * Get JWT token from localStorage
   */
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Remove JWT token from localStorage
   */
  clearToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  /**
   * Check if token exists
   */
  hasToken(): boolean {
    return !!this.getToken();
  }
}
