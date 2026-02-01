import { TestBed } from "@angular/core/testing";
import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { HTTP_INTERCEPTORS, HttpClient } from "@angular/common/http";
import { AuthInterceptor } from "./auth.interceptor";
import { AuthService } from "../services/auth/auth.service";

describe("AuthInterceptor", () => {
    let httpMock: HttpTestingController;
    let httpClient: HttpClient;
    let authService: jasmine.SpyObj<AuthService>;

    beforeEach(() => {
        const authServiceSpy = jasmine.createSpyObj("AuthService", ["getToken"]);

        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                { provide: AuthService, useValue: authServiceSpy },
                {
                    provide: HTTP_INTERCEPTORS,
                    useClass: AuthInterceptor,
                    multi: true,
                },
            ],
        });

        httpMock = TestBed.inject(HttpTestingController);
        httpClient = TestBed.inject(HttpClient);
        authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    });

    afterEach(() => {
        httpMock.verify();
    });

    it("should add Authorization header when token exists", () => {
        const token = "test-jwt-token";
        authService.getToken.and.returnValue(token);

        httpClient.get("/api/test").subscribe();

        const req = httpMock.expectOne("/api/test");
        expect(req.request.headers.has("Authorization")).toBe(true);
        expect(req.request.headers.get("Authorization")).toBe(`Bearer ${token}`);
        req.flush({});
    });

    it("should not add Authorization header when token is null", () => {
        authService.getToken.and.returnValue(null);

        httpClient.get("/api/test").subscribe();

        const req = httpMock.expectOne("/api/test");
        expect(req.request.headers.has("Authorization")).toBe(false);
        req.flush({});
    });

    it("should not add Authorization header when token is empty string", () => {
        authService.getToken.and.returnValue("");

        httpClient.get("/api/test").subscribe();

        const req = httpMock.expectOne("/api/test");
        expect(req.request.headers.has("Authorization")).toBe(false);
        req.flush({});
    });

    it("should add Authorization header to POST requests", () => {
        const token = "test-jwt-token";
        authService.getToken.and.returnValue(token);

        const postData = { name: "test" };
        httpClient.post("/api/test", postData).subscribe();

        const req = httpMock.expectOne("/api/test");
        expect(req.request.method).toBe("POST");
        expect(req.request.headers.has("Authorization")).toBe(true);
        expect(req.request.headers.get("Authorization")).toBe(`Bearer ${token}`);
        req.flush({});
    });

    it("should add Authorization header to PUT requests", () => {
        const token = "test-jwt-token";
        authService.getToken.and.returnValue(token);

        const putData = { id: 1, name: "test" };
        httpClient.put("/api/test/1", putData).subscribe();

        const req = httpMock.expectOne("/api/test/1");
        expect(req.request.method).toBe("PUT");
        expect(req.request.headers.has("Authorization")).toBe(true);
        expect(req.request.headers.get("Authorization")).toBe(`Bearer ${token}`);
        req.flush({});
    });

    it("should add Authorization header to DELETE requests", () => {
        const token = "test-jwt-token";
        authService.getToken.and.returnValue(token);

        httpClient.delete("/api/test/1").subscribe();

        const req = httpMock.expectOne("/api/test/1");
        expect(req.request.method).toBe("DELETE");
        expect(req.request.headers.has("Authorization")).toBe(true);
        expect(req.request.headers.get("Authorization")).toBe(`Bearer ${token}`);
        req.flush({});
    });

    it("should preserve existing headers when adding Authorization", () => {
        const token = "test-jwt-token";
        authService.getToken.and.returnValue(token);

        httpClient
            .get("/api/test", {
                headers: {
                    "Content-Type": "application/json",
                    "X-Custom-Header": "custom-value",
                },
            })
            .subscribe();

        const req = httpMock.expectOne("/api/test");
        expect(req.request.headers.get("Authorization")).toBe(`Bearer ${token}`);
        expect(req.request.headers.get("Content-Type")).toBe("application/json");
        expect(req.request.headers.get("X-Custom-Header")).toBe("custom-value");
        req.flush({});
    });

    it("should handle multiple concurrent requests", () => {
        const token = "test-jwt-token";
        authService.getToken.and.returnValue(token);

        httpClient.get("/api/test1").subscribe();
        httpClient.get("/api/test2").subscribe();
        httpClient.post("/api/test3", {}).subscribe();

        const req1 = httpMock.expectOne("/api/test1");
        const req2 = httpMock.expectOne("/api/test2");
        const req3 = httpMock.expectOne("/api/test3");

        expect(req1.request.headers.get("Authorization")).toBe(`Bearer ${token}`);
        expect(req2.request.headers.get("Authorization")).toBe(`Bearer ${token}`);
        expect(req3.request.headers.get("Authorization")).toBe(`Bearer ${token}`);

        req1.flush({});
        req2.flush({});
        req3.flush({});
    });

    it("should call authService.getToken for each request", () => {
        const token = "test-jwt-token";
        authService.getToken.and.returnValue(token);

        httpClient.get("/api/test1").subscribe();
        httpClient.get("/api/test2").subscribe();

        expect(authService.getToken).toHaveBeenCalledTimes(2);

        httpMock.expectOne("/api/test1").flush({});
        httpMock.expectOne("/api/test2").flush({});
    });

    it("should handle requests when token changes between calls", () => {
        authService.getToken.and.returnValue("token1");
        httpClient.get("/api/test1").subscribe();

        authService.getToken.and.returnValue("token2");
        httpClient.get("/api/test2").subscribe();

        const req1 = httpMock.expectOne("/api/test1");
        const req2 = httpMock.expectOne("/api/test2");

        expect(req1.request.headers.get("Authorization")).toBe("Bearer token1");
        expect(req2.request.headers.get("Authorization")).toBe("Bearer token2");

        req1.flush({});
        req2.flush({});
    });

    it("should pass through the response from the server", () => {
        const token = "test-jwt-token";
        const mockResponse = { data: "test response" };
        authService.getToken.and.returnValue(token);

        httpClient.get("/api/test").subscribe((response) => {
            expect(response).toEqual(mockResponse);
        });

        const req = httpMock.expectOne("/api/test");
        req.flush(mockResponse);
    });

    it("should pass through errors from the server", () => {
        const token = "test-jwt-token";
        authService.getToken.and.returnValue(token);

        httpClient.get("/api/test").subscribe(
            () => fail("should have failed"),
            (error) => {
                expect(error.status).toBe(401);
                expect(error.statusText).toBe("Unauthorized");
            },
        );

        const req = httpMock.expectOne("/api/test");
        req.flush("Unauthorized", { status: 401, statusText: "Unauthorized" });
    });
});
