package com.bidding.system.frontend.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDTO {

    private Long id;
    /**
     * Designação primária do sujeito.
     */
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 8, message = "O nome deve ter pelo menos 8 caracteres")
    private String nome;
    /**
     * Localizador de correio digital exigido na triagem.
     */
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Por favor, informe um e-mail válido")
    private String email;
    /**
     * Senha em texto pleno oriunda do frontend. A API cuidará de efetuar Hash.
     */
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres")
    private String senha;
    /**
     * Checagem tipográfica extra da senha. Verificada se coincide no Controller e na API.
     */
    @NotBlank(message = "A confirmação de senha é obrigatória")
    private String confirmarSenha;
    private String role;

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