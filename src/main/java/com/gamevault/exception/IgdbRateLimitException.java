package com.gamevault.exception;

public class IgdbRateLimitException extends RuntimeException{
    public IgdbRateLimitException() {
        super("IGDB rate limit exceeded");
    }

    public IgdbRateLimitException(String message) {
        super(message);
    }
}
