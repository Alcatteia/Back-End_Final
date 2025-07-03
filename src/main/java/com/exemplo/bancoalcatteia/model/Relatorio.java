package com.exemplo.bancoalcatteia.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "relatorios")
public class Relatorio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String titulo;

    @Column(length = 50)
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "gerado_por_id")
    private Usuarios geradoPor;

    @ManyToOne
    @JoinColumn(name = "time_id")
    private Time time;

    @Column(name = "data_geracao")
    private LocalDateTime dataGeracao;

    @Column(name = "periodo_inicio")
    private LocalDate periodoInicio;

    @Column(name = "periodo_fim")
    private LocalDate periodoFim;

    @Enumerated(EnumType.STRING)
    private Formato formato;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(columnDefinition = "LONGTEXT")
    private String conteudo;

    public enum Formato {
        HTML, PDF, JSON, CSV
    }

    public enum Status {
        GERANDO, CONCLUIDO, ERRO
    }
}
