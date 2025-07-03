package com.exemplo.bancoalcatteia.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuarios usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoFeedback tipo;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusFeedback status = StatusFeedback.PENDENTE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadeFeedback prioridade = PrioridadeFeedback.MEDIA;

    @Column(nullable = false)
    private Boolean anonimo = false;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_resposta")
    private LocalDateTime dataResposta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "respondido_por_id")
    private Usuarios respondidoPor;

    @Column(columnDefinition = "TEXT")
    private String resposta;

    @Column(nullable = false)
    private Boolean ativo = true;

    public enum TipoFeedback {
        SUGESTAO,
        RECLAMACAO,
        ELOGIO,
        BUG_REPORT,
        MELHORIA,
        DUVIDA
    }

    public enum StatusFeedback {
        PENDENTE,
        EM_ANALISE,
        RESPONDIDO,
        FECHADO,
        REJEITADO
    }

    public enum PrioridadeFeedback {
        BAIXA,
        MEDIA,
        ALTA,
        CRITICA
    }

    @PrePersist
    protected void onCreate() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
        if (status == null) {
            status = StatusFeedback.PENDENTE;
        }
        if (prioridade == null) {
            prioridade = PrioridadeFeedback.MEDIA;
        }
        if (anonimo == null) {
            anonimo = false;
        }
        if (ativo == null) {
            ativo = true;
        }
    }

    public void responder(Usuarios respondedor, String textoResposta) {
        this.respondidoPor = respondedor;
        this.resposta = textoResposta;
        this.dataResposta = LocalDateTime.now();
        this.status = StatusFeedback.RESPONDIDO;
    }

    public void fechar() {
        this.status = StatusFeedback.FECHADO;
    }

    public void analisar() {
        this.status = StatusFeedback.EM_ANALISE;
    }

    public void rejeitar() {
        this.status = StatusFeedback.REJEITADO;
    }

    public boolean isPendente() {
        return StatusFeedback.PENDENTE.equals(this.status);
    }

    public boolean isRespondido() {
        return StatusFeedback.RESPONDIDO.equals(this.status);
    }

    public boolean isFechado() {
        return StatusFeedback.FECHADO.equals(this.status);
    }
} 
