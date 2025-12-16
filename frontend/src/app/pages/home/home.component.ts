import { Component } from "@angular/core";

import { RouterLink } from "@angular/router";
import { RecipientService } from "src/app/services/recipient.service";
import { Recipient } from "src/app/models/recipient.model";

@Component({
    selector: "app-home",
    imports: [RouterLink],
    templateUrl: "./home.component.html",
    styleUrl: "./home.component.css",
})
export class HomeComponent {
    recipient: Recipient | null = null;

    constructor(private recipientService: RecipientService) {}

    ngOnInit() {
        if (this.recipient == null) {
            // Preload recipient data
            console.log("Fetching recipient data...");
            this.recipientService.fetchRecipient();
            this.recipient = this.recipientService.currentRecipient;
        } else {
            this.recipient = this.recipientService.currentRecipient;
        }
    }
}
