package com.exemplo.bancoalcatteia.service;

import com.exemplo.bancoalcatteia.model.Role;
import com.exemplo.bancoalcatteia.model.Usuarios;
import com.exemplo.bancoalcatteia.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Service responsável por gerenciar informações do usuário atual logado
 */
@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Obtém o ID do usuário atualmente logado através do JWT
     * @return ID do usuário logado
     */
    public Integer getCurrentUserId() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            Object userId = request.getAttribute("userId");
            if (userId instanceof Long) {
                return ((Long) userId).intValue();
            }
            if (userId instanceof Integer) {
                return (Integer) userId;
            }
        }
        
        // Fallback: tentar obter através do email do contexto de segurança
        return getCurrentUser().getId();
    }

    /**
     * Obtém o usuário atualmente logado
     * @return Entidade Usuarios completa
     */
    public Usuarios getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new EntityNotFoundException("Usuário não autenticado");
        }
        
        String email = auth.getName();
        Usuarios usuarios = usuarioRepository.findByEmail(email);
        if (usuarios == null) {
            throw new EntityNotFoundException("Usuário com email " + email + " não encontrado");
        }
        
        return usuarios;
    }

    /**
     * Obtém o role do usuário atual
     * @return Role do usuário
     */
    public Role getCurrentUserRole() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            Object userRole = request.getAttribute("userRole");
            if (userRole instanceof String) {
                return Role.valueOf((String) userRole);
            }
        }
        
        // Fallback: obter do banco
        return getCurrentUser().getTipoUsuario();
    }

    /**
     * Verifica se o usuário atual tem permissão de RH
     * @return true se for RH
     */
    public boolean isRH() {
        return getCurrentUserRole() == Role.RH;
    }

    /**
     * Verifica se o usuário atual tem permissão de LIDER
     * @return true se for LIDER
     */
    public boolean isLider() {
        return getCurrentUserRole() == Role.LIDER;
    }

    /**
     * Verifica se o usuário atual tem permissão de RH ou LIDER
     * @return true se for RH ou LIDER
     */
    public boolean isRHouLider() {
        Role role = getCurrentUserRole();
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
        
        // FUNC só pode acessar seus próprios dados
        return getCurrentUserId().equals(usuarioId);
    }

    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest();
        } catch (IllegalStateException e) {
            return null;
        }
    }
} 

