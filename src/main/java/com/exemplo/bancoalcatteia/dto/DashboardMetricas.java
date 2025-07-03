package com.exemplo.bancoalcatteia.dto;

/**
 * DTO para representar métricas do dashboard
 */
public class DashboardMetricas {
    
    // Métricas gerais
    private long totalTarefas;
    private long tarefasConcluidas;
    private long tarefasEmProgresso;
    private long tarefasPendentes;
    private long tarefasAtrasadas;
    
    // Métricas de time
    private long totalMembrosTime;
    private long membrosAtivos;
    private double taxaConclusao;
    private double produtividadeMedia;
    
    // Métricas de humor
    private double humorMedioTime;
    private long checkHumorsHoje;
    
    // Métricas de karaokê
    private long sessoesRealizadas;
    private long participacoesKaraoke;
    private double notaMediaKaraoke;
    
    // Construtores
    public DashboardMetricas() {}
    
    // Getters e Setters
    public long getTotalTarefas() {
        return totalTarefas;
    }
    
    public void setTotalTarefas(long totalTarefas) {
        this.totalTarefas = totalTarefas;
    }
    
    public long getTarefasConcluidas() {
        return tarefasConcluidas;
    }
    
    public void setTarefasConcluidas(long tarefasConcluidas) {
        this.tarefasConcluidas = tarefasConcluidas;
    }
    
    public long getTarefasEmProgresso() {
        return tarefasEmProgresso;
    }
    
    public void setTarefasEmProgresso(long tarefasEmProgresso) {
        this.tarefasEmProgresso = tarefasEmProgresso;
    }
    
    public long getTarefasPendentes() {
        return tarefasPendentes;
    }
    
    public void setTarefasPendentes(long tarefasPendentes) {
        this.tarefasPendentes = tarefasPendentes;
    }
    
    public long getTarefasAtrasadas() {
        return tarefasAtrasadas;
    }
    
    public void setTarefasAtrasadas(long tarefasAtrasadas) {
        this.tarefasAtrasadas = tarefasAtrasadas;
    }
    
    public long getTotalMembrosTime() {
        return totalMembrosTime;
    }
    
    public void setTotalMembrosTime(long totalMembrosTime) {
        this.totalMembrosTime = totalMembrosTime;
    }
    
    public long getMembrosAtivos() {
        return membrosAtivos;
    }
    
    public void setMembrosAtivos(long membrosAtivos) {
        this.membrosAtivos = membrosAtivos;
    }
    
    public double getTaxaConclusao() {
        return taxaConclusao;
    }
    
    public void setTaxaConclusao(double taxaConclusao) {
        this.taxaConclusao = taxaConclusao;
    }
    
    public double getProdutividadeMedia() {
        return produtividadeMedia;
    }
    
    public void setProdutividadeMedia(double produtividadeMedia) {
        this.produtividadeMedia = produtividadeMedia;
    }
    
    public double getHumorMedioTime() {
        return humorMedioTime;
    }
    
    public void setHumorMedioTime(double humorMedioTime) {
        this.humorMedioTime = humorMedioTime;
    }
    
    public long getCheckHumorsHoje() {
        return checkHumorsHoje;
    }
    
    public void setCheckHumorsHoje(long checkHumorsHoje) {
        this.checkHumorsHoje = checkHumorsHoje;
    }
    
    public long getSessoesRealizadas() {
        return sessoesRealizadas;
    }
    
    public void setSessoesRealizadas(long sessoesRealizadas) {
        this.sessoesRealizadas = sessoesRealizadas;
    }
    
    public long getParticipacoesKaraoke() {
        return participacoesKaraoke;
    }
    
    public void setParticipacoesKaraoke(long participacoesKaraoke) {
        this.participacoesKaraoke = participacoesKaraoke;
    }
    
    public double getNotaMediaKaraoke() {
        return notaMediaKaraoke;
    }
    
    public void setNotaMediaKaraoke(double notaMediaKaraoke) {
        this.notaMediaKaraoke = notaMediaKaraoke;
    }
} 
