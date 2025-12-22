import { Component, OnDestroy } from "@angular/core";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { Gift } from "src/app/models/gift.model";
import { GiftService } from "src/app/services/gift.service";
import { LoadingStateService } from "src/app/services/loading-state.service";
import { ToastModule } from "primeng/toast";
import { MessageService as PrimeMessageService } from "primeng/api";
import { MessageService } from "src/app/services/message.service";
import { FormsModule } from "@angular/forms";
import { Subscription } from "rxjs";
import { CommonModule } from "@angular/common";

@Component({
    selector: "app-gift-detail",
    imports: [RouterLink, ToastModule, FormsModule, CommonModule],
    providers: [LoadingStateService],
    templateUrl: "./gift-detail.component.html",
    styleUrl: "./gift-detail.component.css",
})
export class GiftDetailComponent {
    giftId: string = "";
    gift: Gift | null = null;

    loading$ = this.loadingStateService.loading$;
    error$ = this.loadingStateService.error$;

    private errorSubscription?: Subscription;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private giftService: GiftService,
        private loadingStateService: LoadingStateService,
        private messageService: MessageService,
    ) {}

    ngOnInit() {
        this.giftId = this.route.snapshot.paramMap.get("id") || "";

        if (this.giftId) {
            this.loadingStateService
                .fetch(
                    this.giftService.fetchGift(this.giftId),
                    "Failed to fetch gift ideas for Santa",
                )
                .subscribe({
                    next: (gift) => {
                        this.gift = gift;
                    },
                });
        } else {
            // Initialize empty gift for new gift creation
            this.gift = {
                id: "",
                userName: "",
                description: "",
                link: "",
                year: "",
            };
        }
    }

    onSubmit() {
        if (this.gift) {
            this.loadingStateService
                .fetch(this.giftService.saveGift(this.gift), "Failed to save gift")
                .subscribe({
                    next: (gift) => {
                        console.log("Gift saved, response:", gift);
                        this.messageService.showSuccess("Gift saved successfully");
                        this.router.navigate(["/gift-summary"]);
                    },
                });
        }
        this.error$.subscribe((error) => {
            if (error) {
                this.messageService.showError(error);
            }
        });
    }
}
