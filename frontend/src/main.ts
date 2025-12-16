import { bootstrapApplication } from "@angular/platform-browser";
import { provideZoneChangeDetection } from "@angular/core";
import { provideRouter } from "@angular/router";
import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { providePrimeNG } from "primeng/config";
import Aura from "@primeng/themes/aura";
import { MessageService as PrimeMessageService } from "primeng/api";

import { AppComponent } from "./app/app.component";
import { routes } from "./app/app.routes";
import { RecipientService } from "./app/services/recipient.service";

bootstrapApplication(AppComponent, {
    providers: [
        provideZoneChangeDetection({ eventCoalescing: true }),
        provideRouter(routes),
        provideHttpClient(withInterceptorsFromDi()),
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
