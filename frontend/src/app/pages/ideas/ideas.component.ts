import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";
import { BehaviorSubject } from "rxjs";
import { Gift } from "src/app/models/gift.model";
import { GiftService } from "src/app/services/gift.service";
import { RecipientService } from "src/app/services/recipient.service";

@Component({
    selector: "app-ideas",
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: "./ideas.component.html",
    styleUrl: "./ideas.component.css",
})
export class IdeasComponent {
    ideas: Gift[] = [];
    recipientName: string | undefined;
    error: string | null = null;

    loading$ = new BehaviorSubject<boolean>(true);

    constructor(
        private giftService: GiftService,
        private recipientService: RecipientService,
    ) {}

    ngOnInit() {
        this.getIdeas();
        this.recipientName = this.recipientService.currentRecipient?.recipient;
    }

    private getIdeas() {
        this.giftService.getIdeasFromRecipient().subscribe({
            next: (ideas) => {
                this.ideas = ideas;
                this.loading$.next(false);
                console.log(this.ideas);
            },
            error: (err) => this.handleError(err),
        });
    }

    private handleError(err: any) {
        this.error = "Failed to get history data";
        this.loading$.next(false);
        console.error("API Error:", err);
    }
}
