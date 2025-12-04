package be.kdg.ipj3.shop.infrastructure.mollie;

import be.woutschoovaerts.mollie.Client;
import be.woutschoovaerts.mollie.ClientBuilder;
import be.woutschoovaerts.mollie.data.common.Amount;
import be.woutschoovaerts.mollie.data.payment.PaymentRequest;
import be.woutschoovaerts.mollie.data.payment.PaymentResponse;
import be.woutschoovaerts.mollie.exception.MollieException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class MollieGateway {
    private final Client client;

    public MollieGateway(@Value("${mollie.api-key}") String apiKey) {
        this.client = new ClientBuilder()
                .withApiKey(apiKey)
                .build();
    }

    public PaymentResponse createPayment(BigDecimal price, String description, String redirectUrl, String webhookUrl) {
        try {
            PaymentRequest paymentRequest = PaymentRequest.builder()
                    .amount(Amount.builder()
                            .currency("EUR")
                            .value(price)
                            .build())
                    .description(description)
                    .redirectUrl(redirectUrl)
                    .webhookUrl(Optional.ofNullable(webhookUrl))
                    .build();

            return client.payments().createPayment(paymentRequest);

        } catch (MollieException e) {
            throw new RuntimeException("Failed to create Mollie payment: " + e.getMessage(), e);
        }
    }

    public boolean checkPaymentIsPaid(String paymentId) {
        try {
            PaymentResponse payment = client.payments().getPayment(paymentId);
            return "paid".equalsIgnoreCase(String.valueOf(payment.getStatus()));

        } catch (MollieException e) {
            throw new RuntimeException("Failed to check Mollie payment status: " + e.getMessage(), e);
        }
    }
}
