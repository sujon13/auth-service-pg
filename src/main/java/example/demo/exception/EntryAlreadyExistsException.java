package example.demo.exception;

public class EntryAlreadyExistsException extends RuntimeException {
    public EntryAlreadyExistsException(String message) {
        super(message);
    }
}
