import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";
import { Recipient } from "src/app/models/recipient.model";
import { RecipientService } from "src/app/services/recipient/recipient.service";
import { LoadingStateService } from "src/app/services/loading-state/loading-state.service";
import { ToastModule } from "primeng/toast";
import { MessageService as PrimeMessageService } from "primeng/api";
import { MessageService } from "src/app/services/message/message.service";

@Component({
    selector: "app-pick-status",
    imports: [CommonModule, RouterLink, ToastModule],
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
        private messageService: MessageService,
    ) {}

    ngOnInit() {
        this.loadingState
            .fetch(this.recipientService.fetchPickStatus(), "Failed to fetch pick status")
            .subscribe({
                next: (pickers) => {
                    this.pickers = pickers;
                },
            });

        this.error$.subscribe((error) => {
            if (error) {
                this.messageService.showError(error);
            }
        });
    }
}
