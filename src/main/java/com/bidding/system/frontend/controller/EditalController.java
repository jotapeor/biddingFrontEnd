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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Controller
public class EditalController {

    @Autowired
    private ApiService restService;

    // ── Lista de editais ────────────────────────────────────────────────────────

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

    // ── Detalhes do edital ─────────────────────────────────────────────────────

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

    // ── Envio de lance ─────────────────────────────────────────────────────────

    @PostMapping("/editais/{id}/lances")
    public String enviarLance(
            @PathVariable Long id,
            @RequestParam("valor") double valor,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        try {
            LanceDTO lance = new LanceDTO();
            lance.setValor(valor);

            restService.enviarLance(id, lance, token);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Lance de R$ " +
                    String.format("%.2f", valor) + " enviado com sucesso!");

        } catch (HttpStatusCodeException ex) {
            String mensagem = extrairMensagemBackend(ex);

            // Mapeia erros de negócio para mensagens amigáveis
            if (mensagem != null) {
                if (mensagem.toLowerCase().contains("fechado") || mensagem.toLowerCase().contains("closed")) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "⛔ Este edital está fechado e não aceita mais lances.");
                } else if (mensagem.toLowerCase().contains("data") || mensagem.toLowerCase().contains("prazo")) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "📅 Data inválida: o prazo para envio de lances foi encerrado.");
                } else if (mensagem.toLowerCase().contains("já existe") || mensagem.toLowerCase().contains("duplicate")
                        || mensagem.toLowerCase().contains("already")) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "⚠️ Você já enviou um lance para este edital.");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Erro ao enviar lance: " + mensagem);
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Erro ao enviar lance. Código HTTP: " + ex.getStatusCode().value());
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado: " + e.getMessage());
        }

        return "redirect:/editais/" + id;
    }

    // ── Visualização de lances ─────────────────────────────────────────────────

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

    // ── Visualização dos Próprios Lances (Para FORNECEDOR) ─────────────────────

    @GetMapping("/meus-lances")
    public String meusLances(
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        String role = (String) session.getAttribute("role");
        if (!"FORNECEDOR".equals(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado: apenas fornecedores podem visualizar seus lances.");
            return "redirect:/editais";
        }

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

    // ── Criar novo edital ──────────────────────────────────────────────────────

    @GetMapping("/novo-edital")
    public String novoEdital(Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        model.addAttribute("editalDTO", new EditalDTO());
        return "novo-edital";
    }

    @PostMapping("/novo-edital")
    public String novoEdital(@ModelAttribute("editalDTO") EditalDTO editalDTO, HttpSession session, RedirectAttributes redirectAttributes) {
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

    // ── Utilidade ──────────────────────────────────────────────────────────────

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