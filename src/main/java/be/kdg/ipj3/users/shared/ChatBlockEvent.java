package be.kdg.ipj3.users.shared;

import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent
public record ChatBlockEvent(
       UserId blocker,
       UserId blocked
) {
}
