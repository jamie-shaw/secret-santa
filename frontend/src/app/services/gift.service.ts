import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { Gift } from "../models/gift.model";

@Injectable({
    providedIn: "root",
})
export class GiftService {
    constructor(private http: HttpClient) {}

    fetchIdeasForSanta(): Observable<Gift[]> {
        return this.http.get<Gift[]>("/api/gift/summary");
    }

    fetchIdeasFromRecipient(): Observable<Gift[]> {
        return this.http.get<Gift[]>("/api/idea/summary");
    }

    fetchGift(id: string): Observable<Gift> {
        return this.http.get<Gift>(`/api/gift/${id}`);
    }

    saveGift(gift: Gift): Observable<Gift> {
        return this.http.post<Gift>(`/api/gift/${gift.id}`, gift);
    }

    deleteGift(id: string): Observable<void> {
        return this.http.delete<void>(`/api/gift/${id}`);
    }
}
