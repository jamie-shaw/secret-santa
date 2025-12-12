import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RecipientService } from "src/app/services/recipient.service";
import { Recipient } from "src/app/models/recipient.model";

@Component({
    selector: "app-home",
    imports: [CommonModule],
    templateUrl: "./home.component.html",
    styleUrl: "./home.component.css"
})
export class HomeComponent {
    recipient: Recipient | null = null;

    constructor(private recipientService: RecipientService) {}

    ngOnInit() {
        // Preload recipient data
        console.log("Fetching recipient data...");
        this.recipientService.fetchRecipient().subscribe({
            next: (recipient) => {
                this.recipient = recipient;
                console.log("Recipient data fetched:", recipient);
            },
            error: (err) => {
                console.error("Failed to fetch recipient data:", err);
            },
        });
    }
}
