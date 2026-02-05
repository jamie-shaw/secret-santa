import { Component, OnInit } from "@angular/core";
import { environment } from "../../../environments/environment";
import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { FormsModule, NgForm } from "@angular/forms";
import { Router } from "@angular/router";
import { RecipientService } from "../../services/recipient/recipient.service";
import { AuthService } from "../../services/auth/auth.service";

@Component({
    selector: "app-login",
    imports: [FormsModule],
    templateUrl: "./login.component.html",
    styleUrl: "./login.component.css",
})
export class LoginComponent implements OnInit {
    apiUrl = environment.apiUrl;
    edition = "fernald"; // Default edition value

    constructor(
        private http: HttpClient,
        private router: Router,
        private recipientService: RecipientService,
        private authService: AuthService,
    ) {}

    ngOnInit() {
        // Clear token if navigating to logout route
        if (this.router.url === "/logout") {
            this.authService.clearToken();
        }
    }
    onSubmit(form: NgForm) {
        const { username, password, edition } = form.value;
        this.http.post(`/api/auth/login`, { username, password, edition }).subscribe({
            next: (response) => {
                // Success (2xx status codes)
                if (response && (response as any).token) {
                    console.log("Login successful:", response);

                    // Store the JWT token
                    this.authService.setToken((response as any).token);

                    // Fetch recipient data after successful login
                    this.recipientService.fetchRecipient().subscribe({
                        next: (recipient) => {
                            console.log("Recipient fetched after login:", recipient);
                            this.router.navigate(["/home"]);
                        },
                        error: (error) => {
                            console.error("Error fetching recipient after login:", error);
                            this.router.navigate(["/home"]);
                        },
                    });
                }
            },
            error: (error: HttpErrorResponse) => {
                // Error (4xx, 5xx status codes)
                console.error("Login failed. Status:", error.status);
                console.error("Error message:", error.message);
            },
        });
    }
}
