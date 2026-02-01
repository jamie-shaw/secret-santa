import { TestBed } from "@angular/core/testing";
import { MessageService } from "./message.service";
import { MessageService as PrimeMessageService } from "primeng/api";

describe("MessageService", () => {
    let service: MessageService;
    let primeMessageService: jasmine.SpyObj<PrimeMessageService>;

    beforeEach(() => {
        const primeMessageServiceSpy = jasmine.createSpyObj("PrimeMessageService", [
            "add",
            "clear",
        ]);

        TestBed.configureTestingModule({
            providers: [
                MessageService,
                { provide: PrimeMessageService, useValue: primeMessageServiceSpy },
            ],
        });

        service = TestBed.inject(MessageService);
        primeMessageService = TestBed.inject(
            PrimeMessageService,
        ) as jasmine.SpyObj<PrimeMessageService>;
    });

    it("should be created", () => {
        expect(service).toBeTruthy();
    });

    describe("queueMessage", () => {
        it("should queue a message", (done) => {
            service.queueMessage("info", "Test Summary", "Test Detail");

            service.getPendingMessage().subscribe((message) => {
                expect(message).toEqual({
                    severity: "info",
                    summary: "Test Summary",
                    detail: "Test Detail",
                });
                done();
            });
        });

        it("should overwrite previous queued message", (done) => {
            service.queueMessage("info", "First", "First message");
            service.queueMessage("error", "Second", "Second message");

            service.getPendingMessage().subscribe((message) => {
                expect(message).toEqual({
                    severity: "error",
                    summary: "Second",
                    detail: "Second message",
                });
                done();
            });
        });
    });

    describe("clearPendingMessage", () => {
        it("should clear the pending message", (done) => {
            service.queueMessage("info", "Test", "Test message");
            service.clearPendingMessage();

            service.getPendingMessage().subscribe((message) => {
                expect(message).toBeNull();
                done();
            });
        });
    });

    describe("showError", () => {
        it("should display error message with default summary", () => {
            service.showError("Something went wrong");

            expect(primeMessageService.add).toHaveBeenCalledWith({
                severity: "error",
                summary: "Error",
                detail: "Something went wrong",
                sticky: true,
            });
        });

        it("should display error message with custom summary", () => {
            service.showError("Something went wrong", "Custom Error");

            expect(primeMessageService.add).toHaveBeenCalledWith({
                severity: "error",
                summary: "Custom Error",
                detail: "Something went wrong",
                sticky: true,
            });
        });
    });

    describe("showSuccess", () => {
        it("should display success message with default summary", () => {
            service.showSuccess("Operation completed");

            expect(primeMessageService.add).toHaveBeenCalledWith({
                severity: "success",
                summary: "Success",
                detail: "Operation completed",
                sticky: true,
            });
        });

        it("should display success message with custom summary", () => {
            service.showSuccess("Operation completed", "Well Done");

            expect(primeMessageService.add).toHaveBeenCalledWith({
                severity: "success",
                summary: "Well Done",
                detail: "Operation completed",
                sticky: true,
            });
        });
    });

    describe("showInfo", () => {
        it("should display info message with default summary", () => {
            service.showInfo("Here is some information");

            expect(primeMessageService.add).toHaveBeenCalledWith({
                severity: "info",
                summary: "Info",
                detail: "Here is some information",
                sticky: true,
            });
        });

        it("should display info message with custom summary", () => {
            service.showInfo("Here is some information", "FYI");

            expect(primeMessageService.add).toHaveBeenCalledWith({
                severity: "info",
                summary: "FYI",
                detail: "Here is some information",
                sticky: true,
            });
        });
    });

    describe("showWarning", () => {
        it("should display warning message with default summary", () => {
            service.showWarning("Please be careful");

            expect(primeMessageService.add).toHaveBeenCalledWith({
                severity: "warn",
                summary: "Warning",
                detail: "Please be careful",
                sticky: true,
            });
        });

        it("should display warning message with custom summary", () => {
            service.showWarning("Please be careful", "Caution");

            expect(primeMessageService.add).toHaveBeenCalledWith({
                severity: "warn",
                summary: "Caution",
                detail: "Please be careful",
                sticky: true,
            });
        });
    });

    describe("clear", () => {
        it("should clear all messages", () => {
            service.clear();

            expect(primeMessageService.clear).toHaveBeenCalled();
        });
    });

    describe("clearByKey", () => {
        it("should clear message by specific key", () => {
            const key = "test-key";
            service.clearByKey(key);

            expect(primeMessageService.clear).toHaveBeenCalledWith(key);
        });
    });

    describe("getPendingMessage", () => {
        it("should return observable of pending message", (done) => {
            service.getPendingMessage().subscribe((message) => {
                expect(message).toBeNull();
                done();
            });
        });
    });
});
