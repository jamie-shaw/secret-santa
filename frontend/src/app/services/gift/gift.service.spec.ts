import { TestBed } from "@angular/core/testing";
import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { GiftService } from "./gift.service";
import { Gift } from "src/app/models/gift.model";

describe("GiftService", () => {
    let service: GiftService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [GiftService],
        });
        service = TestBed.inject(GiftService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    it("should be created", () => {
        expect(service).toBeTruthy();
    });

    describe("fetchIdeasForSanta", () => {
        it("should fetch gift ideas for Santa", () => {
            const mockGifts: Gift[] = [
                {
                    id: "1",
                    userName: "John",
                    description: "Book",
                    link: "http://example.com",
                    year: "2026",
                },
                {
                    id: "2",
                    userName: "Jane",
                    description: "Toy",
                    link: "http://example.com",
                    year: "2026",
                },
            ];

            service.fetchIdeasForSanta().subscribe((gifts) => {
                expect(gifts).toEqual(mockGifts);
                expect(gifts.length).toBe(2);
            });

            const req = httpMock.expectOne("/api/gift/summary");
            expect(req.request.method).toBe("GET");
            req.flush(mockGifts);
        });

        it("should return empty array if no gifts", () => {
            service.fetchIdeasForSanta().subscribe((gifts) => {
                expect(gifts).toEqual([]);
            });

            const req = httpMock.expectOne("/api/gift/summary");
            req.flush([]);
        });
    });

    describe("fetchIdeasFromRecipient", () => {
        it("should fetch gift ideas from recipient", () => {
            const mockGifts: Gift[] = [
                {
                    id: "1",
                    userName: "Bob",
                    description: "Game",
                    link: "http://example.com",
                    year: "2026",
                },
            ];

            service.fetchIdeasFromRecipient().subscribe((gifts) => {
                expect(gifts).toEqual(mockGifts);
            });

            const req = httpMock.expectOne("/api/idea/summary");
            expect(req.request.method).toBe("GET");
            req.flush(mockGifts);
        });
    });

    describe("fetchGift", () => {
        it("should fetch a single gift by id", () => {
            const mockGift: Gift = {
                id: "123",
                userName: "Alice",
                description: "Headphones",
                link: "http://example.com",
                year: "2026",
            };

            service.fetchGift("123").subscribe((gift) => {
                expect(gift).toEqual(mockGift);
            });

            const req = httpMock.expectOne("/api/gift/123");
            expect(req.request.method).toBe("GET");
            req.flush(mockGift);
        });

        it("should handle 404 for non-existent gift", () => {
            service.fetchGift("999").subscribe(
                () => fail("should have failed with 404 error"),
                (error) => {
                    expect(error.status).toBe(404);
                },
            );

            const req = httpMock.expectOne("/api/gift/999");
            req.flush("Not Found", { status: 404, statusText: "Not Found" });
        });
    });

    describe("saveGift", () => {
        it("should update existing gift with PUT request", () => {
            const existingGift: Gift = {
                id: "123",
                userName: "Alice",
                description: "Headphones",
                link: "http://example.com",
                year: "2026",
            };
            const mockResponse: Gift = { ...existingGift };

            service.saveGift(existingGift).subscribe((gift) => {
                expect(gift).toEqual(mockResponse);
            });

            const req = httpMock.expectOne("/api/gift/123");
            expect(req.request.method).toBe("PUT");
            expect(req.request.body).toEqual(existingGift);
            req.flush(mockResponse);
        });

        it("should create new gift with POST request when no id", () => {
            const newGift: Gift = {
                id: "",
                userName: "Bob",
                description: "Camera",
                link: "http://example.com",
                year: "2026",
            };
            const mockResponse: Gift = { ...newGift, id: "456" };

            service.saveGift(newGift).subscribe((gift) => {
                expect(gift).toEqual(mockResponse);
                expect(gift.id).toBe("456");
            });

            const req = httpMock.expectOne("/api/gift");
            expect(req.request.method).toBe("POST");
            expect(req.request.body).toEqual(newGift);
            req.flush(mockResponse);
        });

        it("should handle validation errors", () => {
            const invalidGift: Gift = {
                id: "",
                userName: "",
                description: "",
                link: "",
                year: "2026",
            };

            service.saveGift(invalidGift).subscribe(
                () => fail("should have failed with 400 error"),
                (error) => {
                    expect(error.status).toBe(400);
                },
            );

            const req = httpMock.expectOne("/api/gift");
            req.flush("Validation error", { status: 400, statusText: "Bad Request" });
        });
    });

    describe("deleteGift", () => {
        it("should delete gift by id", () => {
            service.deleteGift("123").subscribe((response) => {
                expect(response).toBeNull();
            });

            const req = httpMock.expectOne("/api/gift/123");
            expect(req.request.method).toBe("DELETE");
            req.flush(null);
        });

        it("should handle 404 when deleting non-existent gift", () => {
            service.deleteGift("999").subscribe(
                () => fail("should have failed with 404 error"),
                (error) => {
                    expect(error.status).toBe(404);
                },
            );

            const req = httpMock.expectOne("/api/gift/999");
            req.flush("Not Found", { status: 404, statusText: "Not Found" });
        });

        it("should handle unauthorized deletion", () => {
            service.deleteGift("123").subscribe(
                () => fail("should have failed with 403 error"),
                (error) => {
                    expect(error.status).toBe(403);
                },
            );

            const req = httpMock.expectOne("/api/gift/123");
            req.flush("Forbidden", { status: 403, statusText: "Forbidden" });
        });
    });
});
