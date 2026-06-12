package com.bidding.system.frontend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class LanceDTO {

    private Long id;

    /**
     * Valor estipulado em Reais (R$) da proposta (tem que ser > 0.01).
     */
    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private Double valor;
    /**
     * Momento em que a oferta foi salva no sistema.
     * {@code @JsonFormat} assegura que venha do backend no formato serializado de String ISO8601 e mapeie de volta a objeto nativo de datas.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime data_lance;
    private Long id_edital;
    private Long id_usuario;
    private String nome_fornecedor;
    private boolean vencedor;

    public LanceDTO() {
    }

    public LanceDTO(Long id, Double valor, LocalDateTime data_lance, Long id_edital, Long id_usuario, String nome_fornecedor, boolean vencedor) {
        this.id = id;
        this.valor = valor;
        this.data_lance = data_lance;
        this.id_edital = id_edital;
        this.id_usuario = id_usuario;
        this.nome_fornecedor = nome_fornecedor;
        this.vencedor = vencedor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
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

    public String getNome_fornecedor() {
        return nome_fornecedor;
    }

    public void setNome_fornecedor(String nome_fornecedor) {
        this.nome_fornecedor = nome_fornecedor;
    }

    public boolean isVencedor() {
        return vencedor;
    }

    public void setVencedor(boolean vencedor) {
        this.vencedor = vencedor;
    }
}