import { TestBed } from "@angular/core/testing";
import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { RecipientService } from "./recipient.service";
import { Recipient } from "src/app/models/recipient.model";

describe("RecipientService", () => {
    let service: RecipientService;
    let httpMock: HttpTestingController;
    let sessionStorageMock: { [key: string]: string };

    beforeEach(() => {
        sessionStorageMock = {};

        spyOn(sessionStorage, "getItem").and.callFake((key: string) => {
            return sessionStorageMock[key] || null;
        });

        spyOn(sessionStorage, "setItem").and.callFake((key: string, value: string) => {
            sessionStorageMock[key] = value;
        });

        spyOn(sessionStorage, "removeItem").and.callFake((key: string) => {
            delete sessionStorageMock[key];
        });

        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [RecipientService],
        });

        service = TestBed.inject(RecipientService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    it("should be created", () => {
        expect(service).toBeTruthy();
    });

    describe("constructor", () => {
        it("should restore recipient from sessionStorage if available", () => {
            const storedRecipient: Recipient = {
                userName: "John",
                year: 2026,
                recipient: "Jane",
                assigned: true,
                viewed: true,
            };
            sessionStorageMock["currentRecipient"] = JSON.stringify(storedRecipient);

            const newService = new RecipientService(TestBed.inject(HttpClientTestingModule) as any);

            expect(newService.currentRecipient).toEqual(storedRecipient);
        });
    });

    describe("currentUser", () => {
        it("should get current user", () => {
            service.setCurrentUser("TestUser");
            expect(service.currentUser).toBe("TestUser");
        });

        it("should return null initially", () => {
            expect(service.currentUser).toBeNull();
        });
    });

    describe("currentRecipient", () => {
        it("should get current recipient", () => {
            const recipient: Recipient = {
                userName: "John",
                year: 2026,
                recipient: "Jane",
                assigned: true,
                viewed: false,
            };
            service.setCurrentRecipient(recipient);
            expect(service.currentRecipient).toEqual(recipient);
        });

        it("should return null initially", () => {
            const newService = TestBed.inject(RecipientService);
            expect(newService.currentRecipient).toBeNull();
        });
    });

    describe("setCurrentUser", () => {
        it("should set and emit current user", (done) => {
            service.currentUser$.subscribe((user) => {
                if (user === "TestUser") {
                    expect(user).toBe("TestUser");
                    done();
                }
            });

            service.setCurrentUser("TestUser");
        });
    });

    describe("setCurrentRecipient", () => {
        it("should set and emit current recipient", (done) => {
            const recipient: Recipient = {
                userName: "John",
                year: 2026,
                recipient: "Jane",
                assigned: true,
                viewed: false,
            };

            service.currentRecipient$.subscribe((rec) => {
                if (rec) {
                    expect(rec).toEqual(recipient);
                    done();
                }
            });

            service.setCurrentRecipient(recipient);
        });
    });

    describe("fetchRecipient", () => {
        it("should fetch recipient and update current recipient", (done) => {
            const mockRecipient: Recipient = {
                userName: "Alice",
                year: 2026,
                recipient: "Bob",
                assigned: true,
                viewed: false,
            };

            service.fetchRecipient().subscribe((recipient) => {
                expect(recipient).toEqual(mockRecipient);
                expect(service.currentRecipient).toEqual(mockRecipient);
                done();
            });

            const req = httpMock.expectOne("/api/recipient");
            expect(req.request.method).toBe("GET");
            req.flush(mockRecipient);
        });

        it("should store recipient in sessionStorage", () => {
            const mockRecipient: Recipient = {
                userName: "Alice",
                year: 2026,
                recipient: "Bob",
                assigned: true,
                viewed: false,
            };

            service.fetchRecipient().subscribe(() => {
                expect(sessionStorage.setItem).toHaveBeenCalledWith(
                    "currentRecipient",
                    JSON.stringify(mockRecipient),
                );
            });

            const req = httpMock.expectOne("/api/recipient");
            req.flush(mockRecipient);
        });

        it("should handle fetch error", () => {
            service.fetchRecipient().subscribe(
                () => fail("should have failed"),
                (error) => {
                    expect(error.status).toBe(500);
                },
            );

            const req = httpMock.expectOne("/api/recipient");
            req.flush("Server Error", { status: 500, statusText: "Internal Server Error" });
        });
    });

    describe("fetchAllRecipients", () => {
        it("should fetch all recipients for a given year", () => {
            const mockRecipients: Recipient[] = [
                { userName: "Alice", year: 2025, recipient: "Bob", assigned: true, viewed: true },
                {
                    userName: "Bob",
                    year: 2025,
                    recipient: "Charlie",
                    assigned: true,
                    viewed: false,
                },
            ];

            service.fetchAllRecipients(2025).subscribe((recipients) => {
                expect(recipients).toEqual(mockRecipients);
                expect(recipients.length).toBe(2);
            });

            const req = httpMock.expectOne("/api/history/2025");
            expect(req.request.method).toBe("GET");
            req.flush(mockRecipients);
        });

        it("should return empty array for year with no recipients", () => {
            service.fetchAllRecipients(2020).subscribe((recipients) => {
                expect(recipients).toEqual([]);
            });

            const req = httpMock.expectOne("/api/history/2020");
            req.flush([]);
        });
    });

    describe("fetchHistoryYears", () => {
        it("should fetch available history years", () => {
            const mockYears = [2026, 2025, 2024, 2023];

            service.fetchHistoryYears().subscribe((years) => {
                expect(years).toEqual(mockYears);
                expect(years.length).toBe(4);
            });

            const req = httpMock.expectOne("/api/history/years");
            expect(req.request.method).toBe("GET");
            req.flush(mockYears);
        });

        it("should return empty array if no history", () => {
            service.fetchHistoryYears().subscribe((years) => {
                expect(years).toEqual([]);
            });

            const req = httpMock.expectOne("/api/history/years");
            req.flush([]);
        });
    });

    describe("fetchPickStatus", () => {
        it("should fetch pick status for all recipients", () => {
            const mockStatuses: Recipient[] = [
                { userName: "Alice", year: 2026, recipient: "Bob", assigned: true, viewed: true },
                {
                    userName: "Bob",
                    year: 2026,
                    recipient: "Charlie",
                    assigned: true,
                    viewed: false,
                },
                {
                    userName: "Charlie",
                    year: 2026,
                    recipient: "Alice",
                    assigned: false,
                    viewed: false,
                },
            ];

            service.fetchPickStatus().subscribe((statuses) => {
                expect(statuses).toEqual(mockStatuses);
                expect(statuses.length).toBe(3);
            });

            const req = httpMock.expectOne("/api/pick/status");
            expect(req.request.method).toBe("GET");
            req.flush(mockStatuses);
        });

        it("should handle empty pick status", () => {
            service.fetchPickStatus().subscribe((statuses) => {
                expect(statuses).toEqual([]);
            });

            const req = httpMock.expectOne("/api/pick/status");
            req.flush([]);
        });
    });
});
