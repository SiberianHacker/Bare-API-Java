package ru.bareapi.lib.exception;

public class BareAPIException extends Exception {
	private static final long serialVersionUID = 1L;
	private final int statusCode;
    
    public BareAPIException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}