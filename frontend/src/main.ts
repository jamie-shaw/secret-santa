import { bootstrapApplication } from "@angular/platform-browser";
import { provideZoneChangeDetection } from "@angular/core";
import { provideRouter } from "@angular/router";
import { provideHttpClient, withInterceptorsFromDi, HTTP_INTERCEPTORS } from "@angular/common/http";
import { providePrimeNG } from "primeng/config";
import Aura from "@primeng/themes/aura";
import { MessageService as PrimeMessageService } from "primeng/api";

import { AppComponent } from "./app/app.component";
import { routes } from "./app/app.routes";
import { RecipientService } from "./app/services/recipient/recipient.service";
import { AuthInterceptor } from "./app/interceptors/auth.interceptor";

bootstrapApplication(AppComponent, {
    providers: [
        provideZoneChangeDetection({ eventCoalescing: true }),
        provideRouter(routes),
        provideHttpClient(withInterceptorsFromDi()),
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true,
        },
        providePrimeNG({
            theme: {
                preset: Aura,
                options: {
                    darkModeSelector: false || "none",
                },
            },
        }),
        RecipientService,
        PrimeMessageService,
    ],
}).catch((err) => console.error(err));
