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
public class FeedbackDTO {

    private Integer id;

    @NotNull(message = "ID do usuário é obrigatório")
    private Long usuarioId;
    private String usuarioNome;

    @NotBlank(message = "Tipo do feedback é obrigatório")
    private String tipo;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
    private String titulo;

    @NotBlank(message = "Conteúdo é obrigatório")
    @Size(max = 2000, message = "Conteúdo deve ter no máximo 2000 caracteres")
    private String conteudo;

    private String status;

    private String prioridade;

    private Boolean anonimo;

    private String dataCriacao;
    private String dataResposta;

    private Long respondidoPorId;
    private String respondidoPorNome;

    @Size(max = 2000, message = "Resposta deve ter no máximo 2000 caracteres")
    private String resposta;

    private Boolean ativo;

    // Construtores de conveniência
    public FeedbackDTO(String tipo, String titulo, String conteudo) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.status = "PENDENTE";
        this.prioridade = "MEDIA";
        this.anonimo = false;
        this.ativo = true;
    }

    public FeedbackDTO(String tipo, String titulo, String conteudo, Boolean anonimo) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.anonimo = anonimo;
        this.status = "PENDENTE";
        this.prioridade = "MEDIA";
        this.ativo = true;
    }

    // Métodos auxiliares
    public boolean isPendente() {
        return "PENDENTE".equals(this.status);
    }

    public boolean isEmAnalise() {
        return "EM_ANALISE".equals(this.status);
    }

    public boolean isRespondido() {
        return "RESPONDIDO".equals(this.status);
    }

    public boolean isFechado() {
        return "FECHADO".equals(this.status);
    }

    public boolean isRejeitado() {
        return "REJEITADO".equals(this.status);
    }

    public boolean isAnonimo() {
        return Boolean.TRUE.equals(this.anonimo);
    }

    public boolean isAtivo() {
        return Boolean.TRUE.equals(this.ativo);
    }

    public boolean temResposta() {
        return this.resposta != null && !this.resposta.trim().isEmpty();
    }

    public boolean isTipoSugestao() {
        return "SUGESTAO".equals(this.tipo);
    }

    public boolean isTipoReclamacao() {
        return "RECLAMACAO".equals(this.tipo);
    }

    public boolean isTipoElogio() {
        return "ELOGIO".equals(this.tipo);
    }

    public boolean isTipoBugReport() {
        return "BUG_REPORT".equals(this.tipo);
    }

    public boolean isTipoMelhoria() {
        return "MELHORIA".equals(this.tipo);
    }

    public boolean isTipoDuvida() {
        return "DUVIDA".equals(this.tipo);
    }

    public boolean isPrioridadeCritica() {
        return "CRITICA".equals(this.prioridade);
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
