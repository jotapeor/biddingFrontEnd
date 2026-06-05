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

// Controlador principal que gerencia as operações relacionadas a editais e lances.
// Inclui listagem, visualização de detalhes, envio e visualização de lances e criação de editais.
@Controller
public class EditalController {

    // Serviço para integração com a API de backend.
    // O Spring injeta automaticamente o bean ApiService registrado com @Service.
    @Autowired
    private ApiService restService;

    // Lista todos os editais disponíveis no sistema.
    // Permite filtrar os editais pela flag de "urgente".
    // Requer que o usuário esteja autenticado (token na sessão).
    @GetMapping("/editais")
    public String editais(
            Model model,
            HttpSession session,
            // required=false: o parâmetro é opcional na URL; defaultValue="false" garante valor padrão sem lançar erro
            @RequestParam(value = "urgente", required = false, defaultValue = "false") boolean urgente
    ) {
        // Recupera o token salvo na sessão; cast explícito necessário pois getAttribute retorna Object
        String token = (String) session.getAttribute("token");

        // Guarda de segurança: redireciona para login se a sessão não tiver token (usuário não logado)
        if (token == null) return "redirect:/login";

        // Role usada na view para controlar a visibilidade de botões (ex: "Criar Edital" só aparece para ORGAO)
        String role = (String) session.getAttribute("role");

        // Busca a lista de editais no backend, aplicando o filtro "urgente" se necessário
        List<EditalDTO> editais = restService.listarEditais(token, urgente);

        // Adiciona os dados ao Model para que o template Thymeleaf possa iterá-los com th:each
        model.addAttribute("editais", editais);

        // Mantém o estado do filtro na view para que o checkbox/botão "urgente" reflita a seleção atual
        model.addAttribute("urgente", urgente);

        // Passa a role para a view controlar renderização condicional (ex: th:if="${role == 'ORGAO'}")
        model.addAttribute("role", role);

        return "editais";
    }

    // Exibe os detalhes de um edital específico baseado no seu ID.
    @GetMapping("/editais/{id}")
    public String editalDetalhes(
            @PathVariable Long id,  // Extrai o valor {id} diretamente do path da URL e converte para Long
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        String role = (String) session.getAttribute("role");

        try {
            // Busca os dados completos do edital pelo ID via API
            EditalDTO edital = restService.buscarEdital(id, token);

            // Injeta o edital no modelo para o Thymeleaf acessar seus campos (ex: th:text="${edital.titulo}")
            model.addAttribute("edital", edital);
            model.addAttribute("role", role);
            return "edital-detalhes";

        } catch (HttpStatusCodeException ex) {
            // Captura erros HTTP do backend (ex: 404 Not Found se o edital não existir)
            try {
                String mensagem = new ObjectMapper()
                        .readTree(ex.getResponseBodyAsString())  // Parse do JSON de erro do backend
                        .get("message").asText();                // Extrai o campo "message"
                redirectAttributes.addFlashAttribute("errorMessage", "Edital não encontrado: " + mensagem);
            } catch (Exception e) {
                // Fallback se o corpo da resposta não for JSON válido
                redirectAttributes.addFlashAttribute("errorMessage", "Edital não encontrado.");
            }
            return "redirect:/editais";

        } catch (Exception e) {
            // Outros erros inesperados (timeout, servidor indisponível, etc.)
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao carregar o edital: " + e.getMessage());
            return "redirect:/editais";
        }
    }

