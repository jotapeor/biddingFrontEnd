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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tools.jackson.databind.ObjectMapper;

@Controller
public class AuthController {

    @Autowired
    private ApiService restService;

    @GetMapping("/")
    public String home(HttpSession session) {
        if (session.getAttribute("token") != null) {
            return "redirect:/editais";
        }
        return "home";
    }

    @GetMapping("/api/verificar-email")
    @ResponseBody
    public ResponseEntity<Boolean> verificarEmail(@RequestParam String email) {
        try {
            return ResponseEntity.ok(restService.verificarEmail(email));
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/api/verificar-nome")
    @ResponseBody
    public ResponseEntity<Boolean> verificarNome(@RequestParam String nome) {
        try {
            return ResponseEntity.ok(restService.verificarNome(nome));
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/login")
    public String login(Model model) {
        if (!model.containsAttribute("credenciais")) {
            model.addAttribute("credenciais", new UserRequestDTO());
        }
        return "login";
    }

    @PostMapping("/logar")
    public String logar(@ModelAttribute("credenciais") UserRequestDTO credenciais, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            String token = restService.logar(credenciais);
            session.setAttribute("token", token);
            String role = restService.extrairRole(token);
            session.setAttribute("role", role);
            session.setAttribute("email", credenciais.getEmail());
            String nome = restService.extrairNome(token);
            session.setAttribute("nome", nome != null ? nome : credenciais.getEmail().split("@")[0]);
            return "redirect:/editais";

        } catch (HttpStatusCodeException ex) {
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
    public String registrar(@ModelAttribute("user") UserDTO user, RedirectAttributes redirectAttributes) {
        try {
            restService.registrar(user);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Cadastro realizado com sucesso! Faça o login.");
            return "redirect:/login";

        } catch (HttpStatusCodeException ex) {
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