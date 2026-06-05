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

// Controlador responsável pelas operações de autenticação e registro de usuários.
// Gerencia endpoints para login, logout, registro e validações via AJAX (verificar-email/nome).
@Controller
public class AuthController {

    // Injeção de dependência do serviço responsável por fazer chamadas à API backend.
    // O Spring injeta automaticamente o bean ApiService registrado com @Service.
    @Autowired
    private ApiService restService;

    // Endpoint raiz da aplicação.
    // Verifica se o usuário já possui um token na sessão. Se possuir, redireciona para a lista de editais.
    // Caso contrário, exibe a página inicial (home).
    @GetMapping("/")
    public String home(HttpSession session) {
        // Verifica se já existe um token salvo na sessão HTTP (indica usuário logado)
        if (session.getAttribute("token") != null) {
            // "redirect:" instrui o Spring MVC a emitir um HTTP 302 para o navegador
            return "redirect:/editais";
        }
        // Retorna o nome lógico da view; o Thymeleaf resolve para templates/home.html
        return "home";
    }

    // Endpoint da API interna (geralmente chamado via AJAX) para checar se um e-mail já está em uso.
    @GetMapping("/api/verificar-email")
    @ResponseBody  // Indica que o retorno deve ser serializado diretamente no corpo da resposta HTTP (não resolve view)
    public ResponseEntity<Boolean> verificarEmail(@RequestParam String email) {
        try {
            // Delega a verificação ao ApiService que consulta a API backend
            return ResponseEntity.ok(restService.verificarEmail(email));
        } catch (Exception e) {
            // Em caso de erro na comunicação, retorna false para não bloquear o formulário
            return ResponseEntity.ok(false);
        }
    }

    // Endpoint da API interna (via AJAX) para verificar se o nome de usuário (ou login) já está em uso.
    @GetMapping("/api/verificar-nome")
    @ResponseBody
    public ResponseEntity<Boolean> verificarNome(@RequestParam String nome) {
        try {
            return ResponseEntity.ok(restService.verificarNome(nome));
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    // Exibe a página de login.
    // Se não existir o atributo "credenciais" no modelo, inicializa um objeto vazio para o formulário.
    @GetMapping("/login")
    public String login(Model model) {
        // Verifica se o model já tem "credenciais" — isso ocorre quando um POST de login
        // falha e o controller redireciona de volta com o objeto preenchido via RedirectAttributes.
        // Evita sobrescrever os dados que o usuário já digitou.
        if (!model.containsAttribute("credenciais")) {
            // Adiciona um DTO vazio para que o th:object do formulário Thymeleaf encontre o objeto esperado
            model.addAttribute("credenciais", new UserRequestDTO());
        }
        return "login";
    }

    // Processa a submissão do formulário de login.
    // Envia as credenciais para o backend via ApiService e, em caso de sucesso, armazena
    // informações cruciais na sessão (token JWT, role, email e nome).
    @PostMapping("/logar")
    public String logar(@ModelAttribute("credenciais") UserRequestDTO credenciais, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            // Chama o backend para autenticar e recebe o token JWT como string pura
            String token = restService.logar(credenciais);

            // Armazena o token JWT na sessão para ser reutilizado nas próximas requisições autenticadas
            session.setAttribute("token", token);

            // Decodifica o payload do JWT para extrair a role sem precisar chamar o backend novamente
            String role = restService.extrairRole(token);
            session.setAttribute("role", role);

            // Salva o email na sessão para exibição ou auditoria futura
            session.setAttribute("email", credenciais.getEmail());

            // Extrai o nome do usuário do token; se não disponível, usa a parte local do e-mail (antes do @) como fallback
            String nome = restService.extrairNome(token);
            session.setAttribute("nome", nome != null ? nome : credenciais.getEmail().split("@")[0]);

            return "redirect:/editais";

        } catch (HttpStatusCodeException ex) {
            // Captura erros HTTP do backend (ex: 401 Unauthorized, 400 Bad Request)
            try {
                // O backend retorna um JSON com campo "message" descrevendo o erro
                // ObjectMapper.readTree() faz o parse do JSON para acessar o campo específico
                String mensagem = new ObjectMapper()
                        .readTree(ex.getResponseBodyAsString())  // Converte o corpo da resposta em JsonNode
                        .get("message").asText();                // Extrai o valor do campo "message" como String
                redirectAttributes.addFlashAttribute("errorMessage", mensagem);
            } catch (Exception e) {
                // Falha ao parsear o JSON de erro (resposta inesperada do backend)
                redirectAttributes.addFlashAttribute("errorMessage", "Ocorreu um erro inesperado na comunicação.");
            }
            // Reenvia o objeto credenciais para repopular o formulário no redirect
            redirectAttributes.addFlashAttribute("credenciais", credenciais);
            return "redirect:/login";

        } catch (Exception e) {
            // Captura erros genéricos (timeout, servidor indisponível, etc.)
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/login";
        }
    }

    // Realiza o logoff do usuário, invalidando sua sessão atual, o que destrói o token guardado.
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Invalida a sessão inteira, removendo token, role, email e nome armazenados
        // O Spring cria uma nova sessão vazia automaticamente na próxima requisição
        session.invalidate();
        return "redirect:/login";
    }

    // Exibe a página de registro de um novo usuário.
    // Inicializa o DTO "user" no modelo caso não exista, preparando o formulário.
    @GetMapping("/registrar")
    public String registrar(Model model) {
        // Mesma lógica do /login: preserva os dados já preenchidos se vier de um redirect com erro
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserDTO());
        }
        return "registrar";
    }

    // Processa o formulário de cadastro de novo usuário.
    // Envia os dados encapsulados em UserDTO ao backend.
    @PostMapping("/registrar")
    public String registrar(@ModelAttribute("user") UserDTO user, RedirectAttributes redirectAttributes) {
        try {
            // Delega a criação do usuário ao ApiService (que internamente força role = "FORNECEDOR")
            restService.registrar(user);

            // Flash attribute persiste apenas para o próximo redirect e depois é descartado automaticamente
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Cadastro realizado com sucesso! Faça o login.");
            return "redirect:/login";

        } catch (HttpStatusCodeException ex) {
            // Erro HTTP retornado pelo backend (ex: e-mail já cadastrado, validação falhou)
            try {
                String mensagem = new ObjectMapper()
                        .readTree(ex.getResponseBodyAsString())
                        .get("message").asText();
                redirectAttributes.addFlashAttribute("errorMessage", mensagem);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ocorreu um erro inesperado na comunicação.");
            }
            // Reenvia o objeto user para repopular o formulário (evita que o usuário precise redigitar tudo)
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/registrar";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/registrar";
        }
    }
}