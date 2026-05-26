/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bidding.system.frontend.model;

/**
 *
 * @author Usuario
 */
// Classe de transferência de dados para requisições de login
// DTO é usado para receber apenas os dados necessários para autenticação (email e senha)
public class UserRequestDTO {
    // Email do usuário que está tentando fazer login
    private String email;
    // Senha do usuário para verificação de autenticação
    private String senha;

    // Construtor vazio (padrão do JavaBeans)
    public UserRequestDTO() {
    }

    // Construtor com email e senha
    // Permite criar uma requisição de login com os dados de autenticação
    public UserRequestDTO(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    // Método getter - retorna o email do usuário
    public String getEmail() {
        return email;
    }

    // Método setter - define o email do usuário
    public void setEmail(String email) {
        this.email = email;
    }

    // Método getter - retorna a senha do usuário
    public String getSenha() {
        return senha;
    }

    // Método setter - define a senha do usuário
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    
}
