package com.exemplo.bancoalcatteia.config;

import com.exemplo.bancoalcatteia.model.Usuarios;
import com.exemplo.bancoalcatteia.service.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    private final SessionService sessionService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Verificar se já está autenticado
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Tentar obter usuário da sessão
        Optional<Usuarios> currentUser = sessionService.getCurrentUser(request);
        
        if (currentUser.isPresent()) {
            Usuarios user = currentUser.get();
            
            // Criar authorities baseado no role
            var authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getTipoUsuario().name())
            );

            // Criar token de autenticação
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    authorities
            );
            
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
            // Definir no contexto de segurança
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/usuarios/login") || 
               path.startsWith("/usuarios/criar") ||
               path.startsWith("/usuarios/logout") ||
               path.startsWith("/public") ||
               path.startsWith("/h2-console") ||
               path.startsWith("/swagger") ||
               path.startsWith("/v3/api-docs");
    }
} 