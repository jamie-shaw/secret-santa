import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule, NgForm } from "@angular/forms";
import { RouterLink } from "@angular/router";
import { EmailRequest, EmailService } from "src/app/services/email.service";
import { LoadingStateService } from "src/app/services/loading-state.service";
import { ToastModule } from "primeng/toast";
import { MessageService as PrimeMessageService } from "primeng/api";
import { MessageService } from "src/app/services/message.service";

@Component({
    selector: "app-email",
    imports: [CommonModule, FormsModule, RouterLink, ToastModule],
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
        private messageService: MessageService,
    ) {}

    ngOnInit() {
        this.error$.subscribe((error) => {
            if (error) {
                this.messageService.showError(error);
            }
        });
    }

    onSubmit(emailForm: NgForm) {
        if (emailForm.invalid) {
            return;
        }

        this.success = null;
        this.loadingState
            .fetch(this.emailService.sendEmail(this.emailRequest), "Failed to send email")
            .subscribe({
                next: () => {
                    this.messageService.showSuccess("Message sent successfully!");
                    this.emailRequest.message = "";
                    emailForm.resetForm({ addressee: this.emailRequest.addressee });
                },
            });
    }
}
