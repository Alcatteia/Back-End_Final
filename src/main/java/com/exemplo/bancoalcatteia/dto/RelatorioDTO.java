package com.exemplo.bancoalcatteia.dto;

import lombok.Data;

@Data
public class RelatorioDTO {
    private Integer id;
    private String tipo;
    private Long geradoPorId;
    private Long timeId;
    private String dataGeracao;
    private String conteudo;
} 
