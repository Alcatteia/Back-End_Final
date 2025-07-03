package com.exemplo.bancoalcatteia.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipanteTarefaDTO {

    private Integer id;

    @NotNull(message = "ID da tarefa é obrigatório")
    private Long tarefaId;
    private String tarefaTitulo;

    @NotNull(message = "ID do usuário é obrigatório")
    private Long usuarioId;
    private String usuarioNome;
    private String usuarioEmail;

    private String statusParticipacao;

    @Size(max = 500, message = "Motivo de rejeição deve ter no máximo 500 caracteres")
    private String motivoRejeicao;

    private String dataConvite;
    private String dataResposta;

    private Boolean ativo;

    // Construtores de conveniência
    public ParticipanteTarefaDTO(Long tarefaId, Long usuarioId) {
        this.tarefaId = tarefaId;
        this.usuarioId = usuarioId;
        this.statusParticipacao = "PENDENTE";
        this.ativo = true;
    }

    public ParticipanteTarefaDTO(Long tarefaId, Long usuarioId, String statusParticipacao) {
        this.tarefaId = tarefaId;
        this.usuarioId = usuarioId;
        this.statusParticipacao = statusParticipacao;
        this.ativo = true;
    }

    // Métodos auxiliares
    public boolean isPendente() {
        return "PENDENTE".equals(this.statusParticipacao);
    }

    public boolean isAceito() {
        return "ACEITO".equals(this.statusParticipacao);
    }

    public boolean isRejeitado() {
        return "REJEITADO".equals(this.statusParticipacao);
    }

    public boolean isRemovido() {
        return "REMOVIDO".equals(this.statusParticipacao);
    }

    public boolean isAtivo() {
        return Boolean.TRUE.equals(this.ativo);
    }

    public boolean temResposta() {
        return this.dataResposta != null && !this.dataResposta.isEmpty();
    }
} 
