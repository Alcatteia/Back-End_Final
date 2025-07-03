package com.exemplo.bancoalcatteia.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KanbanCategoriaDTO {
    private Integer id;
    
    @NotBlank(message = "Nome da categoria é obrigatório")
    @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
    private String nome;
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String descricao;
    
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Cor deve ser um código hexadecimal válido")
    private String cor;
    
    @Min(value = 0, message = "Ordem deve ser maior ou igual a 0")
    private Integer ordem;
    
    private Integer usuarioId;
    private String usuarioNome; // Para exibição
    
    private Integer timeId;
    private String timeNome; // Para exibição
    
    private LocalDateTime dataCriacao;
    
    @Builder.Default
    private Boolean ativo = true;
    
    // Estatísticas para o frontend
    private Long totalTarefas;
    private Long tarefasAtivas;
    private Long tarefasConcluidas;
} 
