package com.exemplo.bancoalcatteia.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tarefas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTarefa status = StatusTarefa.TODO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridade prioridade = Prioridade.MEDIA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private Usuarios responsavel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por_id", nullable = false)
    private Usuarios criadoPor;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_entrega")
    private LocalDate dataEntrega;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(name = "data_ultima_atualizacao")
    private LocalDateTime dataUltimaAtualizacao = LocalDateTime.now();

    @Column(name = "estimativa_horas", precision = 5, scale = 2)
    private BigDecimal estimativaHoras;

    @Column(name = "horas_trabalhadas", precision = 5, scale = 2)
    private BigDecimal horasTrabalhadas = BigDecimal.ZERO;

    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ParticipanteTarefa> participantes;

    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notificacao> notificacoes;

    public enum StatusTarefa {
        TODO, DOING, DONE
    }

    public enum Prioridade {
        BAIXA, MEDIA, ALTA
    }

    @PrePersist
    protected void onCreate() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
        if (dataUltimaAtualizacao == null) {
            dataUltimaAtualizacao = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataUltimaAtualizacao = LocalDateTime.now();
    }

    public boolean isAtrasada() {
        if (dataEntrega == null || status == StatusTarefa.DONE) {
            return false;
        }
        return LocalDate.now().isAfter(dataEntrega) && status == StatusTarefa.DOING;
    }

    public boolean isPrazoVencido() {
        if (dataEntrega == null) {
            return false;
        }
        return LocalDate.now().isAfter(dataEntrega) && status != StatusTarefa.DONE;
    }
} 
