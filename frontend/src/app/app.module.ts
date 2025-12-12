import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { FormsModule } from "@angular/forms";

import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
import { HeaderComponent } from "./components/header/header.component";
import { FooterComponent } from "./components/footer/footer.component";
import { RecipientService } from "./services/recipient.service";

@NgModule({
    declarations: [AppComponent],
    imports: [BrowserModule, AppRoutingModule, FormsModule, HeaderComponent, FooterComponent],
    providers: [RecipientService, provideHttpClient(withInterceptorsFromDi())],
    bootstrap: [AppComponent],
})
export class AppModule {}
