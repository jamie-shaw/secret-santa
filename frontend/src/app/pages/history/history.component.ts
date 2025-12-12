import { Component } from "@angular/core";
import { RecipientService } from "../../services/recipient.service";
import { Recipient } from "src/app/models/recipient.model";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { combineLatest, map, BehaviorSubject } from "rxjs";

@Component({
    selector: "app-history",
    imports: [CommonModule, FormsModule],
    templateUrl: "./history.component.html",
    styleUrl: "./history.component.css"
})
export class HistoryComponent {
    recipients: Recipient[] = [];
    years: number[] = [];
    selectedYear: number = 0;
    error: string | null = null;

    recipientsLoading$ = new BehaviorSubject<boolean>(true);
    yearsLoading$ = new BehaviorSubject<boolean>(true);

    loading$ = combineLatest([this.recipientsLoading$, this.yearsLoading$]).pipe(
        map(([recipientsLoading, yearsLoading]) => recipientsLoading || yearsLoading),
    );

    constructor(private recipientService: RecipientService) {}

    ngOnInit() {
        this.getHistoryYears();
    }

    onYearChange(year: number) {
        this.getAllRecipientsForYear(year);
    }

    private getHistoryYears() {
        this.recipientService.fetchHistoryYears().subscribe({
            next: (years) => {
                this.years = years;
                this.yearsLoading$.next(false);

                // Set selectedYear to the most recent year
                this.selectedYear = this.years[0];
                this.getAllRecipientsForYear(this.selectedYear);
            },
            error: (err) => this.handleError(err),
        });
    }

    private getAllRecipientsForYear(year: number) {
        this.recipientsLoading$.next(true);
        this.recipientService.fetchAllRecipients(year).subscribe({
            next: (recipients) => {
                this.recipients = recipients;
                this.recipientsLoading$.next(false);
            },
            error: (err) => this.handleError(err),
        });
    }

    private handleError(err: any) {
        this.error = "Failed to get history data";
        this.recipientsLoading$.next(false);
        this.yearsLoading$.next(false);
        console.error("API Error:", err);
    }
}
