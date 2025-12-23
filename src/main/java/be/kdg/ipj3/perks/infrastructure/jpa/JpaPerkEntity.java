package be.kdg.ipj3.perks.infrastructure.jpa;

import be.kdg.ipj3.perks.domain.Perk;
import be.kdg.ipj3.perks.domain.PerkType;
import be.kdg.ipj3.perks.shared.PerkId;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "perks")
public class JpaPerkEntity {

    @Id
    @Column(name = "perk_id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private PerkType type;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private int pointCost;

    protected JpaPerkEntity() {
    } // for JPA

    public JpaPerkEntity(UUID id, PerkType type, String name, String description, int pointCost) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.pointCost = pointCost;
    }

    public static JpaPerkEntity fromDomain(Perk perk) {
        return new JpaPerkEntity(
                perk.getId().id(),
                perk.getType(),
                perk.getName(),
                perk.getDescription(),
                perk.getPointCost()
        );
    }

    public Perk toDomain(){
        return new Perk(new PerkId(id), type, name, description, pointCost);
    }
}
