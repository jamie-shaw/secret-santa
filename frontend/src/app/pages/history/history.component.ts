import { HttpClient } from "@angular/common/http";
import { Component } from "@angular/core";
import { RecipientService } from "../../services/recipient.service";
import { Recipient } from "src/app/models/recipient.model";
import { CommonModule } from "@angular/common";
import { getCurrentYear } from "../../utils/globals";
import { combineLatest, map, BehaviorSubject } from "rxjs";

@Component({
    selector: "app-history",
    standalone: true,
    imports: [CommonModule],
    templateUrl: "./history.component.html",
    styleUrl: "./history.component.css",
})
export class HistoryComponent {
    recipients: Recipient[] = [];
    years: number[] = [];

    private recipientsLoading$ = new BehaviorSubject<boolean>(true);
    private yearsLoading$ = new BehaviorSubject<boolean>(true);
    loading$ = combineLatest([this.recipientsLoading$, this.yearsLoading$]).pipe(
        map(([recipientsLoading, yearsLoading]) => recipientsLoading || yearsLoading),
    );

    error: string | null = null;
    selectedYear: number = getCurrentYear();

    constructor(private recipientService: RecipientService) {}

    ngOnInit() {
        this.error = null;

        this.getAllRecipientsForYear(this.selectedYear);

        this.getHistoryYears();
    }

    private getHistoryYears() {
        this.recipientService.getHistoryYears().subscribe({
            next: (years) => {
                this.years = years;
                this.yearsLoading$.next(false);
            },
            error: (err) => {
                this.error = "Failed to get history data";
                this.recipientsLoading$.next(false);
                this.yearsLoading$.next(false);
            },
        });
    }

    onYearChange(event: Event) {
        const target = event.target as HTMLSelectElement;
        this.selectedYear = Number(target.value);
        this.recipientsLoading$.next(true);
        this.getAllRecipientsForYear(this.selectedYear);
    }

    private getAllRecipientsForYear(year: number) {
        this.recipientService.getAllRecipients(year).subscribe({
            next: (recipients) => {
                this.recipients = recipients;
                this.recipientsLoading$.next(false);
            },
            error: (err) => {
                this.error = "Failed to get history data";
                this.recipientsLoading$.next(false);
                this.yearsLoading$.next(false);
                console.error("API Error:", err);
            },
        });
    }
}
