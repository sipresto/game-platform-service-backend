package be.kdg.ipj3.shared.domain;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String keycloakId) {
        super("User with KC id [" + keycloakId + "] not found");
    }
}
