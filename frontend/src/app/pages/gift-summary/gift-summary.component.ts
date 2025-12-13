import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";
import { Gift } from "src/app/models/gift.model";
import { GiftService } from "src/app/services/gift.service";
import { LoadingStateService } from "src/app/services/loading-state.service";

@Component({
    selector: "app-gift-summary",
    imports: [CommonModule, RouterLink],
    providers: [LoadingStateService],
    templateUrl: "./gift-summary.component.html",
    styleUrl: "./gift-summary.component.css",
})
export class GiftSummaryComponent {
    loading$ = this.loadingState.loading$;
    error$ = this.loadingState.error$;

    gifts: Gift[] = [];

    constructor(
        private giftService: GiftService,
        private loadingState: LoadingStateService,
    ) {}

    ngOnInit() {
        this.loadingState
            .withLoading(
                this.giftService.fetchIdeasForSanta(),
                "Failed to fetch gift ideas for Santa",
            )
            .subscribe({
                next: (gifts) => {
                    this.gifts = gifts;
                },
            });
    }
}
