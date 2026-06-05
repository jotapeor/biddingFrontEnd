package com.bidding.system.frontend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Data Transfer Object (DTO) para representar os dados de um Edital.
// Usado para serialização/deserialização na comunicação com a API backend e binding em formulários web.
public class EditalDTO {

    private Long id; // Identificador único do edital gerado no banco de dados
    private String titulo; // Título principal do edital
    private String descricao; // Descrição detalhada do escopo do edital
    
    // Data de encerramento de recebimento de lances.
    // Mapeado em JSON com a chave "data_fechamento" e em formulários Spring no formato "yyyy-MM-dd'T'HH:mm".
    @JsonProperty("data_fechamento")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dataFechamento;
    
    private String status; // Status do edital, que dita se ele ainda aceita lances ou já foi encerrado

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

    // Método auxiliar para apresentação na view.
    // Formata a data de fechamento para um padrão brasileiro de leitura amigável (dd/MM/yyyy HH:mm).
    public String getDataFechamentoFormatada() {
        if (dataFechamento == null) return "-";
        return dataFechamento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}