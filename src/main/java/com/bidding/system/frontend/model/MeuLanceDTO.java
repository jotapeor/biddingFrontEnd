package com.bidding.system.frontend.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// DTO específico para a funcionalidade "Meus Lances", agregando informações
// sobre o lance do usuário e informações sumarizadas do edital (como título e status).
// Isso evita a necessidade de múltiplas chamadas à API no backend.
public class MeuLanceDTO {
    
    private Long idLance; // Identificador interno do lance no banco
    private double valor; // Valor do lance submetido

    // Data e hora em que o lance foi efetuado.
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataLance;

    private Long idEdital; // Identificador do edital respectivo
    private String tituloEdital; // Título do edital para fácil exibição na tabela
    private String statusEdital; // Status do edital (ex: ABERTO, FECHADO) na hora da consulta
    private boolean vencedor; // Flag booleana indicando se este lance é considerado o vencedor atual/final do certame

    public MeuLanceDTO() {
    }

    public Long getIdLance() {
        return idLance;
    }

    public void setIdLance(Long idLance) {
        this.idLance = idLance;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public LocalDateTime getDataLance() {
        return dataLance;
    }

    public void setDataLance(LocalDateTime dataLance) {
        this.dataLance = dataLance;
    }

    public Long getIdEdital() {
        return idEdital;
    }

    public void setIdEdital(Long idEdital) {
        this.idEdital = idEdital;
    }

    public String getTituloEdital() {
        return tituloEdital;
    }

    public void setTituloEdital(String tituloEdital) {
        this.tituloEdital = tituloEdital;
    }

    public String getStatusEdital() {
        return statusEdital;
    }

    public void setStatusEdital(String statusEdital) {
        this.statusEdital = statusEdital;
    }

    public boolean isVencedor() {
        return vencedor;
    }

    public void setVencedor(boolean vencedor) {
        this.vencedor = vencedor;
    }

    // Formata a data do lance para o padrão "dd/MM/yyyy HH:mm:ss" a ser apresentado na view.
    // Retorna String contendo a data formatada, ou "-" se ausente.
    public String getDataLanceFormatada() {
        if (dataLance == null) return "-";
        return dataLance.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}
