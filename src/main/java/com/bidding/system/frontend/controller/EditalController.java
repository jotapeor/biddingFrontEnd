package com.bidding.system.frontend.controller;

import com.bidding.system.frontend.model.EditalDTO;
import com.bidding.system.frontend.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Controller
public class EditalController {

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
        return "Erro ao processar a requisição do edital.";
    }

    @GetMapping("/editais")
    public String editais(
            Model model,
            HttpSession session,
            @RequestParam(value = "urgente", required = false, defaultValue = "false") boolean urgente
    ) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        String role = (String) session.getAttribute("role");

        List<EditalDTO> editais = restService.listarEditais(token, urgente);

        model.addAttribute("editais", editais);
        model.addAttribute("urgente", urgente);
        model.addAttribute("role", role);

        return "editais";
    }

    @GetMapping("/novo-edital")
    public String novoEdital(Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        model.addAttribute("editalDTO", new EditalDTO());
        return "novo-edital";
    }

    @PostMapping("/novo-edital")
    public String novoEdital(@ModelAttribute("editalDTO") EditalDTO editalDTO, HttpSession session, Model model) {
        try {
            String token = (String) session.getAttribute("token");
            restService.novoEdital(editalDTO, token);
            return "redirect:/editais";
        } catch (HttpClientErrorException e) {
            String msg = extrairMensagemDeErro(e);
            model.addAttribute("errorMessage", msg);
            model.addAttribute("editalDTO", editalDTO);
            return "novo-edital";
        }
    }
}