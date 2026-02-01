import { TestBed, fakeAsync, tick } from "@angular/core/testing";
import { LoadingStateService } from "./loading-state.service";
import { of, throwError, delay } from "rxjs";
import { take } from "rxjs/operators";

describe("LoadingStateService", () => {
    let service: LoadingStateService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [LoadingStateService],
        });
        service = TestBed.inject(LoadingStateService);
    });

    it("should be created", () => {
        expect(service).toBeTruthy();
    });

    describe("initial state", () => {
        it("should start with loading false", (done) => {
            service.loading$.subscribe((loading) => {
                expect(loading).toBe(false);
                done();
            });
        });

        it("should start with no error", (done) => {
            service.error$.subscribe((error) => {
                expect(error).toBeNull();
                done();
            });
        });
    });

    describe("fetch", () => {
        it("should set loading to true during fetch", (done) => {
            const mockObservable = of("data").pipe(delay(10));
            const loadingStates: boolean[] = [];

            service.loading$.subscribe((loading) => {
                loadingStates.push(loading);
            });

            service.fetch(mockObservable).subscribe(() => {
                // Should have been true at some point
                expect(loadingStates).toContain(true);
                done();
            });
        });

        it("should set loading to false after successful fetch", fakeAsync(() => {
            const mockObservable = of("data");
            const loadingStates: boolean[] = [];

            const subscription = service.loading$.subscribe((loading) => {
                loadingStates.push(loading);
            });

            service.fetch(mockObservable).subscribe();
            tick(); // Process all microtasks

            // Should have gone from false -> true -> false
            expect(loadingStates[loadingStates.length - 1]).toBe(false);
            expect(loadingStates).toContain(true);
            subscription.unsubscribe();
        }));

        it("should clear error on successful fetch", fakeAsync(() => {
            service.setError("Previous error");

            const mockObservable = of("data");

            service.fetch(mockObservable).subscribe();
            tick();

            service.error$.pipe(take(1)).subscribe((error) => {
                expect(error).toBeNull();
            });
        }));

        it("should set error message on fetch failure", fakeAsync(() => {
            const errorMessage = "Failed to load data";
            const mockObservable = throwError(() => new Error("API Error"));

            service.fetch(mockObservable, errorMessage).subscribe(
                () => fail("should have failed"),
                () => {},
            );
            tick();

            service.error$.pipe(take(1)).subscribe((error) => {
                expect(error).toBe(errorMessage);
            });
        }));

        it("should use default error message if not provided", fakeAsync(() => {
            const mockObservable = throwError(() => new Error("API Error"));

            service.fetch(mockObservable).subscribe(
                () => fail("should have failed"),
                () => {},
            );
            tick();

            service.error$.pipe(take(1)).subscribe((error) => {
                expect(error).toBe("Failed to load data");
            });
        }));

        it("should set loading to false after fetch failure", fakeAsync(() => {
            const mockObservable = throwError(() => new Error("API Error"));

            service.fetch(mockObservable).subscribe(
                () => fail("should have failed"),
                () => {},
            );
            tick();

            service.loading$.pipe(take(1)).subscribe((loading) => {
                expect(loading).toBe(false);
            });
        }));

        it("should pass through the observable data", (done) => {
            const testData = { id: 1, name: "Test" };
            const mockObservable = of(testData);

            service.fetch(mockObservable).subscribe((data) => {
                expect(data).toEqual(testData);
                done();
            });
        });
    });

    describe("setLoading", () => {
        it("should manually set loading to true", (done) => {
            service.setLoading(true);

            service.loading$.subscribe((loading) => {
                expect(loading).toBe(true);
                done();
            });
        });

        it("should manually set loading to false", (done) => {
            service.setLoading(true);
            service.setLoading(false);

            service.loading$.subscribe((loading) => {
                expect(loading).toBe(false);
                done();
            });
        });
    });

    describe("setError", () => {
        it("should set error message", (done) => {
            const errorMessage = "Test error";
            service.setError(errorMessage);

            service.error$.subscribe((error) => {
                expect(error).toBe(errorMessage);
                done();
            });
        });

        it("should set error to null", (done) => {
            service.setError("Error");
            service.setError(null);

            service.error$.subscribe((error) => {
                expect(error).toBeNull();
                done();
            });
        });
    });

    describe("clearError", () => {
        it("should clear existing error", (done) => {
            service.setError("Test error");
            service.clearError();

            service.error$.subscribe((error) => {
                expect(error).toBeNull();
                done();
            });
        });
    });

    describe("reset", () => {
        it("should reset loading and error to initial state", (done) => {
            service.setLoading(true);
            service.setError("Test error");
            service.reset();

            let loadingChecked = false;
            let errorChecked = false;

            service.loading$.subscribe((loading) => {
                expect(loading).toBe(false);
                loadingChecked = true;
                if (loadingChecked && errorChecked) done();
            });

            service.error$.subscribe((error) => {
                expect(error).toBeNull();
                errorChecked = true;
                if (loadingChecked && errorChecked) done();
            });
        });
    });
});
