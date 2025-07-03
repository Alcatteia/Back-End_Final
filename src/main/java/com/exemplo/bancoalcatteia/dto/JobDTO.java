package com.exemplo.bancoalcatteia.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {

    private Integer id;

    @NotBlank(message = "Nome do job é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String descricao;

    @NotBlank(message = "Tipo do job é obrigatório")
    private String tipo;

    private String status;

    @NotNull(message = "Data de agendamento é obrigatória")
    private String dataAgendamento;

    private String dataExecucao;
    private String dataConclusao;
    private String dataCriacao;

    @NotNull(message = "ID do criador é obrigatório")
    private Long criadoPorId;
    private String criadoPorNome;

    private String cronExpression;

    @Size(max = 2000, message = "Parâmetros devem ter no máximo 2000 caracteres")
    private String parametros;

    private String resultado;
    private String erro;

    private Boolean ativo;

    private Integer tentativas;
    private Integer maxTentativas;

    // Construtores de conveniência
    public JobDTO(String nome, String tipo, String dataAgendamento) {
        this.nome = nome;
        this.tipo = tipo;
        this.dataAgendamento = dataAgendamento;
        this.status = "AGENDADO";
        this.ativo = true;
        this.tentativas = 0;
        this.maxTentativas = 3;
    }

    public JobDTO(String nome, String descricao, String tipo, String dataAgendamento) {
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
        this.dataAgendamento = dataAgendamento;
        this.status = "AGENDADO";
        this.ativo = true;
        this.tentativas = 0;
        this.maxTentativas = 3;
    }

    // Métodos auxiliares
    public boolean isAgendado() {
        return "AGENDADO".equals(this.status);
    }

    public boolean isExecutando() {
        return "EXECUTANDO".equals(this.status);
    }

    public boolean isConcluido() {
        return "CONCLUIDO".equals(this.status);
    }

    public boolean isComErro() {
        return "ERRO".equals(this.status);
    }

    public boolean isCancelado() {
        return "CANCELADO".equals(this.status);
    }

    public boolean isPausado() {
        return "PAUSADO".equals(this.status);
    }

    public boolean isAtivo() {
        return Boolean.TRUE.equals(this.ativo);
    }

    public boolean temErro() {
        return this.erro != null && !this.erro.trim().isEmpty();
    }

    public boolean temResultado() {
        return this.resultado != null && !this.resultado.trim().isEmpty();
    }

    public boolean podeRetentar() {
        return isComErro() && tentativas != null && maxTentativas != null && tentativas < maxTentativas;
    }

    public boolean isTipoNotificacaoPrazo() {
        return "NOTIFICACAO_PRAZO".equals(this.tipo);
    }

    public boolean isTipoBackupDados() {
        return "BACKUP_DADOS".equals(this.tipo);
    }

    public boolean isTipoLimpezaLogs() {
        return "LIMPEZA_LOGS".equals(this.tipo);
    }

    public boolean isTipoRelatorioAutomatico() {
        return "RELATORIO_AUTOMATICO".equals(this.tipo);
    }

    public boolean isTipoEnvioEmail() {
        return "ENVIO_EMAIL".equals(this.tipo);
    }

    public boolean isTipoSincronizacao() {
        return "SINCRONIZACAO".equals(this.tipo);
    }

    public boolean isTipoManutencao() {
        return "MANUTENCAO".equals(this.tipo);
    }

    public boolean isRecorrente() {
        return this.cronExpression != null && !this.cronExpression.trim().isEmpty();
    }
} 
