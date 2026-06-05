package com.bidding.system.frontend.model;

// Data Transfer Object que carrega os dados completos para registro de um novo usuário.
// Usado pelo formulário de cadastro na rota /registrar.
public class UserDTO {

    private Long id; // Identificador do usuário no banco
    private String nome; // Nome completo ou razão social informada no cadastro
    private String email; // E-mail usado para login e comunicação posterior
    private String senha; // Senha em texto plano provinda do formulário, a ser criptografada/armazenada no backend
    private String confirmarSenha; // Confirmação de senha (usada tipicamente para validação adicional de input no frontend)
    private String role; // Role de acesso, que para novos registros pelo frontend publico, será forçada para "FORNECEDOR"

    public UserDTO() {
    }

    public UserDTO(Long id, String nome, String email, String senha, String confirmarSenha, String role) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.confirmarSenha = confirmarSenha;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public String getConfirmarSenha() {
        return confirmarSenha;
    }

    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}