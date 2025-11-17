package be.kdg.ipj3.shared.domain.profile;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record Bio(String bio) {
    public Bio{
        if(bio.length() > 500)
            throw new IllegalArgumentException("Bio length exceeds 500!");
    }
}
