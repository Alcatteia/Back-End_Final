package com.exemplo.bancoalcatteia.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoJob tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusJob status = StatusJob.AGENDADO;

    @Column(name = "data_agendamento", nullable = false)
    private LocalDateTime dataAgendamento;

    @Column(name = "data_execucao")
    private LocalDateTime dataExecucao;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por_id", nullable = false)
    private Usuarios criadoPor;

    @Column(name = "cron_expression")
    private String cronExpression;

    @Column(name = "parametros", columnDefinition = "JSON")
    private String parametros;

    @Column(name = "resultado", columnDefinition = "TEXT")
    private String resultado;

    @Column(name = "erro", columnDefinition = "TEXT")
    private String erro;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "tentativas")
    private Integer tentativas = 0;

    @Column(name = "max_tentativas")
    private Integer maxTentativas = 3;

    public enum TipoJob {
        NOTIFICACAO_PRAZO,
        BACKUP_DADOS,
        LIMPEZA_LOGS,
        RELATORIO_AUTOMATICO,
        ENVIO_EMAIL,
        SINCRONIZACAO,
        MANUTENCAO
    }

    public enum StatusJob {
        AGENDADO,
        EXECUTANDO,
        CONCLUIDO,
        ERRO,
        CANCELADO,
        PAUSADO
    }

    @PrePersist
    protected void onCreate() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
        if (status == null) {
            status = StatusJob.AGENDADO;
        }
        if (ativo == null) {
            ativo = true;
        }
        if (tentativas == null) {
            tentativas = 0;
        }
        if (maxTentativas == null) {
            maxTentativas = 3;
        }
    }

    public void iniciarExecucao() {
        this.status = StatusJob.EXECUTANDO;
        this.dataExecucao = LocalDateTime.now();
        this.tentativas++;
    }

    public void concluirComSucesso(String resultado) {
        this.status = StatusJob.CONCLUIDO;
        this.dataConclusao = LocalDateTime.now();
        this.resultado = resultado;
        this.erro = null;
    }

    public void concluirComErro(String erro) {
        this.status = StatusJob.ERRO;
        this.dataConclusao = LocalDateTime.now();
        this.erro = erro;
    }

    public void cancelar() {
        this.status = StatusJob.CANCELADO;
        this.dataConclusao = LocalDateTime.now();
    }

    public void pausar() {
        this.status = StatusJob.PAUSADO;
    }

    public void reagendar(LocalDateTime novaData) {
        this.dataAgendamento = novaData;
        this.status = StatusJob.AGENDADO;
        this.dataExecucao = null;
        this.dataConclusao = null;
    }

    public boolean isAgendado() {
        return StatusJob.AGENDADO.equals(this.status);
    }

    public boolean isExecutando() {
        return StatusJob.EXECUTANDO.equals(this.status);
    }

    public boolean isConcluido() {
        return StatusJob.CONCLUIDO.equals(this.status);
    }

    public boolean isComErro() {
        return StatusJob.ERRO.equals(this.status);
    }

    public boolean podeRetentar() {
        return isComErro() && tentativas < maxTentativas;
    }

    public boolean isProntoParaExecucao() {
        return isAgendado() && LocalDateTime.now().isAfter(dataAgendamento);
    }
} 
