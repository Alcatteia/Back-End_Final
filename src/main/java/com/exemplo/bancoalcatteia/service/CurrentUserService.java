package com.exemplo.bancoalcatteia.service;

import com.exemplo.bancoalcatteia.model.Role;
import com.exemplo.bancoalcatteia.model.Usuarios;
import com.exemplo.bancoalcatteia.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * Service responsável por gerenciar informações do usuário atual logado
 */
@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UsuarioRepository usuarioRepository;
    private final SessionService sessionService;

    /**
     * Obtém o ID do usuário atualmente logado através da sessão
     * @return ID do usuário logado, ou null se não autenticado
     */
    public Integer getCurrentUserId() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return sessionService.getCurrentUserId(request).orElse(null);
        }
        return null;
    }

    /**
     * Obtém o usuário atualmente logado
     * @return Optional contendo a entidade Usuarios completa, ou Optional.empty() se não autenticado
     */
    public Optional<Usuarios> getCurrentUser() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            return sessionService.getCurrentUser(request);
        }
        return Optional.empty();
    }

    /**
     * Obtém o role do usuário atual
     * @return Role do usuário, ou null se não autenticado
     */
    public String getCurrentUserRole() {
        return getCurrentUser()
                .map(user -> user.getTipoUsuario().name())
                .orElse(null);
    }

    /**
     * Obtém o role do usuário atual como enum
     * @return Role do usuário, ou null se não autenticado
     */
    public Role getCurrentUserRoleEnum() {
        return getCurrentUser()
                .map(Usuarios::getTipoUsuario)
                .orElse(null);
    }

    /**
     * Verifica se o usuário atual tem um role específico
     * @param role Role a ser verificado
     * @return true se o usuário tem o role, false caso contrário
     */
    public boolean hasRole(String role) {
        String currentRole = getCurrentUserRole();
        return currentRole != null && currentRole.equals(role);
    }

    /**
     * Verifica se o usuário atual tem permissão de RH
     * @return true se for RH
     */
    public boolean isRH() {
        return getCurrentUserRoleEnum() == Role.RH;
    }

    /**
     * Verifica se o usuário atual tem permissão de LIDER
     * @return true se for LIDER
     */
    public boolean isLider() {
        return getCurrentUserRoleEnum() == Role.LIDER;
    }

    /**
     * Verifica se o usuário atual tem permissão de RH ou LIDER
     * @return true se for RH ou LIDER
     */
    public boolean isRHouLider() {
        Role role = getCurrentUserRoleEnum();
        return role == Role.RH || role == Role.LIDER;
    }

    /**
     * Verifica se o usuário atual pode acessar dados de outro usuário
     * @param usuarioId ID do usuário que se deseja acessar
     * @return true se pode acessar
     */
    public boolean canAccessUserData(Integer usuarioId) {
        // RH pode acessar dados de qualquer pessoa
        if (isRH()) {
            return true;
        }
        
        // LIDER pode acessar dados de qualquer pessoa (por enquanto)
        if (isLider()) {
            return true;
        }

        // FUNC só pode acessar seus próprios dados, se estiver autenticado
        return getCurrentUserId() != null && getCurrentUserId().equals(usuarioId);
    }

    /**
     * Verifica se existe um usuário logado
     * @return true se há usuário logado, false caso contrário
     */
    public boolean isAuthenticated() {
        HttpServletRequest request = getCurrentRequest();
        return request != null && sessionService.hasValidSession(request);
    }

    /**
     * Obtém o HttpServletRequest atual
     * @return HttpServletRequest atual ou null se não disponível
     */
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
} 

