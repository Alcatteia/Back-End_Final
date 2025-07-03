package com.exemplo.bancoalcatteia.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarefaDTO {

    private Integer id;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    private String titulo;

    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String descricao;

    private String status;

    private String prioridade;

    private Integer responsavelId;
    private String responsavelNome;

    private Long criadoPorId;
    private String criadoPorNome;

    private String dataCriacao;
    private String dataEntrega;
    private String dataConclusao;
    private String dataUltimaAtualizacao;

    private BigDecimal estimativaHoras;
    private BigDecimal horasTrabalhadas;

    private Boolean atrasada;
    private Boolean prazoVencido;

    private List<ParticipanteTarefaDTO> participantes;
    private Integer totalParticipantes;
    private Integer participantesAceitos;
    private Integer participantesPendentes;

    // Construtores de conveniência
    public TarefaDTO(String titulo, String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public TarefaDTO(String titulo, String descricao, String status, String prioridade) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.status = status;
        this.prioridade = prioridade;
    }

    // Métodos auxiliares
    public boolean isAtrasada() {
        return Boolean.TRUE.equals(this.atrasada);
    }

    public boolean isPrazoVencido() {
        return Boolean.TRUE.equals(this.prazoVencido);
    }

    public boolean isStatusTodo() {
        return "TODO".equals(this.status);
    }

    public boolean isStatusDoing() {
        return "DOING".equals(this.status);
    }

    public boolean isStatusDone() {
        return "DONE".equals(this.status);
    }

    public boolean isPrioridadeAlta() {
        return "ALTA".equals(this.prioridade);
    }

    public boolean isPrioridadeMedia() {
        return "MEDIA".equals(this.prioridade);
    }

    public boolean isPrioridadeBaixa() {
        return "BAIXA".equals(this.prioridade);
    }
} 
