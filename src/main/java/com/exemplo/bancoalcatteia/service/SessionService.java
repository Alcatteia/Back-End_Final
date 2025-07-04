package com.exemplo.bancoalcatteia.service;

import com.exemplo.bancoalcatteia.model.Usuarios;
import com.exemplo.bancoalcatteia.repository.UsuarioRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final UsuarioRepository usuarioRepository;
    
    // Cache em memória para armazenar sessões
    private final Map<String, Integer> sessionCache = new ConcurrentHashMap<>();
    
    private static final String SESSION_COOKIE_NAME = "ALCATTEIA_SESSION";
    private static final int COOKIE_MAX_AGE = 24 * 60 * 60; // 24 horas em segundos

    /**
     * Cria uma nova sessão para o usuário
     */
    public String createSession(Integer userId, HttpServletResponse response) {
        String sessionId = UUID.randomUUID().toString();
        
        // Armazenar no cache
        sessionCache.put(sessionId, userId);
        
        // Criar cookie
        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(false); // Para desenvolvimento local, mudar para true em produção
        
        response.addCookie(cookie);
        
        return sessionId;
    }

    /**
     * Obtém o userId da sessão atual
     */
    public Optional<Integer> getCurrentUserId(HttpServletRequest request) {
        String sessionId = getSessionIdFromRequest(request);
        if (sessionId == null) {
            return Optional.empty();
        }
        
        Integer userId = sessionCache.get(sessionId);
        return Optional.ofNullable(userId);
    }

    /**
     * Obtém o usuário completo da sessão atual
     */
    public Optional<Usuarios> getCurrentUser(HttpServletRequest request) {
        Optional<Integer> userId = getCurrentUserId(request);
        if (userId.isEmpty()) {
            return Optional.empty();
        }
        
        return Optional.ofNullable(usuarioRepository.findById(userId.get()).orElse(null));
    }

    /**
     * Remove a sessão (logout)
     */
    public void removeSession(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = getSessionIdFromRequest(request);
        if (sessionId != null) {
            sessionCache.remove(sessionId);
        }
        
        // Remover cookie
        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * Verifica se existe uma sessão válida
     */
    public boolean hasValidSession(HttpServletRequest request) {
        return getCurrentUserId(request).isPresent();
    }

    /**
     * Extrai o sessionId do request
     */
    private String getSessionIdFromRequest(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        
        for (Cookie cookie : request.getCookies()) {
            if (SESSION_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        
        return null;
    }

    /**
     * Limpa todas as sessões (útil para testes)
     */
    public void clearAllSessions() {
        sessionCache.clear();
    }
} 