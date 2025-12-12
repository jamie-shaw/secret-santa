import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { Gift } from "../models/gift.model";

@Injectable({
    providedIn: "root",
})
export class GiftService {
    constructor(private http: HttpClient) {}

    getIdeasForSanta(): Observable<Gift[]> {
        return this.http.get<Gift[]>("/api/idea/summary");
    }
}
