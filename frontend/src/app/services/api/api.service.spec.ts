import { TestBed } from "@angular/core/testing";
import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { ApiService } from "./api.service";

describe("ApiService", () => {
    let service: ApiService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [ApiService],
        });
        service = TestBed.inject(ApiService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    it("should be created", () => {
        expect(service).toBeTruthy();
    });

    describe("getData", () => {
        it("should make a GET request to /api/data", () => {
            const mockData = { test: "data" };

            service.getData().subscribe((data) => {
                expect(data).toEqual(mockData);
            });

            const req = httpMock.expectOne("/api/data");
            expect(req.request.method).toBe("GET");
            req.flush(mockData);
        });

        it("should handle errors from getData", () => {
            const errorMessage = "Not Found";

            service.getData().subscribe(
                () => fail("should have failed with 404 error"),
                (error) => {
                    expect(error.status).toBe(404);
                    expect(error.statusText).toBe(errorMessage);
                },
            );

            const req = httpMock.expectOne("/api/data");
            req.flush("Not Found", { status: 404, statusText: errorMessage });
        });
    });

    describe("postData", () => {
        it("should make a POST request to /api/data with data", () => {
            const mockData = { name: "test", value: 123 };
            const mockResponse = { success: true, id: "123" };

            service.postData(mockData).subscribe((response) => {
                expect(response).toEqual(mockResponse);
            });

            const req = httpMock.expectOne("/api/data");
            expect(req.request.method).toBe("POST");
            expect(req.request.body).toEqual(mockData);
            req.flush(mockResponse);
        });

        it("should handle errors from postData", () => {
            const mockData = { name: "test" };
            const errorMessage = "Bad Request";

            service.postData(mockData).subscribe(
                () => fail("should have failed with 400 error"),
                (error) => {
                    expect(error.status).toBe(400);
                    expect(error.statusText).toBe(errorMessage);
                },
            );

            const req = httpMock.expectOne("/api/data");
            req.flush("Bad Request", { status: 400, statusText: errorMessage });
        });
    });
});
