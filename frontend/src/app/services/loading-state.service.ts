import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";
import { finalize, tap } from "rxjs/operators";

/**
 * Service for managing loading states in components
 * Can be injected at component level for isolated loading states
 */
@Injectable()
export class LoadingStateService {
    private loadingSubject = new BehaviorSubject<boolean>(false);
    public loading$ = this.loadingSubject.asObservable();

    private errorSubject = new BehaviorSubject<string | null>(null);
    public error$ = this.errorSubject.asObservable();

    /**
     * Wraps an observable with automatic loading state management
     */
    fetch<T>(
        observable: Observable<T>,
        errorMessage: string = "Failed to load data",
    ): Observable<T> {
        this.loadingSubject.next(true);
        this.errorSubject.next(null);

        return observable.pipe(
            tap({
                error: (err) => {
                    this.errorSubject.next(errorMessage);
                    console.error(errorMessage, err);
                },
            }),
            finalize(() => {
                this.loadingSubject.next(false);
            }),
        );
    }

    /**
     * Manually set loading state
     */
    setLoading(loading: boolean): void {
        this.loadingSubject.next(loading);
    }

    /**
     * Set error message
     */
    setError(error: string | null): void {
        this.errorSubject.next(error);
    }

    /**
     * Clear error
     */
    clearError(): void {
        this.errorSubject.next(null);
    }

    /**
     * Reset state
     */
    reset(): void {
        this.loadingSubject.next(false);
        this.errorSubject.next(null);
    }
}
