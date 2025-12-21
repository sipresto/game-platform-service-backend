package be.kdg.ipj3.perks.shared;

import be.kdg.ipj3.shared.domain.exceptions.NotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;
import java.util.UUID;

@ValueObject
public record PerkId(UUID id) {
    public PerkId {
        Objects.requireNonNull(id, "Perk id cannot be null!");
    }

    public NotFoundException notFound() {
        return new NotFoundException("Perk [" + id + "] not found");
    }

}