import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { BehaviorSubject, Observable } from "rxjs";
import { tap } from "rxjs/operators";
import { Recipient } from "../models/recipient.model";

@Injectable({
    providedIn: "root",
})
export class RecipientService {
    private currentUserSubject = new BehaviorSubject<string | null>(null);
    public currentUser$ = this.currentUserSubject.asObservable();

    private currentRecipientSubject = new BehaviorSubject<Recipient | null>(null);
    public currentRecipient$ = this.currentRecipientSubject.asObservable();
    private instanceId = Math.random();

    constructor(private http: HttpClient) {
        console.log("RecipientService constructor called. Instance ID:", this.instanceId);
        
        // Restore from sessionStorage if available
        const stored = sessionStorage.getItem('currentRecipient');
        if (stored) {
            const recipient = JSON.parse(stored);
            this.currentRecipientSubject.next(recipient);
        }
    }

    get currentUser(): string | null {
        return this.currentUserSubject.value;
    }

    get currentRecipient(): Recipient | null {
        console.log("Current Recipient:", this.currentRecipientSubject.value);
        return this.currentRecipientSubject.value;
    }

    setCurrentUser(user: string): void {
        this.currentUserSubject.next(user);
    }

    setCurrentRecipient(recipient: Recipient): void {
        this.currentRecipientSubject.next(recipient);
    }

    fetchRecipient(): Observable<Recipient> {
        return this.http
            .get<Recipient>("/api/recipient")
            .pipe(tap((recipient) => {
                console.log("Setting currentRecipient in service. Instance ID:", this.instanceId, "Recipient:", recipient);
                this.currentRecipientSubject.next(recipient);
                sessionStorage.setItem('currentRecipient', JSON.stringify(recipient));
            }));
    }

    fetchAllRecipients(year: number): Observable<Recipient[]> {
        return this.http.get<Recipient[]>(`/api/history/${year}`);
    }

    fetchHistoryYears(): Observable<number[]> {
        return this.http.get<number[]>("/api/history/years");
    }
}
