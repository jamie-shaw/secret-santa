import { HttpClient } from "@angular/common/http";
import { Component } from "@angular/core";
import { RecipientService } from "../../services/recipient.service";
import { Recipient } from "src/app/models/recipient.model";
import { CommonModule } from "@angular/common";

@Component({
    selector: "app-history",
    standalone: true,
    imports: [CommonModule],
    templateUrl: "./history.component.html",
    styleUrl: "./history.component.css",
})
export class HistoryComponent {
    recipients: Recipient[] = [];
    loading = false;
    error: string | null = null;

    constructor(private recipientService: RecipientService) {}

    ngOnInit() {
        this.loading = true;
        this.error = null;
        console.log("Fetching history data for 2023...");

        this.recipientService.getAllRecipients(2023).subscribe({
            next: (recipients) => {
                this.recipients = recipients;
                this.loading = false;
            },
            error: (err) => {
                this.error = "Failed to get history data";
                this.loading = false;
                console.error("API Error:", err);
            },
        });
    }
}
