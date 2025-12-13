import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";
import { Recipient } from "src/app/models/recipient.model";
import { RecipientService } from "src/app/services/recipient.service";

@Component({
    selector: "app-pick-status",
    imports: [CommonModule, RouterLink],
    templateUrl: "./pick-status.component.html",
    styleUrl: "./pick-status.component.css",
})
export class PickStatusComponent {
    pickers: Recipient[] = [];

    constructor(private recipientService: RecipientService) {}

    ngOnInit() {
        this.recipientService.fetchPickStatus().subscribe({
            next: (pickers) => {
                this.pickers = pickers;
            },
            error: (err) => {
                console.error("Failed to fetch pick status", err);
            },
        });
    }
}
