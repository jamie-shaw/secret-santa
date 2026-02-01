import { Component, OnDestroy } from "@angular/core";

import { RouterLink } from "@angular/router";
import { RecipientService } from "src/app/services/recipient/recipient.service";
import { Recipient } from "src/app/models/recipient.model";
import { MessageService } from "src/app/services/message/message.service";
import { ToastModule } from "primeng/toast";
import { filter, take } from "rxjs/operators";

@Component({
    selector: "app-home",
    imports: [RouterLink, ToastModule],
    templateUrl: "./home.component.html",
    styleUrl: "./home.component.css",
})
export class HomeComponent implements OnDestroy {
    recipient: Recipient | null = null;
    public daysRemaining: number = 0;

    constructor(
        private recipientService: RecipientService,
        private messageService: MessageService,
    ) {
        // Subscribe to pending messages in constructor, take only 1 emission
        this.messageService
            .getPendingMessage()
            .pipe(
                filter((message) => message !== null),
                take(1), // Only take the first non-null message
            )
            .subscribe((message) => {
                console.log("Pending message received:", message);
                // Add a small delay to ensure component is fully initialized
                setTimeout(() => {
                    if (message.severity === "success") {
                        console.log("Showing success toast");
                        this.messageService.showSuccess(message.detail, message.summary);
                    } else if (message.severity === "error") {
                        console.log("Showing error toast");
                        this.messageService.showError(message.detail, message.summary);
                    }
                    // Clear the message after showing it
                    this.messageService.clearPendingMessage();
                }, 100);
            });
    }

    ngOnInit() {
        console.log("Home component initialized, checking for pending messages");
        this.recipient = this.recipientService.currentRecipient;
        this.daysRemaining = this.calculateDaysUntilChristmas();
    }

    ngOnDestroy() {
        // Component cleanup
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
