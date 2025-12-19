import { Injectable } from "@angular/core";
import { MessageService as PrimeMessageService } from "primeng/api";

/**
 * Wrapper service for PrimeNG MessageService
 * Provides centralized message/toast handling across the application
 */
@Injectable({
    providedIn: "root",
})
export class MessageService {
    constructor(private primeMessageService: PrimeMessageService) {}

    /**
     * Show an error message
     */
    showError(detail: string, summary: string = "Error"): void {
        this.primeMessageService.add({
            severity: "error",
            summary: summary,
            detail: detail,
            sticky: true,
        });
    }

    /**
     * Show a success message
     */
    showSuccess(detail: string, summary: string = "Success"): void {
        this.primeMessageService.add({
            severity: "success",
            summary: summary,
            detail: detail,
            sticky: true,
        });
    }

    /**
     * Show an info message
     */
    showInfo(detail: string, summary: string = "Info"): void {
        this.primeMessageService.add({
            severity: "info",
            summary: summary,
            detail: detail,
            sticky: true,
        });
    }

    /**
     * Show a warning message
     */
    showWarning(detail: string, summary: string = "Warning"): void {
        this.primeMessageService.add({
            severity: "warn",
            summary: summary,
            detail: detail,
            sticky: true,
        });
    }

    /**
     * Clear all messages
     */
    clear(): void {
        this.primeMessageService.clear();
    }

    /**
     * Clear a specific message by key
     */
    clearByKey(key: string): void {
        this.primeMessageService.clear(key);
    }
}
