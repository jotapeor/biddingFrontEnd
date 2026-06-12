package com.bidding.system.frontend.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserRequestDTO {

    /**
     * Correio exigido pelo campo do form.
     */
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Por favor, informe um e-mail válido")
    private String email;
    /**
     * Senha digitada a conferir.
     */
    @NotBlank(message = "A senha é obrigatória")
    private String senha;

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