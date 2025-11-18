package be.kdg.ipj3.sessions.domain.session;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum SessionState {
    WAITING,
    ACTIVE,
    FINISHED
}
