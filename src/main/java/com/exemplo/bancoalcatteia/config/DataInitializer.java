package com.exemplo.bancoalcatteia.config;

import com.exemplo.bancoalcatteia.model.Role;
import com.exemplo.bancoalcatteia.model.Usuarios;
import com.exemplo.bancoalcatteia.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Sempre verificar se Ana Silva existe
        if (!usuarioRepository.existsByEmail("ana.silva@bancoalcatteia.com")) {
            System.out.println("Criando usuário Ana Silva...");
            criarUsuario("Ana Silva", "ana.silva@bancoalcatteia.com", "123456", Role.RH, "1985-03-15", "Gestora de RH especializada em bem-estar organizacional");
        }

        // Sempre verificar se Carlos existe  
        if (!usuarioRepository.existsByEmail("carlos.mendes.novo@bancoalcatteia.com")) {
            System.out.println("Criando usuário Carlos Mendes (novo)...");
            criarUsuario("Carlos Mendes", "carlos.mendes.novo@bancoalcatteia.com", "123456", Role.LIDER, "1988-07-22", "Líder técnico com experiência em arquitetura de software");
        }

        // Criar usuário admin para testes
        if (!usuarioRepository.existsByEmail("admin@bancoalcatteia.com")) {
            System.out.println("Criando usuário admin...");
            criarUsuario("Administrador", "admin@bancoalcatteia.com", "admin123", Role.RH, "1980-01-01", "Administrador do sistema");
        }

        System.out.println("Verificação de usuários essenciais concluída!");
    }

    private void criarUsuario(String nome, String email, String senha, Role tipo, String dataNascimento, String descricao) {
        try {
            Usuarios usuario = new Usuarios();
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setSenha(passwordEncoder.encode(senha)); // IMPORTANTE: Criptografar a senha
            usuario.setTipoUsuario(tipo);
//            usuario.setDataCriacao().tDataNascimento(LocalDate.parse(dataNascimento));
            usuario.setDescricao(descricao);
            usuario.setAtivo(true);
            
            usuarioRepository.save(usuario);
            System.out.println("✅ Usuário criado com senha criptografada: " + email);
        } catch (Exception e) {
            System.err.println("❌ Erro ao criar usuário " + email + ": " + e.getMessage());
        }
    }
} 