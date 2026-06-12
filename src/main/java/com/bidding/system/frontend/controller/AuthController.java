package com.bidding.system.frontend.controller;

import com.bidding.system.frontend.model.UserDTO;
import com.bidding.system.frontend.model.UserRequestDTO;
import com.bidding.system.frontend.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import tools.jackson.databind.ObjectMapper;

@Controller
public class AuthController {

    @Autowired
    private ApiService restService;

    @GetMapping("/")
    public String home(HttpSession session) {
        // Verifica se já existe um token salvo na sessão HTTP (indica usuário logado)
        if (session.getAttribute("token") != null) {
            return "redirect:/editais";
        }
        return "home";
    }

    @GetMapping("/login")
    public String login(Model model) {
        // Preserva dados caso um POST anterior tenha falhado e redirecionado de volta
        if (!model.containsAttribute("credenciais")) {
            model.addAttribute("credenciais", new UserRequestDTO());
        }
        return "login";
    }

    @PostMapping("/logar")
    public String logar(@Valid @ModelAttribute("credenciais") UserRequestDTO credenciais, BindingResult result, RedirectAttributes redirectAttributes, HttpSession session) {
        if (result.hasErrors()) {
            return "login"; // Retorna à página caso a validação do formulário falhe
        }

        try {
            // Chama a API do backend; em caso de falha de login lança HttpStatusCodeException
            String token = restService.logar(credenciais);

            // Armazena na sessão
            session.setAttribute("token", token);
            session.setAttribute("role", restService.extrairRole(token));
            session.setAttribute("email", credenciais.getEmail());

            String nome = restService.extrairNome(token);
            session.setAttribute("nome", nome != null ? nome : credenciais.getEmail().split("@")[0]);

            return "redirect:/editais";

        } catch (HttpStatusCodeException ex) {
            // Extrai a mensagem de erro da API (se houver) e exibe via flash attribute
            try {
                String mensagem = new ObjectMapper()
                        .readTree(ex.getResponseBodyAsString())
                        .get("message").asText();
                redirectAttributes.addFlashAttribute("errorMessage", mensagem);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ocorreu um erro inesperado na comunicação.");
            }
            redirectAttributes.addFlashAttribute("credenciais", credenciais);
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Invalida a sessão, removendo o token e outras informações
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/registrar")
    public String registrar(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDTO());
        }
        return "registrar";
    }

    @PostMapping("/registrar")
    public String registrar(@Valid @ModelAttribute("user") UserDTO user, BindingResult result, RedirectAttributes redirectAttributes) {
        // Verifica duplicidade de nome chamando a API
        if (!result.hasFieldErrors("nome") && restService.verificarNome(user.getNome())) {
            result.rejectValue("nome", "error.user", "Este nome já está em uso.");
        }

        // Verifica duplicidade de e-mail chamando a API
        if (!result.hasFieldErrors("email") && restService.verificarEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Este e-mail já está em uso.");
        }

        // Verifica se a senha e confirmação batem
        if (user.getSenha() != null && user.getConfirmarSenha() != null && !user.getSenha().equals(user.getConfirmarSenha())) {
            result.rejectValue("confirmarSenha", "error.user", "As senhas não coincidem.");
        }

        if (result.hasErrors()) {
            return "registrar"; // Mostra os erros na página de registro
        }

        try {
            // Delega a requisição POST para a API
            restService.registrar(user);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Cadastro realizado com sucesso! Faça o login.");
            return "redirect:/login";

        } catch (HttpStatusCodeException ex) {
            // Se o backend retornar erro (ex: validação do lado do servidor)
            try {
                String mensagem = new ObjectMapper()
                        .readTree(ex.getResponseBodyAsString())
                        .get("message").asText();
                redirectAttributes.addFlashAttribute("errorMessage", mensagem);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ocorreu um erro inesperado na comunicação.");
            }
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/registrar";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/registrar";
        }
    }
}