import { Component } from "@angular/core";
import { environment } from "../../../environments/environment";
import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { FormsModule, NgForm } from "@angular/forms";
import { Router } from "@angular/router";

@Component({
    selector: "app-login",
    standalone: true,
    imports: [FormsModule],
    templateUrl: "./login.component.html",
    styleUrl: "./login.component.css",
})
export class LoginComponent {
    apiUrl = environment.apiUrl;
    edition = "fernald"; // Default edition value

    constructor(
        private http: HttpClient,
        private router: Router,
    ) {}
    onSubmit(form: NgForm) {
        const { username, password, edition } = form.value;
        this.http.post(`/api/auth/login`, { username, password, edition }).subscribe({
            next: (response) => {
                // Success (2xx status codes)
                if (response && (response as any).token) {
                    console.log("Login successful:", response);
                    this.router.navigate(["/home"]);
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
