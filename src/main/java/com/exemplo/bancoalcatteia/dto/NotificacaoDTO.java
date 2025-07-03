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
public class NotificacaoDTO {

    private Integer id;

    @NotNull(message = "ID do usuário notificado é obrigatório")
    private Long usuarioNotificadoId;
    private String usuarioNotificadoNome;

    @NotBlank(message = "Tipo de notificação é obrigatório")
    private String tipoNotificacao;

    private Long tarefaId;
    private String tarefaTitulo;

    @NotBlank(message = "Texto da notificação é obrigatório")
    @Size(max = 1000, message = "Texto da notificação deve ter no máximo 1000 caracteres")
    private String textoNotificacao;

    private String dataCriacao;

    private Boolean confirmacaoLeitura;

    // Construtores de conveniência
    public NotificacaoDTO(Long usuarioNotificadoId, String tipoNotificacao, String textoNotificacao) {
        this.usuarioNotificadoId = usuarioNotificadoId;
        this.tipoNotificacao = tipoNotificacao;
        this.textoNotificacao = textoNotificacao;
        this.confirmacaoLeitura = false;
    }

    public NotificacaoDTO(Long usuarioNotificadoId, String tipoNotificacao, String textoNotificacao, Long tarefaId) {
        this.usuarioNotificadoId = usuarioNotificadoId;
        this.tipoNotificacao = tipoNotificacao;
        this.textoNotificacao = textoNotificacao;
        this.tarefaId = tarefaId;
        this.confirmacaoLeitura = false;
    }

    // Métodos auxiliares
    public boolean isLida() {
        return Boolean.TRUE.equals(this.confirmacaoLeitura);
    }

    public boolean isNaoLida() {
        return !isLida();
    }

    public boolean temTarefa() {
        return this.tarefaId != null;
    }

    public boolean isTipoTarefaAtribuida() {
        return "TAREFA_ATRIBUIDA".equals(this.tipoNotificacao);
    }

    public boolean isTipoPrazoConclusao() {
        return "PRAZO_CONCLUSAO".equals(this.tipoNotificacao);
    }

    public boolean isTipoComentario() {
        return "COMENTARIO".equals(this.tipoNotificacao);
    }

    public boolean isTipoTarefaAtrasada() {
        return "TAREFA_ATRASADA".equals(this.tipoNotificacao);
    }

    public boolean isTipoTarefaConcluida() {
        return "TAREFA_CONCLUIDA".equals(this.tipoNotificacao);
    }

    public boolean isTipoTarefaAceita() {
        return "TAREFA_ACEITA".equals(this.tipoNotificacao);
    }

    public boolean isTipoTarefaRejeitada() {
        return "TAREFA_REJEITADA".equals(this.tipoNotificacao);
    }
} 
