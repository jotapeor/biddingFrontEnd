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
import org.springframework.web.client.HttpClientErrorException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Controller
public class AuthController {

    @Autowired
    private ApiService restService;

    private String extrairMensagemDeErro(HttpClientErrorException e) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(e.getResponseBodyAsString());
            if (root.has("message")) {
                return root.get("message").asText();
            }
        } catch (Exception ex) {
        }
        return "Ocorreu um erro inesperado na comunicação.";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("credenciais", new UserRequestDTO());
        return "login";
    }

    @PostMapping("/logar")
    public String logar(@ModelAttribute("credenciais") UserRequestDTO credenciais, Model model, HttpSession session) {
        try {
            String token = restService.logar(credenciais);
            session.setAttribute("token", token);
            String role = restService.extrairRole(token);
            session.setAttribute("role", role);
            return "redirect:/editais";
        } catch (HttpClientErrorException e) {
            String msg = extrairMensagemDeErro(e);
            model.addAttribute("errorMessage", msg);
            model.addAttribute("credenciais", credenciais);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/registrar")
    public String registrar(Model model) {
        model.addAttribute("user", new UserDTO());
        return "registrar";
    }

    @PostMapping("/registrar")
    public String registrar(@ModelAttribute("user") UserDTO user, Model model) {
        try {
            restService.registrar(user);
            return "redirect:/login";
        } catch (HttpClientErrorException e) {
            String msg = extrairMensagemDeErro(e);
            model.addAttribute("errorMessage", msg);
            model.addAttribute("user", user);
            return "registrar";
        }
    }
}