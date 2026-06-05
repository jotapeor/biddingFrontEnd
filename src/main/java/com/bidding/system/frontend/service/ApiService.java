package com.bidding.system.frontend.service;

import com.bidding.system.frontend.model.EditalDTO;
import com.bidding.system.frontend.model.LanceDTO;
import com.bidding.system.frontend.model.UserDTO;
import com.bidding.system.frontend.model.UserRequestDTO;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

// Serviço responsável por encapsular todas as chamadas HTTP para a API REST do backend.
// Utiliza o RestClient do Spring (disponível em versões recentes) para comunicação.
@Service
public class ApiService {

    // Cliente HTTP síncrono configurado para interagir com o backend.
    // É final porque não deve ser reatribuído após a inicialização no construtor.
    private final RestClient restClient;

    // Construtor padrão que inicializa o RestClient configurando a URL base da API.
    public ApiService() {
        this.restClient = RestClient.builder()
                // Define o prefixo de URL que será concatenado automaticamente a todos os .uri() posteriores.
                // Exemplo: .uri("/editais") resulta em "http://localhost:8080/api/editais"
                .baseUrl("http://localhost:8080/api")
                // Finaliza a configuração do builder e cria a instância imutável do RestClient.
                .build();
    }

    // Envia credenciais do usuário para o endpoint de login e recupera o token JWT.
    // Retorna String representando o token JWT retornado pelo backend.
    public String logar(UserRequestDTO user) {
        return restClient.post()           // Inicia uma requisição HTTP POST
                .uri("/autenticar/logar")  // Caminho relativo ao baseUrl; resulta em POST /api/autenticar/logar
                .body(user)               // Serializa o objeto UserRequestDTO para JSON no corpo da requisição
                .retrieve()              // Executa a requisição e prepara o manuseio da resposta
                .body(String.class);     // Desserializa o corpo da resposta para String (o token JWT vem como texto puro)
    }

    // Verifica junto ao backend se o e-mail informado já está cadastrado.
    // Retorna true se o e-mail existe, false se não existe ou em caso de erro na comunicação.
    public boolean verificarEmail(String email) {
        try {
            return Boolean.TRUE.equals(restClient.get()   // Inicia requisição HTTP GET
                    .uri(uriBuilder -> uriBuilder         // Lambda que recebe um UriBuilder para montar a URL programaticamente
                            .path("/autenticar/verificar-email")  // Define o caminho do endpoint
                            .queryParam("email", email)           // Adiciona ?email=<valor> na query string
                            .build())                             // Constrói o URI final: /autenticar/verificar-email?email=...
                    .retrieve()
                    .body(Boolean.class));  // Espera um boolean JSON (true/false) como resposta
        } catch (Exception e) {
            // Em caso de falha de comunicação, assume que o e-mail não existe para evitar bloquear a UI
            return false;
        }
    }

