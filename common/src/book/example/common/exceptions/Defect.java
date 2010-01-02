package book.example.common.exceptions;

public class Defect extends RuntimeException {
    public Defect(String message) {
        super(message);
    }

    public Defect(String message, Throwable cause) {
        super(message, cause);
    }
}
