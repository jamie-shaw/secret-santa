import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";
import { Recipient } from "src/app/models/recipient.model";
import { RecipientService } from "src/app/services/recipient.service";
import { LoadingStateService } from "src/app/services/loading-state.service";

@Component({
    selector: "app-pick-status",
    imports: [CommonModule, RouterLink],
    providers: [LoadingStateService],
    templateUrl: "./pick-status.component.html",
    styleUrl: "./pick-status.component.css",
})
export class PickStatusComponent {
    loading$ = this.loadingState.loading$;
    error$ = this.loadingState.error$;

    pickers: Recipient[] = [];

    constructor(
        private recipientService: RecipientService,
        private loadingState: LoadingStateService,
    ) {}

    ngOnInit() {
        this.loadingState
            .fetch(this.recipientService.fetchPickStatus(), "Failed to fetch pick status")
            .subscribe({
                next: (pickers) => {
                    this.pickers = pickers;
                },
            });
    }
}
