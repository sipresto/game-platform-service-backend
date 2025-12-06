package be.kdg.ipj3.shop.infrastructure.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.secret-key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public Charge charge(ChargeRequest chargeRequest) throws StripeException {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", chargeRequest.amount());
        chargeParams.put("currency", chargeRequest.currency().getCurrencyCode().toLowerCase());
        chargeParams.put("description", chargeRequest.description());
        chargeParams.put("source", chargeRequest.stripeToken());
        return Charge.create(chargeParams);
    }

    public Charge retrieve(String chargeId) throws StripeException {
        return Charge.retrieve(chargeId);
    }
}
