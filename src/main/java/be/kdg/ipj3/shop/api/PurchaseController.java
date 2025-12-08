package be.kdg.ipj3.shop.api;

import be.kdg.ipj3.shop.api.dto.PurchaseDto;
import be.kdg.ipj3.shop.application.PurchaseService;
import be.kdg.ipj3.shop.domain.Purchase;
import be.kdg.ipj3.shop.infrastructure.stripe.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final StripeService stripeService;

    public PurchaseController(PurchaseService purchaseService, StripeService stripeService) {
        this.purchaseService = purchaseService;
        this.stripeService = stripeService;
    }

    @PostMapping("/{gameIdParam}")
    public ResponseEntity<PurchaseDto> buyGame(
            @PathVariable UUID gameIdParam,
            @RequestBody Map<String, String> requestBody,
            @AuthenticationPrincipal Jwt token
    ) {
        String chargeId = requestBody.get("chargeId");

        if (chargeId == null || chargeId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Charge charge = stripeService.retrieve(chargeId);

            if (!charge.getPaid() || !"succeeded".equals(charge.getStatus())) {
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).build();
            }

            Purchase purchase = purchaseService.buy(token.getSubject(), gameIdParam, chargeId);

            return ResponseEntity.ok(PurchaseDto.fromDomain(purchase));

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
