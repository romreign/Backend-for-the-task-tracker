package main.java.exceptions;

public class RegisterClientException extends RuntimeException {
    public RegisterClientException() {
        super("Ошибка регистрации на KV сервере.");
    }
}
