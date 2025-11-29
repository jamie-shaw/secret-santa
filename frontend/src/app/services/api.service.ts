import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";

/**
 * Service to interact with the Secret Santa API
 */
@Injectable({
  providedIn: "root",
})
export class ApiService {
  private baseUrl = "/api";

  constructor(private http: HttpClient) {}

  /**
   * Example method to get data from the API
   * Replace with your actual API endpoints
   */
  getData(): Observable<any> {
    return this.http.get(`${this.baseUrl}/data`);
  }

  /**
   * Example POST request
   */
  postData(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/data`, data);
  }
}