    // Verifica junto ao backend se o nome de usuário informado já está em uso.
    // Retorna true se o nome já está em uso, false caso contrário ou em erro.
    public boolean verificarNome(String nome) {
        try {
            return Boolean.TRUE.equals(restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/autenticar/verificar-nome")
                            .queryParam("nome", nome)  // Adiciona ?nome=<valor> na query string
                            .build())
                    .retrieve()
                    .body(Boolean.class));
        } catch (Exception e) {
            return false;
        }
    }

    // Extrai de forma manual a 'role' (perfil) decodificando o payload base64 de um token JWT.
    // Retorna a role do usuário (ex: "FORNECEDOR", "ORGAO") extraída do payload, ou null se não encontrar.
    public String extrairRole(String token) {
        try {
            // Um JWT é composto por 3 partes separadas por '.': header.payload.signature
            String[] partes = token.split("\\.");

            // A parte [1] é o payload, que contém os claims (dados) do token em Base64URL
            String payload = partes[1];

            // O Base64 padrão exige que o comprimento seja múltiplo de 4.
            // Base64URL omite o '=' de padding, então é necessário recalcular e adicionar manualmente.
            int padding = (4 - payload.length() % 4) % 4;
            payload = payload + "=".repeat(padding);

            // Decodifica o payload de Base64URL para bytes e converte para String JSON (UTF-8)
            String json = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);

            // Define a chave que identifica o campo "role" no JSON do payload
            String roleKey = "\"role\":\"";

            // Localiza a posição inicial da chave no JSON
            int start = json.indexOf(roleKey);
            if (start == -1) return null; // Campo "role" não existe no token

            // Avança o índice para o início do valor (após a chave e as aspas de abertura)
            start += roleKey.length();

            // Encontra o fechamento das aspas do valor para delimitar a substring
            int end = json.indexOf("\"", start);

            // Retorna o valor da role entre as aspas
            return json.substring(start, end);
        } catch (Exception e) {
            // Token malformado, nulo ou payload inesperado
            return null;
        }
    }

    // Decodifica o payload do token JWT para extrair o nome do usuário.
    // Tenta buscar as chaves "nome" ou "name" dentro do JSON do token.
    // Retorna o nome do usuário, ou null caso não localize.
    public String extrairNome(String token) {
        try {
            // Mesmo processo de decodificação do payload JWT descrito em extrairRole()
            String[] partes = token.split("\\.");
            String payload = partes[1];
            int padding = (4 - payload.length() % 4) % 4;
            payload = payload + "=".repeat(padding);
            String json = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);

            // Tenta encontrar o nome sob duas possíveis chaves: "nome" (pt-BR) ou "name" (en)
            // Isso garante compatibilidade com backends que usam convenções de nomenclatura distintas
            for (String key : new String[]{"\"nome\":\"", "\"name\":\""}) {
                int start = json.indexOf(key);
                if (start != -1) {
                    start += key.length();       // Avança para o início do valor
                    int end = json.indexOf("\"", start); // Encontra o fim do valor
                    return json.substring(start, end);
                }
            }
            return null; // Nenhuma das chaves foi encontrada no payload
        } catch (Exception e) {
            return null;
        }
    }

    // Envia o payload contendo os dados de um novo usuário para o endpoint de registro.
    // Força a atribuição da role "FORNECEDOR" antes de enviar ao backend.
    public void registrar(UserDTO user) {
        // Garante que todo usuário criado pelo frontend público seja do tipo FORNECEDOR,
        // independente do que o formulário possa ter enviado
        user.setRole("FORNECEDOR");

        restClient.post()
                .uri("/autenticar/registrar")  // Endpoint de cadastro de novo usuário
                .body(user)                    // Envia o UserDTO serializado como JSON
                .retrieve()
                .body(String.class);           // Resposta descartada; qualquer erro HTTP é propagado como exceção
    }

    // Obtém a lista de editais disponíveis no sistema.
    // Retorna uma lista tipada de objetos EditalDTO contendo as informações de cada edital.
    public List<EditalDTO> listarEditais(String token, boolean urgente) {
        // A API retorna um array JSON, por isso o tipo alvo é EditalDTO[] (array)
        EditalDTO[] editais = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/editais")
                        .queryParam("urgente", urgente)  // Passa o filtro como query param: ?urgente=true/false
                        .build())
                // Adiciona o header de Authorization com o token JWT no padrão Bearer
                // Necessário porque o endpoint /editais é protegido por autenticação no backend
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(EditalDTO[].class);  // Desserializa o array JSON em array de EditalDTO

        // Converte o array para List para facilitar o uso no controller e na view (Thymeleaf)
        return Arrays.asList(editais);
    }

    // Cria um novo edital submetendo as informações capturadas no frontend.
    public void novoEdital(EditalDTO edital, String token) {
        restClient.post()
                .uri("/editais/criar")                    // Endpoint específico de criação de edital
                .header("Authorization", "Bearer " + token) // Token obrigatório; o backend valida se o usuário tem role ORGAO
                .body(edital)                             // EditalDTO serializado como JSON no corpo
                .retrieve()
                .body(String.class);                      // Resposta descartada; exceções HTTP são propagadas ao controller
    }

    // Busca os dados completos de um único edital utilizando seu ID.
    // Retorna um EditalDTO com os detalhes retornados pela API.
    public EditalDTO buscarEdital(Long id, String token) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/editais/{id}")  // Template de URI com variável {id}
                        .build(id))            // Substitui {id} pelo valor do parâmetro Long id
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(EditalDTO.class);  // Desserializa o JSON de resposta diretamente em um EditalDTO
    }

    // Envia uma oferta (lance) para um determinado edital.
    public void enviarLance(Long idEdital, LanceDTO lance, String token) {
        restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/editais/{id}/lances")  // Endpoint de submissão de lance com ID do edital na URL
                        .build(idEdital))              // Resolve a variável {id} com o ID do edital
                .header("Authorization", "Bearer " + token)  // Backend usa o token para identificar o fornecedor
                .body(lance)     // Corpo da requisição com os dados do lance (principalmente o valor)
                .retrieve()
                .body(String.class);  // Resposta descartada; erros de negócio (edital fechado, duplicado, etc.) chegam como HttpStatusCodeException
    }

    // Recupera a lista de lances já registrados para um edital específico.
    // Retorna a Lista de lances (LanceDTO) ordenados ou processados pelo backend.
    public List<LanceDTO> listarLances(Long idEdital, String token) {
        LanceDTO[] lances = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/editais/{id}/lances")  // Mesmo path de envio, mas com GET retorna a lista de lances existentes
                        .build(idEdital))
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(LanceDTO[].class);  // Array de LanceDTO correspondente ao array JSON retornado

        return Arrays.asList(lances);  // Converte para List imutável
    }

    // Busca todos os lances de um fornecedor logado para que ele veja seu próprio histórico.
    // Retorna uma lista de MeuLanceDTO, DTO específico que agrupa dados do lance e do edital relacionado.
    public List<com.bidding.system.frontend.model.MeuLanceDTO> getMeusLances(String token) {
        // Usa o path completo do import pois MeuLanceDTO não foi importado no topo da classe
        com.bidding.system.frontend.model.MeuLanceDTO[] lances = restClient.get()
                // Endpoint exclusivo para retornar apenas os lances do usuário autenticado (identificado pelo token)
                .uri("/lances/meus-lances")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(com.bidding.system.frontend.model.MeuLanceDTO[].class);

        return Arrays.asList(lances);
    }
}