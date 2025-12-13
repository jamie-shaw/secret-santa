import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";
import { Gift } from "src/app/models/gift.model";
import { GiftService } from "src/app/services/gift.service";

@Component({
    selector: "app-gift-summary",
    imports: [CommonModule, RouterLink],
    templateUrl: "./gift-summary.component.html",
    styleUrl: "./gift-summary.component.css",
})
export class GiftSummaryComponent {
    gifts: Gift[] = [];

    constructor(private giftService: GiftService) {}

    ngOnInit() {
        this.giftService.fetchIdeasForSanta().subscribe({
            next: (gifts) => {
                this.gifts = gifts;
            },
            error: (err) => {
                console.error("Failed to fetch gift ideas for Santa", err);
            },
        });
    }
}
