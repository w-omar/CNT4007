package exceptions;

public class InvalidMessageTypeException extends Exception {
    public InvalidMessageTypeException(String message) {
        super(message);
    }
}