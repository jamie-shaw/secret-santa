import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";

export interface EmailRequest {
    addressee: string;
    message: string;
}

@Injectable({
    providedIn: "root",
})
export class EmailService {
    private apiUrl = "/api/email";

    constructor(private http: HttpClient) {}

    sendEmail(emailRequest: EmailRequest): Observable<any> {
        return this.http.post(`${this.apiUrl}/send`, emailRequest);
    }
}