    // Recebe e processa o envio de um novo lance para um determinado edital.
    @PostMapping("/editais/{id}/lances")
    public String enviarLance(
            @PathVariable Long id,
            @RequestParam("valor") double valor,  // Valor enviado pelo campo "valor" do formulário HTML
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        try {
            // Cria um LanceDTO com apenas o valor, pois os demais campos (id_edital, id_usuario)
            // são preenchidos pelo backend com base no token e no path da URL
            LanceDTO lance = new LanceDTO();
            lance.setValor(valor);

            // Envia o lance para o backend; qualquer violação de regra de negócio lança HttpStatusCodeException
            restService.enviarLance(id, lance, token);

            // String.format("%.2f") formata o double com 2 casas decimais para exibição amigável
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Lance de R$ " +
                    String.format("%.2f", valor) + " enviado com sucesso!");

        } catch (HttpStatusCodeException ex) {
            // Captura erros HTTP de negócio retornados pelo backend
            String mensagem = extrairMensagemBackend(ex);

            // Mapeia substrings específicas da mensagem do backend para textos amigáveis ao usuário
            if (mensagem != null) {
                if (mensagem.toLowerCase().contains("fechado") || mensagem.toLowerCase().contains("closed")) {
                    // Edital com status FECHADO não aceita novos lances
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "⛔ Este edital está fechado e não aceita mais lances.");
                } else if (mensagem.toLowerCase().contains("data") || mensagem.toLowerCase().contains("prazo")) {
                    // Prazo de submissão ultrapassou a data de fechamento do edital
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "📅 Data inválida: o prazo para envio de lances foi encerrado.");
                } else if (mensagem.toLowerCase().contains("já existe") || mensagem.toLowerCase().contains("duplicate")
                        || mensagem.toLowerCase().contains("already")) {
                    // Constraint de unicidade: o mesmo fornecedor não pode ter dois lances no mesmo edital
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "⚠️ Você já enviou um lance para este edital.");
                } else {
                    // Erro de negócio genérico não mapeado acima
                    redirectAttributes.addFlashAttribute("errorMessage", "Erro ao enviar lance: " + mensagem);
                }
            } else {
                // Backend retornou erro mas sem corpo JSON legível; exibe o código HTTP
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Erro ao enviar lance. Código HTTP: " + ex.getStatusCode().value());
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado: " + e.getMessage());
        }

        // Sempre redireciona de volta para a página de detalhes do mesmo edital, independente de sucesso/falha
        return "redirect:/editais/" + id;
    }

    // Exibe todos os lances de um edital específico, geralmente acessado pelo perfil de ORGAO.
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
            // Busca os dados do edital para exibir informações contextuais na view (título, status, etc.)
            EditalDTO edital = restService.buscarEdital(id, token);

            // Busca a lista de todos os lances desse edital (endpoint só retorna dados para ORGAO no backend)
            List<LanceDTO> lances = restService.listarLances(id, token);

            model.addAttribute("edital", edital);
            model.addAttribute("lances", lances);
            model.addAttribute("role", role);

            return "lances";

        } catch (HttpStatusCodeException ex) {
            // Usa o helper para extrair a mensagem do JSON de erro; fallback para texto genérico
            String mensagem = extrairMensagemBackend(ex);
            redirectAttributes.addFlashAttribute("errorMessage", mensagem != null ? mensagem : "Erro ao carregar lances.");
            return "redirect:/editais";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado ao carregar lances: " + e.getMessage());
            return "redirect:/editais";
        }
    }

    // Página dedicada para fornecedores consultarem o histórico de todos os seus lances em diferentes editais.
    // Valida se a role do usuário logado é efetivamente "FORNECEDOR".
    @GetMapping("/meus-lances")
    public String meusLances(
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        String role = (String) session.getAttribute("role");

        // Proteção de acesso no nível do controller: verifica a role antes de chamar a API.
        // Mesmo que o backend bloqueie a requisição, esta verificação evita chamadas desnecessárias
        if (!"FORNECEDOR".equals(role)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Acesso negado: apenas fornecedores podem visualizar seus lances.");
            return "redirect:/editais";
        }

        try {
            // O backend identifica o usuário pelo token JWT e retorna apenas os lances desse fornecedor
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

    // Exibe o formulário de criação de um novo edital.
    // Inicia um DTO vazio para que o formulário no HTML consiga mapear (binding) os campos corretamente.
    @GetMapping("/novo-edital")
    public String novoEdital(Model model, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        // EditalDTO vazio é necessário para o th:object do formulário Thymeleaf ter um objeto para fazer binding
        // Sem isso, o th:field lançaria NullPointerException ao tentar acessar os campos
        model.addAttribute("editalDTO", new EditalDTO());
        return "novo-edital";
    }

    // Submete a requisição para criação de um novo edital enviando para a API do backend.
    @PostMapping("/novo-edital")
    public String novoEdital(@ModelAttribute("editalDTO") EditalDTO editalDTO, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            // Recupera o token para autorizar a criação; o backend valida que apenas ORGAO pode criar editais
            String token = (String) session.getAttribute("token");
            restService.novoEdital(editalDTO, token);

            redirectAttributes.addFlashAttribute("mensagemSucesso", "Edital criado com sucesso!");
            return "redirect:/editais";

        } catch (HttpStatusCodeException ex) {
            // Erros de validação ou negócio do backend (ex: campos inválidos, data no passado, etc.)
            try {
                String mensagem = new ObjectMapper()
                        .readTree(ex.getResponseBodyAsString())
                        .get("message").asText();
                redirectAttributes.addFlashAttribute("errorMessage", mensagem);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ocorreu um erro inesperado na comunicação.");
            }
            // Redireciona de volta ao formulário para o usuário corrigir os dados
            return "redirect:/novo-edital";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/novo-edital";
        }
    }

    // Método auxiliar privado para extrair a mensagem de erro formatada em JSON enviada pelo backend.
    // Tenta buscar o atributo "message" na string da exceção HTTP.
    private String extrairMensagemBackend(HttpStatusCodeException ex) {
        try {
            // getResponseBodyAsString() retorna o corpo bruto da resposta HTTP de erro como String
            // readTree() faz o parse para uma estrutura de árvore JSON (JsonNode)
            // .get("message") acessa o nó filho com a chave "message"
            // .asText() converte o valor do nó para String primitiva
            return new ObjectMapper()
                    .readTree(ex.getResponseBodyAsString())
                    .get("message").asText();
        } catch (Exception e) {
            // Corpo da resposta não é JSON ou não contém o campo "message"
            return null;
        }
    }
}