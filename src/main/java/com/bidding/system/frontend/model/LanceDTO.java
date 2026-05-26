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
// Classe de transferência de dados (Data Transfer Object) para lances
// DTO é usado para transferir dados entre camadas da aplicação (Controller -> Service -> Repository)
public class LanceDTO {
    // Identificador único do lance no banco de dados
    private Long id;
    // Valor/preço oferecido no lance
    private double valor;
    // Data e hora em que o lance foi realizado
    private Date data_lance;
    // Identificador do edital ao qual este lance está associado
    private long idEdital;
    // Identificador do usuário/fornecedor que fez este lance
    private long idUsuario;

    // Construtor vazio (padrão do JavaBeans)
    public LanceDTO() {
    }

    // Construtor com todos os parâmetros
    // Permite criar um lance completamente inicializado em uma única chamada
    public LanceDTO(Long id, double valor, Date data_lance, long idEdital, long idUsuario) {
        this.id = id;
        this.valor = valor;
        this.data_lance = data_lance;
        this.idEdital = idEdital;
        this.idUsuario = idUsuario;
    }

    // Método getter - retorna o id do lance
    public Long getId() {
        return id;
    }

    // Método setter - define o id do lance
    public void setId(Long id) {
        this.id = id;
    }

    // Método getter - retorna o valor do lance
    public double getValor() {
        return valor;
    }

    // Método setter - define o valor do lance
    public void setValor(double valor) {
        this.valor = valor;
    }

    // Método getter - retorna a data do lance
    public Date getData_lance() {
        return data_lance;
    }

    // Método setter - define a data do lance
    public void setData_lance(Date data_lance) {
        this.data_lance = data_lance;
    }

    // Método getter - retorna o id do edital associado
    public long getIdEdital() {
        return idEdital;
    }

    // Método setter - define o id do edital associado
    public void setIdEdital(long idEdital) {
        this.idEdital = idEdital;
    }

    // Método getter - retorna o id do usuário que fez o lance
    public long getIdUsuario() {
        return idUsuario;
    }

    // Método setter - define o id do usuário que fez o lance
    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    
    
    
    
}
