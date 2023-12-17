package live.match.service;

public class InvalidMatchStateException extends Exception {
    public InvalidMatchStateException(String message) {
        super(message);
    }
}
