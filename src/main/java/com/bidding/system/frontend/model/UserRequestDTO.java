package com.bidding.system.frontend.model;

// Data Transfer Object minimalista utilizado estritamente para o processo de login (autenticação).
// Carrega somente as credenciais essenciais para requisitar um token.
public class UserRequestDTO {

    private String email; // E-mail de login fornecido no formulário
    private String senha; // Senha fornecida no formulário

    public UserRequestDTO() {
    }

    public UserRequestDTO(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

}