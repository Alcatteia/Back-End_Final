package com.exemplo.bancoalcatteia.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "participantes_tarefa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipanteTarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarefa_id", nullable = false)
    private Tarefa tarefa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuarios usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_participacao", nullable = false)
    private StatusParticipacao statusParticipacao = StatusParticipacao.PENDENTE;

    @Column(name = "motivo_rejeicao", columnDefinition = "TEXT")
    private String motivoRejeicao;

    @Column(name = "data_convite", nullable = false)
    private LocalDateTime dataConvite = LocalDateTime.now();

    @Column(name = "data_resposta")
    private LocalDateTime dataResposta;

    @Column(nullable = false)
    private Boolean ativo = true;

    public enum StatusParticipacao {
        PENDENTE, ACEITO, REJEITADO, REMOVIDO
    }

    @PrePersist
    protected void onCreate() {
        if (dataConvite == null) {
            dataConvite = LocalDateTime.now();
        }
        if (ativo == null) {
            ativo = true;
        }
    }

    public void aceitar() {
        this.statusParticipacao = StatusParticipacao.ACEITO;
        this.dataResposta = LocalDateTime.now();
        this.motivoRejeicao = null;
    }

    public void rejeitar(String motivo) {
        this.statusParticipacao = StatusParticipacao.REJEITADO;
        this.dataResposta = LocalDateTime.now();
        this.motivoRejeicao = motivo;
    }

    public void remover() {
        this.statusParticipacao = StatusParticipacao.REMOVIDO;
        this.ativo = false;
    }

    public boolean isPendente() {
        return StatusParticipacao.PENDENTE.equals(this.statusParticipacao);
    }

    public boolean isAceito() {
        return StatusParticipacao.ACEITO.equals(this.statusParticipacao);
    }

    public boolean isRejeitado() {
        return StatusParticipacao.REJEITADO.equals(this.statusParticipacao);
    }
} 
