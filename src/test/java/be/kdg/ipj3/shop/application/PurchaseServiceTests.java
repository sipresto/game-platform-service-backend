//package be.kdg.ipj3.shop.application;
//
//import be.kdg.ipj3.catalog.shared.GameId;
//import be.kdg.ipj3.users.shared.UserId;
//import be.kdg.ipj3.shop.domain.Purchase;
//import be.kdg.ipj3.shop.infrastructure.PurchaseRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.UUID;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//
//
//@ExtendWith(MockitoExtension.class)
//class PurchaseServiceTests {
//
//    @Mock
//    PurchaseRepository purchaseRepository;
//
//    @InjectMocks
//    PurchaseService purchaseService;
//
//    @Test
//    void buy_ShouldCreatePurchaseAndSaveIt() {
//        // Arrange
//        String keycloakId = UUID.randomUUID().toString();
//        UserId userId = new UserId(UUID.fromString(keycloakId));
//        UUID gameUUID = UUID.randomUUID();
//        GameId gameId = new GameId(gameUUID);
//        String chargeId = "TestId";
//
//        Purchase savedPurchase = new Purchase(userId, gameId, chargeId);
//        given(purchaseRepository.save(any(Purchase.class))).willReturn(savedPurchase);
//
//        // Act
//        Purchase result = purchaseService.buy(keycloakId, gameUUID, chargeId);
//
//        // Assert
//        assertThat(result).isNotNull();
//        assertThat(result.buyer()).isEqualTo(userId);
//        assertThat(result.game()).isEqualTo(gameId);
//
//        verify(purchaseRepository).save(any(Purchase.class));
//    }
//
//}