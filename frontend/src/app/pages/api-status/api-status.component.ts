import { Component, OnInit } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-api-status",
  standalone: true,
  imports: [CommonModule],
  templateUrl: "./api-status.component.html",
  styleUrl: "./api-status.component.css",
})
export class ApiStatusComponent implements OnInit {
  apiStatus: any = null;
  loading = false;
  error: string | null = null;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.checkApiStatus();
  }

  checkApiStatus() {
    this.loading = true;
    this.error = null;

    this.http.get("/api/status").subscribe({
      next: (data) => {
        this.apiStatus = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = "Failed to connect to API";
        this.loading = false;
        console.error("API Error:", err);
      },
    });
  }
}
