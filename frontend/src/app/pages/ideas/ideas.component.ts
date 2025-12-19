import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";
import { Gift } from "src/app/models/gift.model";
import { GiftService } from "src/app/services/gift.service";
import { RecipientService } from "src/app/services/recipient.service";
import { LoadingStateService } from "src/app/services/loading-state.service";
import { ToastModule } from "primeng/toast";
import { MessageService as PrimeMessageService } from "primeng/api";
import { MessageService } from "src/app/services/message.service";

@Component({
    selector: "app-ideas",
    standalone: true,
    imports: [CommonModule, RouterLink, ToastModule],
    providers: [LoadingStateService],
    templateUrl: "./ideas.component.html",
    styleUrl: "./ideas.component.css",
})
export class IdeasComponent {
    ideas: Gift[] = [];
    recipientName: string | undefined;
    loading$ = this.loadingState.loading$;
    error$ = this.loadingState.error$;

    constructor(
        private giftService: GiftService,
        private recipientService: RecipientService,
        private loadingState: LoadingStateService,
        private messageService: MessageService,
    ) {}

    ngOnInit() {
        this.getIdeas();
        this.recipientName = this.recipientService.currentRecipient?.recipient;

        this.error$.subscribe((error) => {
            if (error) {
                this.messageService.showError(error);
            }
        });
    }

    private getIdeas() {
        this.loadingState
            .fetch(this.giftService.fetchIdeasFromRecipient(), "Failed to get ideas data")
            .subscribe({
                next: (ideas) => {
                    this.ideas = ideas;
                    console.log(this.ideas);
                },
            });
    }
}
