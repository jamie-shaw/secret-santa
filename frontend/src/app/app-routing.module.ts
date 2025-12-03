import { NgModule } from "@angular/core";
import { RouterModule, Routes } from "@angular/router";
import { ApiStatusComponent } from "./pages/api-status/api-status.component";
import { LoginComponent } from "./pages/login/login.component";

const routes: Routes = [
    { path: "", component: LoginComponent },
    { path: "api-status", component: ApiStatusComponent }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule],
})
export class AppRoutingModule {}
