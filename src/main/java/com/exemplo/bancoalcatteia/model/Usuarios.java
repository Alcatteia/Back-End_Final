package com.exemplo.bancoalcatteia.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade principal de usuários do sistema
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="usuarios")
public class Usuarios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Email(message = "Email deve ser válido")
    @NotBlank(message = "Email é obrigatório")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String senha;

    @Column(name = "tipo_usuario")
    @Enumerated(EnumType.STRING)
    private Role tipoUsuario;

    @Column(name = "data_criacao")
    private LocalDate dataCriacao;

    @Column(name = "data_ultima_atualizacao")
    private LocalDateTime dataUltimaAtualizacao;

    private String descricao;

    @Column(name = "ativo")
    private Boolean ativo = true;

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDate.now();
        this.dataUltimaAtualizacao = LocalDateTime.now();
        if (this.ativo == null) {
            this.ativo = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }
}

