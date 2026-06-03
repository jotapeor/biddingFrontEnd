package com.bidding.system.frontend.service;

import com.bidding.system.frontend.model.EditalDTO;
import com.bidding.system.frontend.model.UserDTO;
import com.bidding.system.frontend.model.UserRequestDTO;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ApiService {

    private final RestClient restClient;

    public ApiService() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:8080/api")
                .build();
    }

    public String logar(UserRequestDTO user) {
        return restClient.post()
                .uri("/autenticar/logar")
                .body(user)
                .retrieve()
                .body(String.class);
    }

    public String extrairRole(String token) {
        try {
            String[] partes = token.split("\\.");
            String payload = partes[1];
            int padding = (4 - payload.length() % 4) % 4;
            payload = payload + "=".repeat(padding);
            String json = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
            String roleKey = "\"role\":\"";
            int start = json.indexOf(roleKey);
            if (start == -1) return null;
            start += roleKey.length();
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        } catch (Exception e) {
            return null;
        }
    }

    public void registrar(UserDTO user) {
        user.setRole("FORNECEDOR");
        restClient.post()
                .uri("/autenticar/registrar")
                .body(user)
                .retrieve()
                .body(String.class);
    }

    public List<EditalDTO> listarEditais(String token, boolean urgente) {
        EditalDTO[] editais = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/editais")
                        .queryParam("urgente", urgente)
                        .build())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(EditalDTO[].class);

        return Arrays.asList(editais);
    }

    public void novoEdital(EditalDTO edital, String token) {
        restClient.post()
                .uri("/editais/criar")
                .header("Authorization", "Bearer " + token)
                .body(edital)
                .retrieve()
                .body(String.class);
    }
}