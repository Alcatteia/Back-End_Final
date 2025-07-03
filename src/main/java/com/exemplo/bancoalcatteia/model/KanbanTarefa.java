package com.exemplo.bancoalcatteia.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "kanban_tarefas")
public class KanbanTarefa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private KanbanCategoria categoria;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Usuarios responsavel;

    @ManyToOne(optional = false)
    @JoinColumn(name = "criado_por_id")
    private Usuarios criadoPor;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "data_entrega")
    private LocalDate dataEntrega;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "estimativa_horas", precision = 5, scale = 2)
    private BigDecimal estimativaHoras;

    @Column(name = "horas_trabalhadas", precision = 5, scale = 2)
    private BigDecimal horasTrabalhadas;

    private Integer ordem;

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        if (this.status == null) {
            this.status = Status.ATIVA;
        }
        if (this.prioridade == null) {
            this.prioridade = Prioridade.MEDIA;
        }
        if (this.horasTrabalhadas == null) {
            this.horasTrabalhadas = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.status == Status.CONCLUIDA && this.dataConclusao == null) {
            this.dataConclusao = LocalDateTime.now();
        }
        if (this.status != Status.CONCLUIDA) {
            this.dataConclusao = null;
        }
    }

    // Métodos de conveniência
    public boolean isAtrasada() {
        return dataEntrega != null && 
               LocalDate.now().isAfter(dataEntrega) && 
               status != Status.CONCLUIDA;
    }

    public BigDecimal calcularPorcentagemConclusao() {
        if (estimativaHoras == null || estimativaHoras.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (horasTrabalhadas == null) {
            return BigDecimal.ZERO;
        }
        return horasTrabalhadas.divide(estimativaHoras, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public enum Prioridade {
        BAIXA, MEDIA, ALTA
    }

    public enum Status {
        ATIVA, PAUSADA, CONCLUIDA, CANCELADA
    }
}
