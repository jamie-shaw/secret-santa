import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";
import { Gift } from "src/app/models/gift.model";
import { GiftService } from "src/app/services/gift.service";
import { LoadingStateService } from "src/app/services/loading-state.service";
import { ToastModule } from "primeng/toast";
import { MessageService as PrimeMessageService } from "primeng/api";
import { MessageService } from "src/app/services/message.service";

@Component({
    selector: "app-gift-summary",
    imports: [CommonModule, RouterLink, ToastModule],
    providers: [LoadingStateService],
    templateUrl: "./gift-summary.component.html",
    styleUrl: "./gift-summary.component.css",
})
export class GiftSummaryComponent {
    loading$ = this.loadingStateService.loading$;
    error$ = this.loadingStateService.error$;

    gifts: Gift[] = [];

    constructor(
        private giftService: GiftService,
        private loadingStateService: LoadingStateService,
        private messageService: MessageService,
    ) {}

    ngOnInit() {
        this.loadingStateService
            .fetch(this.giftService.fetchIdeasForSanta(), "Failed to fetch gift ideas for Santa")
            .subscribe({
                next: (gifts) => {
                    this.gifts = gifts;
                },
            });

        // Subscribe to errors and show toast
        this.error$.subscribe((error) => {
            if (error) {
                this.messageService.showError(error);
            }
        });
    }
}
