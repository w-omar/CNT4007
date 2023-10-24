package exceptions;

public class InvalidMessageByteStreamException extends Exception {
    public InvalidMessageByteStreamException (String message) {
        super(message);
    }
}