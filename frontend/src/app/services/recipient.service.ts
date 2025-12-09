import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { BehaviorSubject, Observable } from "rxjs";
import { Recipient } from "../models/recipient.model";

@Injectable({
    providedIn: "root",
})
export class RecipientService {
    constructor(private http: HttpClient) {}

    getRecipient(): Observable<Recipient> {
        return this.http.get<Recipient>("/api/recipient");
    }

    getAllRecipients(year: number): Observable<Recipient[]> {
        return this.http.get<Recipient[]>(`/api/history/${year}`);
    }

    getHistoryYears(): Observable<number[]> {
        return this.http.get<number[]>("/api/history/years");
    }
}
