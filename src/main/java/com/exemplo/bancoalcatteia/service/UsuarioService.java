package com.exemplo.bancoalcatteia.service;

import com.exemplo.bancoalcatteia.config.JwtService;
import com.exemplo.bancoalcatteia.dto.UsuarioDTO;
import com.exemplo.bancoalcatteia.model.Usuarios;
import com.exemplo.bancoalcatteia.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioDTO login(String email, String senha) {
        Usuarios usuarios = usuarioRepository.findByEmail(email);
        if(usuarios == null) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }
        
        if(!passwordEncoder.matches(senha, usuarios.getSenha())) {
            throw new IllegalArgumentException("Senha incorreta");
        }
        
        // Gerar token JWT
        String token = jwtService.generateToken(
            usuarios.getEmail(), 
            usuarios.getId(), 
            usuarios.getTipoUsuario().name()
        );
        
        UsuarioDTO dto = convertToDTO(usuarios);
        dto.setToken(token);
        return dto;
    }

    public UsuarioDTO buscarPorEmail(String email) {
        Usuarios usuarios = usuarioRepository.findByEmail(email);
        if(usuarios == null) {
            throw new EntityNotFoundException("Usuarios com email " + email + " não encontrado");
        }
        return convertToDTO(usuarios);
    }

    public UsuarioDTO buscar(Integer id) {
        Usuarios usuarios = usuarioRepository.findById(id).get();
        return convertToDTO(usuarios);
    }

    public UsuarioDTO criar(UsuarioDTO usuarioDTO) {
        Usuarios usuarios = convertToEntity(usuarioDTO);
        Usuarios usuariosSalvo = usuarioRepository.save(usuarios);

        return convertToDTO(usuariosSalvo);
    }

    private void validarUsuario(UsuarioDTO usuarioDTO) {

        if (usuarioDTO.getNome() == null || usuarioDTO.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do usuário é obrigatório. Por favor, preencha todos os campos.");
        }

        if (usuarioDTO.getEmail() == null || usuarioDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("O email do usuário é obrigatório. Por favor, preencha todos os campos.");
        }

        if (usuarioDTO.getSenha() == null || usuarioDTO.getSenha().trim().isEmpty()) {
            throw new IllegalArgumentException("A senha do usuário é obrigatória. Por favor, preencha todos os campos.");
        }

        if (usuarioDTO.getTipoUsuario() == null) {
            throw new IllegalArgumentException("O tipo do usuário é obrigatório. Por favor, preencha todos os campos.");
        }
    }

    public UsuarioDTO atualizar(Integer id, UsuarioDTO usuarioDTO) {
        Usuarios usuariosExistente = usuarioRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Usuário com ID " + id + " não encontrado"));

        //usuariosExistente.setImagem(usuarioDTO.getImagem());
        usuariosExistente.setDescricao(usuarioDTO.getDescricao());

        Usuarios usuariosAtualizada = usuarioRepository.save(usuariosExistente);

        return convertToDTO(usuariosAtualizada);
    }

    public void deletar(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário com ID " + id + " não encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    private Usuarios convertToEntity(UsuarioDTO dto) {
        Usuarios usuarios = new Usuarios();

        validarUsuario(dto);

        usuarios.setNome(dto.getNome());
        usuarios.setEmail(dto.getEmail());
        usuarios.setSenha(passwordEncoder.encode(dto.getSenha())); // Criptografar senha
        usuarios.setTipoUsuario(dto.getTipoUsuario());

        usuarios.setDataCriacao(LocalDate.now());

        return usuarios;
    }

    private UsuarioDTO convertToDTO(Usuarios usuarios) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuarios.getId());
        dto.setNome(usuarios.getNome());
        dto.setEmail(usuarios.getEmail());
        dto.setSenha(usuarios.getSenha());
        dto.setTipoUsuario(usuarios.getTipoUsuario());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate data = LocalDate.of(usuarios.getDataCriacao().getYear(),usuarios.getDataCriacao().getMonth(),usuarios.getDataCriacao().getDayOfMonth());
        String dataFormatada = data.format(formatter);
        dto.setDataCriacao(dataFormatada);
        usuarios.setDataCriacao(data);

        //dto.setImagem(usuarios.getImagem());
        dto.setDescricao(usuarios.getDescricao());
        return dto;
    }
}

