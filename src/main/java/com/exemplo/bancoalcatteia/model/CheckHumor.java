package com.exemplo.bancoalcatteia.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "check_humor", uniqueConstraints = {@UniqueConstraint(columnNames = {"usuario_id", "data_registro"})})
public class CheckHumor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuarios usuario;

    @Column(name = "data_registro")
    private LocalDate dataRegistro;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Humor humor;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "anonimo")
    private Boolean anonimo = false;

    // Novos campos para funcionalidades avançadas
    @Column(name = "bem_estar_pontos")
    private Integer bemEstarPontos;

    @Column(name = "confirmado")
    private Boolean confirmado = false;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        if (this.dataRegistro == null) {
            this.dataRegistro = LocalDate.now();
        }
        if (this.anonimo == null) {
            this.anonimo = false;
        }
        if (this.confirmado == null) {
            this.confirmado = false;
        }
        // Definir pontos de bem-estar automaticamente baseado no humor
        if (this.humor != null && this.bemEstarPontos == null) {
            this.bemEstarPontos = this.humor.getBemEstarPontos();
        }
    }



    public enum Humor {
        CONTENTE("Contente", 5, 2),
        MOTIVADO("Motivado", 4, 1),
        CALMO("Calmo", 3, 0),
        NEUTRO("Neutro", 0, 0),
        DESMOTIVADO("Desmotivado", 2, -1),
        ESTRESSADO("Estressado", 1, -2);

        private final String descricao;
        private final int opcao;
        private final int bemEstarPontos;

        Humor(String descricao, int opcao, int bemEstarPontos) {
            this.descricao = descricao;
            this.opcao = opcao;
            this.bemEstarPontos = bemEstarPontos;
        }

        public String getDescricao() {
            return descricao;
        }

        public int getOpcao() {
            return opcao;
        }

        public int getBemEstarPontos() {
            return bemEstarPontos;
        }

        // Converte humor para valor numérico para cálculos de relatório (compatibilidade)
        public int getValorNumerico() {
            return opcao;
        }

        // Método para obter humor pela opção numérica
        public static Humor fromOpcao(int opcao) {
            for (Humor humor : values()) {
                if (humor.opcao == opcao) {
                    return humor;
                }
            }
            throw new IllegalArgumentException("Opção inválida: " + opcao);
        }

        // Descrição personalizada por gênero
        public String getDescricaoPorGenero(String sexoUsuario) {
            if (sexoUsuario == null || sexoUsuario.isEmpty()) {
                return this.descricao;
            }

            return switch (this) {
                case ESTRESSADO -> switch (sexoUsuario.toUpperCase()) {
                    case "M" -> "Estressado :(";
                    case "F" -> "Estressada :(";
                    default -> "Estressade :(";
                };
                case DESMOTIVADO -> switch (sexoUsuario.toUpperCase()) {
                    case "M" -> "Desmotivado :/";
                    case "F" -> "Desmotivada :/";
                    default -> "Desmotivade :/";
                };
                case CALMO -> switch (sexoUsuario.toUpperCase()) {
                    case "M" -> "Calmo :|";
                    case "F" -> "Calma :|";
                    default -> "Calme :|";
                };
                case NEUTRO -> "Neutro";
                case MOTIVADO -> switch (sexoUsuario.toUpperCase()) {
                    case "M" -> "Motivado :)";
                    case "F" -> "Motivada :)";
                    default -> "Motivade :)";
                };
                case CONTENTE -> "Contente :D";
            };
        }
    }
}
