package com.exemplo.bancoalcatteia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "membros_time")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembroTime {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuarios usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id", nullable = false)
    private Time time;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_membro", nullable = false)
    private TipoMembro tipoMembro = TipoMembro.MEMBRO;
    
    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada = LocalDateTime.now();
    
    @Column(name = "data_saida")
    private LocalDateTime dataSaida;
    
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;
    
    @Column(name = "observacoes")
    private String observacoes;
    
    public enum TipoMembro {
        LIDER("Líder"),
        VICE_LIDER("Vice-líder"), 
        MEMBRO("Membro"),
        COLABORADOR("Colaborador");
        
        private final String descricao;
        
        TipoMembro(String descricao) {
            this.descricao = descricao;
        }
        
        public String getDescricao() {
            return descricao;
        }
    }
    
    /**
     * Método para sair do time
     */
    public void sairDoTime() {
        this.ativo = false;
        this.dataSaida = LocalDateTime.now();
    }
    
    /**
     * Verificar se é líder
     */
    public boolean isLider() {
        return this.tipoMembro == TipoMembro.LIDER;
    }
    
    /**
     * Verificar se está ativo no time
     */
    public boolean isAtivoNoTime() {
        return this.ativo && this.dataSaida == null;
    }
} 