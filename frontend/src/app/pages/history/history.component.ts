import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";
import { RecipientService } from "../../services/recipient.service";
import { Recipient } from "src/app/models/recipient.model";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { combineLatest, map } from "rxjs";
import { LoadingStateService } from "src/app/services/loading-state.service";

@Component({
    selector: "app-history",
    imports: [CommonModule, FormsModule, RouterLink],
    providers: [
        LoadingStateService,
        { provide: "recipientsLoading", useClass: LoadingStateService },
        { provide: "yearsLoading", useClass: LoadingStateService },
    ],
    templateUrl: "./history.component.html",
    styleUrl: "./history.component.css",
})
export class HistoryComponent {
    recipients: Recipient[] = [];
    years: number[] = [];
    selectedYear: number = 0;

    recipientsLoading$ = this.recipientsLoading.loading$;
    yearsLoading$ = this.yearsLoading.loading$;

    loading$ = combineLatest([this.recipientsLoading$, this.yearsLoading$]).pipe(
        map(([recipientsLoading, yearsLoading]) => recipientsLoading || yearsLoading),
    );

    error$ = combineLatest([this.recipientsLoading.error$, this.yearsLoading.error$]).pipe(
        map(([recipientsError, yearsError]) => recipientsError || yearsError),
    );

    constructor(
        private recipientService: RecipientService,
        private recipientsLoading: LoadingStateService,
        private yearsLoading: LoadingStateService,
    ) {}

    ngOnInit() {
        this.getHistoryYears();
    }

    onYearChange(year: number) {
        this.getAllRecipientsForYear(year);
    }

    private getHistoryYears() {
        this.yearsLoading
            .withLoading(this.recipientService.fetchHistoryYears(), "Failed to get history years")
            .subscribe({
                next: (years) => {
                    this.years = years;
                    // Set selectedYear to the most recent year
                    this.selectedYear = this.years[0];
                    this.getAllRecipientsForYear(this.selectedYear);
                },
            });
    }

    private getAllRecipientsForYear(year: number) {
        this.recipientsLoading
            .withLoading(this.recipientService.fetchAllRecipients(year), "Failed to get recipients")
            .subscribe({
                next: (recipients) => {
                    this.recipients = recipients;
                },
            });
    }
}
