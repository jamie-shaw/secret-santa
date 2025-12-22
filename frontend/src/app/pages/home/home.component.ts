import { Component } from "@angular/core";

import { RouterLink } from "@angular/router";
import { RecipientService } from "src/app/services/recipient.service";
import { Recipient } from "src/app/models/recipient.model";

@Component({
    selector: "app-home",
    imports: [RouterLink],
    templateUrl: "./home.component.html",
    styleUrl: "./home.component.css",
})
export class HomeComponent {
    recipient: Recipient | null = null;
    public daysRemaining: number = 0;

    constructor(private recipientService: RecipientService) {}

    ngOnInit() {
        this.recipient = this.recipientService.currentRecipient;
        this.daysRemaining = this.calculateDaysUntilChristmas();
    }

    private calculateDaysUntilChristmas(): number {
        const today = new Date();
        const currentYear = today.getFullYear();
        const christmas = new Date(currentYear, 11, 25); // December 25th

        if (today > christmas) {
            // If today is after Christmas, calculate for next year
            christmas.setFullYear(currentYear + 1);
        }

        const diffTime = christmas.getTime() - today.getTime();
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        return diffDays;
    }
}
