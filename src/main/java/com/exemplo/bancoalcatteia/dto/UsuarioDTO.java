package com.exemplo.bancoalcatteia.dto;

import com.exemplo.bancoalcatteia.model.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UsuarioDTO {
    private Integer id;
    private String nome;
    private String email;
    private String senha;

    @JsonProperty("tipo_usuario")
    private Role tipoUsuario;

    @JsonProperty("data_criacao")
    private String dataCriacao;

    //private String imagem;
    private String descricao;
    
    // Campo para retornar token JWT no login
    private String token;
}
