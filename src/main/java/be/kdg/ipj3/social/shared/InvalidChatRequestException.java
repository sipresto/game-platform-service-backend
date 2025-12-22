package be.kdg.ipj3.social.shared;

public class InvalidChatRequestException extends RuntimeException {
    public InvalidChatRequestException(String message) {
        super(message);
    }
}
