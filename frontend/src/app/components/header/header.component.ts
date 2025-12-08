import { Component, Input } from "@angular/core";
import { getCurrentYear } from "../../utils/globals";

@Component({
    selector: "app-header",
    standalone: true,
    imports: [],
    templateUrl: "./header.component.html",
    styleUrl: "./header.component.css",
})
export class HeaderComponent {
    @Input() title: string = "Secret Santa";

    currentYear = getCurrentYear();
}
