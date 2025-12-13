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
}
