package com.exemplo.bancoalcatteia.repository;

import com.exemplo.bancoalcatteia.model.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuarios, Integer> {
    public Usuarios findByEmail(String email);
    public Usuarios findByEmailAndSenha(String email, String senha);
}


