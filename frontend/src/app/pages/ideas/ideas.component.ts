import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { BehaviorSubject } from "rxjs";
import { Gift } from "src/app/models/gift.model";
import { GiftService } from "src/app/services/gift.service";

@Component({
    selector: "app-ideas",
    imports: [CommonModule],
    templateUrl: "./ideas.component.html",
    styleUrl: "./ideas.component.css"
})
export class IdeasComponent {
    ideas: Gift[] = [];
    error: string | null = null;

    loading$ = new BehaviorSubject<boolean>(true);

    constructor(private giftService: GiftService) {}

    ngOnInit() {
        this.getIdeas();
    }

    private getIdeas() {
        this.giftService.getIdeasForSanta().subscribe({
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
