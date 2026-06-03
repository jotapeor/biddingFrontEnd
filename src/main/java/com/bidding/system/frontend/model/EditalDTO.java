package com.bidding.system.frontend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EditalDTO {

    private Long id;
    private String titulo;
    private String descricao;
    @JsonProperty("data_fechamento")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dataFechamento;
    private String status;

    public EditalDTO() {
    }

    public EditalDTO(Long id, String titulo, String descricao, LocalDateTime dataFechamento, String status) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataFechamento = dataFechamento;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDataFechamentoFormatada() {
        if (dataFechamento == null) return "-";
        return dataFechamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}