/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bidding.system.frontend.model;

import java.sql.Date;

/**
 *
 * @author Usuario
 */
// Classe de transferência de dados (Data Transfer Object) para editais
// DTO é usado para transferir dados entre camadas da aplicação (Controller -> Service -> Repository)
public class EditalDTO {
    // Identificador único do edital no banco de dados
    private Long id;
    // Título do edital (nome do processo de compra)
    private String titulo;
    // Descrição detalhada do edital
    private String descricao;
    // Data e hora de fechamento do edital (quando não mais aceita lances)
    private Date dataFechamento;
    // Status do edital ("ABERTO" = aceitando lances, "FECHADO" = não aceita mais lances)
    private String status;

    // Construtor vazio (padrão do JavaBeans)
    public EditalDTO() {
    }

    // Construtor com todos os parâmetros
    // Permite criar um edital completamente inicializado em uma única chamada
    public EditalDTO(Long id, String titulo, String descricao, Date dataFechamento, String status) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataFechamento = dataFechamento;
        this.status = status;
    }

    // Método getter - retorna o id do edital
    public Long getId() {
        return id;
    }

    // Método setter - define o id do edital
    public void setId(Long id) {
        this.id = id;
    }

    // Método getter - retorna o título do edital
    public String getTitulo() {
        return titulo;
    }

    // Método setter - define o título do edital
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // Método getter - retorna a descrição do edital
    public String getDescricao() {
        return descricao;
    }

    // Método setter - define a descrição do edital
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    // Método getter - retorna a data de fechamento do edital
    public Date getDataFechamento() {
        return dataFechamento;
    }

    // Método setter - define a data de fechamento do edital
    public void setDataFechamento(Date dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    // Método getter - retorna o status do edital
    public String getStatus() {
        return status;
    }

    // Método setter - define o status do edital
    public void setStatus(String status) {
        this.status = status;
    }
    
    
    
}
