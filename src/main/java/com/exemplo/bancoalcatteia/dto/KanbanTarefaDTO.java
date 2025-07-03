package com.exemplo.bancoalcatteia.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KanbanTarefaDTO {
    private Integer id;
    
    @NotBlank(message = "Título da tarefa é obrigatório")
    @Size(max = 100, message = "Título deve ter no máximo 100 caracteres")
    private String titulo;
    
    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String descricao;
    
    @NotNull(message = "Categoria é obrigatória")
    private Integer categoriaId;
    private String categoriaNome; // Para exibição
    private String categoriaCor; // Para exibição
    
    private Integer responsavelId;
    private String responsavelNome; // Para exibição
    
    private Integer criadoPorId;
    private String criadoPorNome; // Para exibição
    
    private LocalDateTime dataCriacao;
    
    private LocalDate dataEntrega;
    private LocalDateTime dataConclusao;
    
    @Pattern(regexp = "BAIXA|MEDIA|ALTA", message = "Prioridade deve ser: BAIXA, MEDIA ou ALTA")
    private String prioridade;
    
    @Pattern(regexp = "ATIVA|PAUSADA|CONCLUIDA|CANCELADA", message = "Status deve ser: ATIVA, PAUSADA, CONCLUIDA ou CANCELADA")
    private String status;
    
    @DecimalMin(value = "0.0", message = "Estimativa de horas deve ser maior ou igual a 0")
    @Digits(integer = 3, fraction = 2, message = "Estimativa deve ter no máximo 3 dígitos inteiros e 2 decimais")
    private BigDecimal estimativaHoras;
    
    @DecimalMin(value = "0.0", message = "Horas trabalhadas deve ser maior ou igual a 0")
    @Digits(integer = 3, fraction = 2, message = "Horas trabalhadas deve ter no máximo 3 dígitos inteiros e 2 decimais")
    private BigDecimal horasTrabalhadas;
    
    @Min(value = 0, message = "Ordem deve ser maior ou igual a 0")
    private Integer ordem;
    
    // Campos calculados para o frontend
    private Boolean atrasada; // Se dataEntrega < hoje e status != CONCLUIDA
    private Long diasRestantes; // Até dataEntrega
    private BigDecimal porcentagemConclusao; // horasTrabalhadas / estimativaHoras * 100
    private String tempoDecorrido; // Tempo desde dataCriacao
} 
