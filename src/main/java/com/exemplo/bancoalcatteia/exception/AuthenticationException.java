package com.exemplo.bancoalcatteia.exception;

/**
 * Exceção lançada quando há problemas de autenticação
 */
public class AuthenticationException extends RuntimeException {
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
} 