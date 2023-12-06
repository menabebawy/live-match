package live.match.api;

public class InvalidMatchStateException extends Exception {
    public InvalidMatchStateException(String message) {
        super(message);
    }
}
