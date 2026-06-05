package com.bidding.system.frontend.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

// Data Transfer Object (DTO) que encapsula as informações de um lance individual.
// Utilizado para receber dados do backend e mapear submissões de lance no frontend.
public class LanceDTO {

    private Long id; // Identificador único do lance
    private double valor; // Valor monetário ofertado no lance

    // Data e hora em que o lance foi registrado. O JsonFormat cuida do parse na integração com a API.
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime data_lance;

    private Long id_edital; // Referência (chave estrangeira) para o edital ao qual este lance pertence
    private Long id_usuario; // Referência para o identificador do usuário (fornecedor) que fez a oferta

    public LanceDTO() {
    }

    public LanceDTO(Long id, double valor, LocalDateTime data_lance, Long id_edital, Long id_usuario) {
        this.id = id;
        this.valor = valor;
        this.data_lance = data_lance;
        this.id_edital = id_edital;
        this.id_usuario = id_usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public LocalDateTime getData_lance() {
        return data_lance;
    }

    public void setData_lance(LocalDateTime data_lance) {
        this.data_lance = data_lance;
    }

    public Long getId_edital() {
        return id_edital;
    }

    public void setId_edital(Long id_edital) {
        this.id_edital = id_edital;
    }

    public Long getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Long id_usuario) {
        this.id_usuario = id_usuario;
    }
}