import { TestBed } from "@angular/core/testing";
import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { EmailService, EmailRequest } from "./email.service";

describe("EmailService", () => {
    let service: EmailService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [EmailService],
        });
        service = TestBed.inject(EmailService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    it("should be created", () => {
        expect(service).toBeTruthy();
    });

    describe("sendEmail", () => {
        it("should send email with correct request body", () => {
            const emailRequest: EmailRequest = {
                addressee: "test@example.com",
                message: "Hello, this is a test message",
            };
            const mockResponse = { success: true, messageId: "123" };

            service.sendEmail(emailRequest).subscribe((response) => {
                expect(response).toEqual(mockResponse);
            });

            const req = httpMock.expectOne("/api/email/send");
            expect(req.request.method).toBe("POST");
            expect(req.request.body).toEqual(emailRequest);
            req.flush(mockResponse);
        });

        it("should handle empty message", () => {
            const emailRequest: EmailRequest = {
                addressee: "test@example.com",
                message: "",
            };

            service.sendEmail(emailRequest).subscribe();

            const req = httpMock.expectOne("/api/email/send");
            expect(req.request.body.message).toBe("");
            req.flush({ success: true });
        });

        it("should handle errors from sendEmail", () => {
            const emailRequest: EmailRequest = {
                addressee: "invalid-email",
                message: "Test message",
            };
            const errorMessage = "Invalid email address";

            service.sendEmail(emailRequest).subscribe(
                () => fail("should have failed with 400 error"),
                (error) => {
                    expect(error.status).toBe(400);
                    expect(error.error).toContain(errorMessage);
                },
            );

            const req = httpMock.expectOne("/api/email/send");
            req.flush(errorMessage, { status: 400, statusText: "Bad Request" });
        });

        it("should handle server errors", () => {
            const emailRequest: EmailRequest = {
                addressee: "test@example.com",
                message: "Test message",
            };

            service.sendEmail(emailRequest).subscribe(
                () => fail("should have failed with 500 error"),
                (error) => {
                    expect(error.status).toBe(500);
                },
            );

            const req = httpMock.expectOne("/api/email/send");
            req.flush("Internal Server Error", {
                status: 500,
                statusText: "Internal Server Error",
            });
        });
    });
});
