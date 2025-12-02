package be.kdg.ipj3.shop.infrastructure.purchaseRepository;

import be.kdg.ipj3.shared.domain.GameId;
import be.kdg.ipj3.shared.domain.UserId;
import be.kdg.ipj3.shop.domain.Purchase;
import be.kdg.ipj3.shop.domain.PurchaseId;
import be.kdg.ipj3.shop.infrastructure.PurchaseRepository;
import be.kdg.ipj3.shop.infrastructure.jpa.JpaPurchaseEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Import(PurchaseRepository.class)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
public class SaveTests {

    @Autowired
    PurchaseRepository purchaseRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void save_shouldPersistPurchase_whenPurchaseIsValid() {
        // Arrange
        PurchaseId purchaseId = PurchaseId.create();
        UserId buyer = UserId.create();
        GameId game = GameId.create();
        Instant purchasedAt = Instant.now();

        Purchase newPurchase = new Purchase(
                purchaseId,
                buyer,
                game,
                purchasedAt
        );

        // Act
        purchaseRepository.save(newPurchase);

        // Assert
        JpaPurchaseEntity foundPurchase = entityManager.find(JpaPurchaseEntity.class, purchaseId.id());
        assertThat(foundPurchase).isNotNull();
        assertThat(foundPurchase.getId()).isEqualTo(purchaseId.id());
        assertThat(foundPurchase.getBuyer()).isEqualTo(buyer.id());
        assertThat(foundPurchase.getGame()).isEqualTo(game.id());
        assertThat(foundPurchase.getPurchasedAt()).isEqualTo(purchasedAt);
    }

    @Test
    void save_shouldUpdatePurchase_whenPurchaseAlreadyExists() {
        // Arrange
        PurchaseId purchaseId = PurchaseId.create();
        UserId originalBuyer = UserId.create();
        GameId originalGame = GameId.create();
        Instant originalTime = Instant.now().minusSeconds(3600);

        Purchase originalPurchase = new Purchase(
                purchaseId,
                originalBuyer,
                originalGame,
                originalTime
        );
        purchaseRepository.save(originalPurchase);

        UserId newBuyer = UserId.create();
        GameId newGame = GameId.create();
        Instant newTime = Instant.now();

        Purchase updatedPurchase = new Purchase(
                purchaseId,
                newBuyer,
                newGame,
                newTime
        );

        // Act
        purchaseRepository.save(updatedPurchase);

        // Assert
        JpaPurchaseEntity foundPurchase = entityManager.find(JpaPurchaseEntity.class, purchaseId.id());
        assertThat(foundPurchase).isNotNull();
        assertThat(foundPurchase.getBuyer()).isEqualTo(newBuyer.id());
        assertThat(foundPurchase.getGame()).isEqualTo(newGame.id());
        assertThat(foundPurchase.getPurchasedAt()).isEqualTo(newTime);
    }
}