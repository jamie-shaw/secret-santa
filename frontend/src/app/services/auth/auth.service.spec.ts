import { TestBed } from "@angular/core/testing";
import { AuthService } from "./auth.service";

describe("AuthService", () => {
    let service: AuthService;
    let localStorageMock: { [key: string]: string };

    beforeEach(() => {
        localStorageMock = {};

        spyOn(localStorage, "getItem").and.callFake((key: string) => {
            return localStorageMock[key] || null;
        });

        spyOn(localStorage, "setItem").and.callFake((key: string, value: string) => {
            localStorageMock[key] = value;
        });

        spyOn(localStorage, "removeItem").and.callFake((key: string) => {
            delete localStorageMock[key];
        });

        TestBed.configureTestingModule({
            providers: [AuthService],
        });
        service = TestBed.inject(AuthService);
    });

    it("should be created", () => {
        expect(service).toBeTruthy();
    });

    describe("setToken", () => {
        it("should store the token in localStorage", () => {
            const token = "test-jwt-token";
            service.setToken(token);

            expect(localStorage.setItem).toHaveBeenCalledWith("jwt_token", token);
            expect(localStorageMock["jwt_token"]).toBe(token);
        });

        it("should overwrite existing token", () => {
            const token1 = "token-1";
            const token2 = "token-2";

            service.setToken(token1);
            expect(localStorageMock["jwt_token"]).toBe(token1);

            service.setToken(token2);
            expect(localStorageMock["jwt_token"]).toBe(token2);
        });
    });

    describe("getToken", () => {
        it("should retrieve the token from localStorage", () => {
            const token = "test-jwt-token";
            localStorageMock["jwt_token"] = token;

            const result = service.getToken();

            expect(localStorage.getItem).toHaveBeenCalledWith("jwt_token");
            expect(result).toBe(token);
        });

        it("should return null if no token exists", () => {
            const result = service.getToken();

            expect(result).toBeNull();
        });
    });

    describe("clearToken", () => {
        it("should remove the token from localStorage", () => {
            localStorageMock["jwt_token"] = "test-token";

            service.clearToken();

            expect(localStorage.removeItem).toHaveBeenCalledWith("jwt_token");
            expect(localStorageMock["jwt_token"]).toBeUndefined();
        });

        it("should not throw error if token doesn't exist", () => {
            expect(() => service.clearToken()).not.toThrow();
        });
    });

    describe("hasToken", () => {
        it("should return true if token exists", () => {
            localStorageMock["jwt_token"] = "test-token";

            const result = service.hasToken();

            expect(result).toBe(true);
        });

        it("should return false if token is null", () => {
            const result = service.hasToken();

            expect(result).toBe(false);
        });

        it("should return false if token is empty string", () => {
            localStorageMock["jwt_token"] = "";

            const result = service.hasToken();

            expect(result).toBe(false);
        });
    });
});
