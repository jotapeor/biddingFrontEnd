package com.bidding.system.frontend.controller;

import com.bidding.system.frontend.model.EditalDTO;
import com.bidding.system.frontend.model.LanceDTO;
import com.bidding.system.frontend.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class EditalController {

    @Autowired
    private ApiService restService;

    @GetMapping("/editais")
    public String editais(
            Model model,
            HttpSession session,
            @RequestParam(value = "urgente", required = false, defaultValue = "false") boolean urgente
    ) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login"; // Proteção de rota

        String role = (String) session.getAttribute("role");
        List<EditalDTO> editais = restService.listarEditais(token, urgente);

        // Calcula a flag "encerrando" para exibir o alerta visual na UI
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime limite48h = agora.plusHours(48);
        for (EditalDTO edital : editais) {
            boolean encerrando = edital.getDataFechamento() != null
                    && edital.getStatus() != null
                    && edital.getStatus().startsWith("ABERTO")
                    && edital.getDataFechamento().isAfter(agora)
                    && edital.getDataFechamento().isBefore(limite48h);
            edital.setEncerrando(encerrando);
        }

        model.addAttribute("editais", editais);
        model.addAttribute("urgente", urgente);
        model.addAttribute("role", role);

        return "editais";
    }

    @GetMapping("/editais/{id}")
    public String editalDetalhes(
            @PathVariable Long id,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        String role = (String) session.getAttribute("role");

        try {
            EditalDTO edital = restService.buscarEdital(id, token);
            model.addAttribute("edital", edital);
            model.addAttribute("role", role);

            // Injeta DTO de lance para o formulário de lances na própria view de detalhes
            if (!model.containsAttribute("lanceDTO")) {
                model.addAttribute("lanceDTO", new LanceDTO());
            }
            return "edital-detalhes";

        } catch (HttpStatusCodeException ex) {
            try {
                String mensagem = new ObjectMapper()
                        .readTree(ex.getResponseBodyAsString())
                        .get("message").asText();
                redirectAttributes.addFlashAttribute("errorMessage", "Edital não encontrado: " + mensagem);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Edital não encontrado.");
            }
            return "redirect:/editais";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao carregar o edital: " + e.getMessage());
            return "redirect:/editais";
        }
    }

    @PostMapping("/editais/{id}/lances")
    public String enviarLance(
            @PathVariable Long id,
            @Valid @ModelAttribute("lanceDTO") LanceDTO lance,
            BindingResult result,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.lanceDTO", result);
            redirectAttributes.addFlashAttribute("lanceDTO", lance);
            return "redirect:/editais/" + id;
        }

        try {
            restService.enviarLance(id, lance, token);
            redirectAttributes.addFlashAttribute("mensagemSucesso",
                    "Lance de R$ " + String.format("%.2f", lance.getValor()) + " enviado com sucesso!");

        } catch (HttpStatusCodeException ex) {
            String mensagem = extrairMensagemBackend(ex);
            redirectAttributes.addFlashAttribute("errorMessage",
                    mensagem != null ? mensagem : "Ocorreu um erro ao enviar o lance. Tente novamente em instantes.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado: " + e.getMessage());
        }

        return "redirect:/editais/" + id;
    }

    @GetMapping("/editais/{id}/lances")
    public String verLances(
            @PathVariable Long id,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        String role = (String) session.getAttribute("role");

        try {
            EditalDTO edital = restService.buscarEdital(id, token);
            List<LanceDTO> lances = restService.listarLances(id, token);

            model.addAttribute("edital", edital);
            model.addAttribute("lances", lances);
            model.addAttribute("role", role);
            return "lances";

        } catch (HttpStatusCodeException ex) {
            String mensagem = extrairMensagemBackend(ex);
            redirectAttributes.addFlashAttribute("errorMessage", mensagem != null ? mensagem : "Erro ao carregar lances.");
            return "redirect:/editais";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado ao carregar lances: " + e.getMessage());
            return "redirect:/editais";
        }
    }

    @GetMapping("/meus-lances")
    public String meusLances(
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        String role = (String) session.getAttribute("role");

        try {
            List<com.bidding.system.frontend.model.MeuLanceDTO> lances = restService.getMeusLances(token);

            model.addAttribute("lances", lances);
            model.addAttribute("role", role);
            return "meus-lances";

        } catch (HttpStatusCodeException ex) {
            String mensagem = extrairMensagemBackend(ex);
            redirectAttributes.addFlashAttribute("errorMessage", mensagem != null ? mensagem : "Erro ao carregar seus lances.");
            return "redirect:/editais";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado ao carregar lances: " + e.getMessage());
            return "redirect:/editais";
        }
    }

    @GetMapping("/novo-edital")
    public String novoEdital(Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        model.addAttribute("editalDTO", new EditalDTO());
        return "novo-edital";
    }

    @PostMapping("/novo-edital")
    public String novoEdital(@Valid @ModelAttribute("editalDTO") EditalDTO editalDTO, BindingResult result, HttpSession session, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "novo-edital";
        }

        try {
            String token = (String) session.getAttribute("token");
            restService.novoEdital(editalDTO, token);

            redirectAttributes.addFlashAttribute("mensagemSucesso", "Edital criado com sucesso!");
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
            return "redirect:/novo-edital";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/novo-edital";
        }
    }

    @PostMapping("/editais/{id}/atualizar")
    public String atualizarEdital(@PathVariable Long id, @Valid @ModelAttribute("editalDTO") EditalDTO edital, BindingResult result, HttpSession session, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editalDTO", result);
            redirectAttributes.addFlashAttribute("editalDTO", edital);
            return "redirect:/editais/" + id;
        }

        try {
            restService.atualizarEdital(id, edital, token);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Edital atualizado com sucesso!");
        } catch (HttpStatusCodeException ex) {
            String mensagem = extrairMensagemBackend(ex);
            redirectAttributes.addFlashAttribute("errorMessage", mensagem != null ? mensagem : "Erro ao atualizar edital.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado: " + e.getMessage());
        }

        return "redirect:/editais/" + id;
    }

    @PostMapping("/editais/{id}/deletar")
    public String deletarEdital(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        try {
            restService.deletarEdital(id, token);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Edital deletado com sucesso!");
            return "redirect:/editais";
        } catch (HttpStatusCodeException ex) {
            String mensagem = extrairMensagemBackend(ex);
            redirectAttributes.addFlashAttribute("errorMessage", mensagem != null ? mensagem : "Erro ao deletar edital.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado: " + e.getMessage());
        }
        return "redirect:/editais/" + id;
    }

    @PostMapping("/lances/{id}/deletar")
    public String deletarLance(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        try {
            restService.deletarLance(id, token);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Lance deletado com sucesso!");
        } catch (HttpStatusCodeException ex) {
            String mensagem = extrairMensagemBackend(ex);
            redirectAttributes.addFlashAttribute("errorMessage", mensagem != null ? mensagem : "Erro ao deletar lance.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado: " + e.getMessage());
        }
        return "redirect:/meus-lances"; // Volta para meus lances, já que o lance é do fornecedor
    }

    private String extrairMensagemBackend(HttpStatusCodeException ex) {
        try {
            return new ObjectMapper()
                    .readTree(ex.getResponseBodyAsString())
                    .get("message").asText();
        } catch (Exception e) {
            return null;
        }
    }
}