import { Component } from "@angular/core";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { Gift } from "src/app/models/gift.model";
import { GiftService } from "src/app/services/gift.service";
import { LoadingStateService } from "src/app/services/loading-state.service";
import { ToastModule } from "primeng/toast";
import { MessageService as PrimeMessageService } from "primeng/api";
import { MessageService } from "src/app/services/message.service";

@Component({
    selector: "app-gift-detail",
    imports: [RouterLink, ToastModule],
    providers: [LoadingStateService],
    templateUrl: "./gift-detail.component.html",
    styleUrl: "./gift-detail.component.css",
})
export class GiftDetailComponent {
    giftId: string = "";
    gift: Gift | null = null;

    loading$ = this.loadingStateService.loading$;
    error$ = this.loadingStateService.error$;

    constructor(
        private route: ActivatedRoute,
        private giftService: GiftService,
        private loadingStateService: LoadingStateService,
        private messageService: MessageService,
    ) {}

    ngOnInit() {
        this.giftId = this.route.snapshot.paramMap.get("id") || "";

        this.loadingStateService
            .fetch(this.giftService.fetchGift(this.giftId), "Failed to fetch gift ideas for Santa")
            .subscribe({
                next: (gift) => {
                    this.gift = gift;
                },
            });

        this.error$.subscribe((error) => {
            if (error) {
                this.messageService.showError(error);
            }
        });
    }

    onSubmit() {
        // Handle form submission logic here
    }
}
