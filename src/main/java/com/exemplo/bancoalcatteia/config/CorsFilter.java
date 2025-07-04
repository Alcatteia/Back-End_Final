package com.exemplo.bancoalcatteia.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtro CORS adicional para garantir compatibilidade máxima com diferentes clientes
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    private static final String ALLOWED_ORIGIN_1 = "http://localhost:5173";
    private static final String ALLOWED_ORIGIN_2 = "http://localhost:8080";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        String origin = request.getHeader("Origin");
        
        // Definir headers CORS permissivos para desenvolvimento
        if (ALLOWED_ORIGIN_1.equals(origin) || ALLOWED_ORIGIN_2.equals(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        } else {
            // Fallback ou negação para origens não explicitamente permitidas
            response.setHeader("Access-Control-Allow-Origin", ALLOWED_ORIGIN_1); // Ou uma origem padrão segura
        }

        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", 
                "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD, TRACE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", 
                "Origin, Content-Type, Accept, Authorization, X-Requested-With, " +
                "X-Auth-Token, X-Custom-Header, Cache-Control, Pragma, Expires, " +
                "Last-Modified, If-Modified-Since");
        response.setHeader("Access-Control-Expose-Headers", 
                "Access-Control-Allow-Origin, Access-Control-Allow-Credentials, " +
                "Access-Control-Allow-Headers, Access-Control-Allow-Methods, " +
                "Access-Control-Max-Age, Content-Type, Authorization, X-Requested-With, " +
                "X-Total-Count, X-Custom-Header, Cache-Control, Content-Language, " +
                "Content-Length, Last-Modified");

        // Para requisições OPTIONS (preflight), retornar OK sem processar
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Continuar com a requisição normal
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // Inicialização não necessária
    }

    @Override
    public void destroy() {
        // Cleanup não necessário
    }
} 
