import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule, NgForm } from "@angular/forms";
import { RouterLink } from "@angular/router";
import { EmailRequest, EmailService } from "src/app/services/email.service";
import { LoadingStateService } from "src/app/services/loading-state.service";

@Component({
    selector: "app-email",
    imports: [CommonModule, FormsModule, RouterLink],
    providers: [LoadingStateService],
    templateUrl: "./email.component.html",
    styleUrl: "./email.component.css",
})
export class EmailComponent {
    emailRequest: EmailRequest = { addressee: "SANTA", message: "" };
    message: string = "";
    success: string | null = null;

    loading$ = this.loadingState.loading$;
    error$ = this.loadingState.error$;

    constructor(
        private emailService: EmailService,
        private loadingState: LoadingStateService,
    ) {}

    onSubmit(emailForm: NgForm) {
        if (emailForm.invalid) {
            return;
        }

        this.success = null;
        this.loadingState
            .withLoading(this.emailService.sendEmail(this.emailRequest), "Failed to send email")
            .subscribe({
                next: () => {
                    this.success = "Message sent successfully!";
                    this.emailRequest.message = "";
                    emailForm.resetForm({ addressee: this.emailRequest.addressee });
                },
            });
    }
}
