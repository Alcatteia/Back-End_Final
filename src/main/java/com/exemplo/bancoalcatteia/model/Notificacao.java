package com.exemplo.bancoalcatteia.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_notificado_id", nullable = false)
    private Usuarios usuarioNotificado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_notificacao", nullable = false)
    private TipoNotificacao tipoNotificacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarefa_id")
    private Tarefa tarefa;

    @Column(name = "texto_notificacao", nullable = false, columnDefinition = "TEXT")
    private String textoNotificacao;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "confirmacao_leitura", nullable = false)
    private Boolean confirmacaoLeitura = false;

    public enum TipoNotificacao {
        TAREFA_ATRIBUIDA,
        PRAZO_CONCLUSAO,
        COMENTARIO,
        TAREFA_ATRASADA,
        ATUALIZACAO_SOLICITADA,
        TAREFA_CONCLUIDA,
        TAREFA_ACEITA,
        TAREFA_REJEITADA
    }

    @PrePersist
    protected void onCreate() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
        if (confirmacaoLeitura == null) {
            confirmacaoLeitura = false;
        }
    }

    public void marcarComoLida() {
        this.confirmacaoLeitura = true;
    }

    public boolean isLida() {
        return Boolean.TRUE.equals(this.confirmacaoLeitura);
    }

    public boolean isNaoLida() {
        return !isLida();
    }

    public static Notificacao criar(Usuarios usuario, TipoNotificacao tipo, String texto) {
        Notificacao notificacao = new Notificacao();
        notificacao.setUsuarioNotificado(usuario);
        notificacao.setTipoNotificacao(tipo);
        notificacao.setTextoNotificacao(texto);
        return notificacao;
    }

    public static Notificacao criarComTarefa(Usuarios usuario, TipoNotificacao tipo, String texto, Tarefa tarefa) {
        Notificacao notificacao = criar(usuario, tipo, texto);
        notificacao.setTarefa(tarefa);
        return notificacao;
    }
} 
