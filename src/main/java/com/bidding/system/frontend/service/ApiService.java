package com.bidding.system.frontend.service;

import com.bidding.system.frontend.model.EditalDTO;
import com.bidding.system.frontend.model.UserDTO;
import com.bidding.system.frontend.model.UserRequestDTO;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ApiService {

    // RestClient é o cliente HTTP moderno do Spring que permite
    // construir requisições de forma fluente e declarativa.
    private final RestClient restClient;

    /**
     * Construtor padrão do serviço.
     * <p>
     * Aqui criamos o RestClient apenas uma vez e configuramos a URL base
     * comum para todas as requisições deste serviço.
     */
    public ApiService() {
        this.restClient = RestClient.builder()
                // Define a base URL que será usada em todas as requisições.
                // Depois, cada chamada só precisa informar o caminho relativo.
                .baseUrl("http://localhost:8080/api")
                .build();
    }

    /**
     * Envia as credenciais do usuário para o endpoint de login.
     *
     * @param user objeto DTO contendo email e senha
     * @return token ou resposta de autenticação como String
     */
    public String logar(UserRequestDTO user) {
        // Inicia a construção de uma requisição POST.
        return restClient.post()
                // Define o caminho relativo ao endpoint de autenticação.
                // A URL final será "http://localhost:3333/api/auth/logar".
                .uri("/autenticar/logar")
                // Define o corpo da requisição como o DTO de login.
                // O Spring converte automaticamente este objeto para JSON.
                .body(user)
                // Dispara a requisição e obtém a resposta do servidor.
                .retrieve()
                // Lê o corpo da resposta e converte para String.
                // Use outro DTO aqui se a API retornar um objeto JSON complexo.
                .body(String.class);
    }

    public void registrar(UserDTO user) {
        user.setRole("FORNECEDOR");
        String retorno = restClient
                .post()
                .uri("/autenticar/registrar")
                .body(user)
                .retrieve()
                .body(String.class);
    }

    /**
     * Lista os editais do backend usando o token JWT no cabeçalho Authorization.
     *
     * @param token token de autenticação recebido após o login
     * @return lista de editais retornada pela API
     */
    public List<EditalDTO> listarEditais(String token) {
        // Faz uma requisição GET para o endpoint de editais.
        EditalDTO[] editais = restClient.get()
                .uri("/editais")
                // Adiciona o header Authorization com o token Bearer.
                .header("Authorization", "Bearer " + token)
                .retrieve()
                // Converte o corpo JSON para um array de EditalDTO.
                .body(EditalDTO[].class);

        // Converte o array para List para uso mais conveniente na aplicação.
        return Arrays.asList(editais);
    }
}