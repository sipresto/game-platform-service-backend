package be.kdg.ipj3.shop.application;

import be.kdg.ipj3.catalog.application.GameService;
import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.shop.domain.IPurchaseRepository;
import be.kdg.ipj3.shop.domain.Purchase;
import be.kdg.ipj3.shop.infrastructure.mollie.MollieGateway;
import be.woutschoovaerts.mollie.data.payment.PaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PurchaseService {
    private final IPurchaseRepository purchaseRepository;
    private final MollieGateway mollieGateway;
    private final GameService gameService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.backend-url}")
    private String backendUrl;

    public PurchaseService(IPurchaseRepository purchaseRepository,
                           MollieGateway mollieGateway,
                           GameService gameService) {
        this.purchaseRepository = purchaseRepository;
        this.mollieGateway = mollieGateway;
        this.gameService = gameService;
    }

    public String initiatePurchase(UserId userId, GameId gameId) {
        // Get game price
        var game = gameService.findGameById(gameId);
        BigDecimal price = game.getPrice().amount();

        // URLs for Mollie
        String redirectUrl = frontendUrl + "/purchase/success?paymentId={paymentId}";
        String webhookUrl = backendUrl + "/api/purchases/webhook";

        PaymentResponse molliePayment = mollieGateway.createPayment(
                price,
                String.format("Purchased: %s",game.getName()),
                redirectUrl,
                webhookUrl
        );

        // Save pending purchase with Mollie payment ID
        Purchase purchase = new Purchase(userId, gameId, molliePayment.getId());
        purchaseRepository.save(purchase);

        // Return checkout URL for frontend redirect
        return molliePayment.getLinks().getCheckout().getHref();
    }

    public void handleWebhook(String paymentId) {
        // Mollie calls this webhook when payment status changes
        if (mollieGateway.checkPaymentIsPaid(paymentId)) {
            Optional<Purchase> purchaseOpt = purchaseRepository.findByPaymentId(paymentId);

            if (purchaseOpt.isPresent()) {
                Purchase purchase = purchaseOpt.get();
                Purchase paidPurchase = purchase.markAsPaid();
                purchaseRepository.save(paidPurchase);
            }
        }
    }

    public Optional<Purchase> findByPaymentId(String paymentId) {
        return purchaseRepository.findByPaymentId(paymentId);
    }
}