package com.exemplo.bancoalcatteia.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckHumorDTO {
    private Integer id;
    
    @NotNull(message = "ID do usuário é obrigatório")
    private Integer usuarioId;
    
    private String nomeUsuario; // Para exibição (quando não anônimo)
    
    // Novos campos para sistema avançado
    private String sexoUsuario;
    
    @Min(value = 1, message = "Opção de humor deve ser entre 1 e 5")
    @Max(value = 5, message = "Opção de humor deve ser entre 1 e 5")
    private Integer opcaoHumor;
    
    private String dataRegistro;
    private LocalDate dataRegistroDate; // Para uso interno
    
    @NotBlank(message = "Humor é obrigatório")
    @Pattern(regexp = "CONTENTE|MOTIVADO|CALMO|NEUTRO|DESMOTIVADO|ESTRESSADO", 
             message = "Humor deve ser: CONTENTE, MOTIVADO, CALMO, NEUTRO, DESMOTIVADO ou ESTRESSADO")
    private String humor;
    
    private String humorDescricao; // Descrição amigável do humor com emoji
    
    // Novos campos para bem-estar
    private Integer bemEstarPontos;
    private Boolean confirmado;
    private Boolean podeResponder;
    private String tempoRestante;
    private String mensagemMenu;
    
    @Size(max = 1000, message = "Comentário deve ter no máximo 1000 caracteres")
    private String observacao; // Comentário
    
    @Builder.Default
    private Boolean anonimo = false;
    
    private LocalDateTime dataCriacao;
    
    // Método para verificar se tem comentário
    public boolean temComentario() {
        return observacao != null && !observacao.trim().isEmpty();
    }
    
    // Método para obter nome para exibição
    public String getNomeParaExibicao() {
        return (anonimo != null && anonimo) ? "Anônimo" : nomeUsuario;
    }
    
    // Métodos de conveniência para conversão entre formatos
    public void setDataRegistroFromDate(LocalDate data) {
        this.dataRegistroDate = data;
        this.dataRegistro = data != null ? data.toString() : null;
    }
    
    public LocalDate getDataRegistroAsDate() {
        if (dataRegistroDate != null) {
            return dataRegistroDate;
        }
        if (dataRegistro != null && !dataRegistro.isEmpty()) {
            return LocalDate.parse(dataRegistro);
        }
        return null;
    }
} 
