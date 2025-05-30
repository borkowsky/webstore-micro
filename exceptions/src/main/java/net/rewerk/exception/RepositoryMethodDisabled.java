package net.rewerk.exception;

public class RepositoryMethodDisabled extends RuntimeException {
    public RepositoryMethodDisabled(String message) {
        super(message);
    }
}
