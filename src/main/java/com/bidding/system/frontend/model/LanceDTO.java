package com.bidding.system.frontend.model;

import java.sql.Date;

public class LanceDTO {

    private Long id;
    private double valor;
    private Date data_lance;
    private long idEdital;
    private long idUsuario;

    public LanceDTO() {
    }

    public LanceDTO(Long id, double valor, Date data_lance, long idEdital, long idUsuario) {
        this.id = id;
        this.valor = valor;
        this.data_lance = data_lance;
        this.idEdital = idEdital;
        this.idUsuario = idUsuario;
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

    public Date getData_lance() {
        return data_lance;
    }

    public void setData_lance(Date data_lance) {
        this.data_lance = data_lance;
    }

    public long getIdEdital() {
        return idEdital;
    }

    public void setIdEdital(long idEdital) {
        this.idEdital = idEdital;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
    }

}