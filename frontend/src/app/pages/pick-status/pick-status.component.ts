import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";
import { BehaviorSubject } from "rxjs";
import { Recipient } from "src/app/models/recipient.model";
import { RecipientService } from "src/app/services/recipient.service";

@Component({
    selector: "app-pick-status",
    imports: [CommonModule, RouterLink],
    templateUrl: "./pick-status.component.html",
    styleUrl: "./pick-status.component.css",
})
export class PickStatusComponent {
    loading$ = new BehaviorSubject<boolean>(true);
    error: string | null = null;

    pickers: Recipient[] = [];

    constructor(private recipientService: RecipientService) {}

    ngOnInit() {
        this.recipientService.fetchPickStatus().subscribe({
            next: (pickers) => {
                this.pickers = pickers;
                this.loading$.next(false);
            },
            error: (err) => {
                this.loading$.next(false);
                this.error = "Failed to fetch pick status";
                console.error(this.error, err);
            },
        });
    }
}
