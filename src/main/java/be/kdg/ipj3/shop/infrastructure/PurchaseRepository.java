package be.kdg.ipj3.shop.infrastructure;

import be.kdg.ipj3.shop.domain.IPurchaseRepository;
import be.kdg.ipj3.shop.domain.Purchase;
import be.kdg.ipj3.shop.infrastructure.jpa.JpaPurchaseEntity;
import be.kdg.ipj3.shop.infrastructure.jpa.JpaPurchaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PurchaseRepository implements IPurchaseRepository {
    private final JpaPurchaseRepository jpaPurchaseRepository;

    public PurchaseRepository(JpaPurchaseRepository jpaPurchaseRepository) {
        this.jpaPurchaseRepository = jpaPurchaseRepository;
    }

    @Override
    public Purchase save(Purchase purchase) {
        return jpaPurchaseRepository.save(JpaPurchaseEntity.fromDomain(purchase)).toDomain();
    }
}
