import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";
import { BehaviorSubject } from "rxjs";
import { Gift } from "src/app/models/gift.model";
import { GiftService } from "src/app/services/gift.service";

@Component({
    selector: "app-gift-summary",
    imports: [CommonModule, RouterLink],
    templateUrl: "./gift-summary.component.html",
    styleUrl: "./gift-summary.component.css",
})
export class GiftSummaryComponent {
    loading$ = new BehaviorSubject<boolean>(true);
    error: string | null = null;

    gifts: Gift[] = [];

    constructor(private giftService: GiftService) {}

    ngOnInit() {
        this.giftService.fetchIdeasForSanta().subscribe({
            next: (gifts) => {
                this.gifts = gifts;
                this.loading$.next(false);
            },
            error: (err) => {
                this.loading$.next(false);
                this.error = "Failed to fetch gift ideas for Santa";
                console.error(this.error, err);
            },
        });
    }
}
