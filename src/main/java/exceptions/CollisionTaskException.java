package main.java.exceptions;

public class CollisionTaskException extends RuntimeException{
    public CollisionTaskException() {
        super();
    }

    public CollisionTaskException(final String message) {
        super(message);
    }
}
