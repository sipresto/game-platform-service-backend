package be.kdg.ipj3.shop.infrastructure.stripe;

import be.kdg.ipj3.catalog.shared.GamePurchaseDto;
import be.kdg.ipj3.catalog.shared.GamesApi;
import be.kdg.ipj3.shared.domain.Money;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class StripeService {

    @Value("${stripe.secret-key}")
    private String secretKey;

    private final GamesApi gamesApi;

    public StripeService(GamesApi gamesApi) {
        this.gamesApi = gamesApi;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public Charge charge(UUID gameId, String stripeToken) throws StripeException {
        GamePurchaseDto game = gamesApi.findGameDtoById(gameId).orElse(null);
        // TODO: maybe throw an exception here
        if (game == null) {
            return null;
        }

        Money total = game.price();
        int amountInCents = total.amount().intValue() * 100;

        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", amountInCents);
        chargeParams.put("currency", total.currency().getCurrencyCode().toLowerCase());
        chargeParams.put("description", "Payment for: " + game.name());
        chargeParams.put("source", stripeToken);
        return Charge.create(chargeParams);
    }

    public Charge retrieve(String chargeId) throws StripeException {
        return Charge.retrieve(chargeId);
    }
}
