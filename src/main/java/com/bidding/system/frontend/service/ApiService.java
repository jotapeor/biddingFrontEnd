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

    public boolean verificarEmail(String email) {
        try {
            return Boolean.TRUE.equals(restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/autenticar/verificar-email")
                            .queryParam("email", email)
                            .build())
                    .retrieve()
                    .body(Boolean.class));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verificarNome(String nome) {
        try {
            return Boolean.TRUE.equals(restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/autenticar/verificar-nome")
                            .queryParam("nome", nome)
                            .build())
                    .retrieve()
                    .body(Boolean.class));
        } catch (Exception e) {
            return false;
        }
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

    public String extrairNome(String token) {
        try {
            String[] partes = token.split("\\.");
            String payload = partes[1];
            int padding = (4 - payload.length() % 4) % 4;
            payload = payload + "=".repeat(padding);
            String json = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);

            for (String key : new String[]{"\"nome\":\"", "\"name\":\""}) {
                int start = json.indexOf(key);
                if (start != -1) {
                    start += key.length();
                    int end = json.indexOf("\"", start);
                    return json.substring(start, end);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void registrar(UserDTO user) {
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

    public EditalDTO buscarEdital(Long id, String token) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/editais/{id}")
                        .build(id))
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(EditalDTO.class);
    }

    public void enviarLance(Long idEdital, LanceDTO lance, String token) {
        restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/editais/{id}/lances")
                        .build(idEdital))
                .header("Authorization", "Bearer " + token)
                .body(lance)
                .retrieve()
                .body(String.class);
    }

    public List<LanceDTO> listarLances(Long idEdital, String token) {
        LanceDTO[] lances = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/editais/{id}/lances")
                        .build(idEdital))
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(LanceDTO[].class);

        return Arrays.asList(lances);
    }

    public List<com.bidding.system.frontend.model.MeuLanceDTO> getMeusLances(String token) {
        com.bidding.system.frontend.model.MeuLanceDTO[] lances = restClient.get()
                .uri("/lances/meus-lances")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(com.bidding.system.frontend.model.MeuLanceDTO[].class);

        return Arrays.asList(lances);
    }

    public void atualizarEdital(Long id, EditalDTO edital, String token) {
        restClient.put()
                .uri(uriBuilder -> uriBuilder.path("/editais/{id}").build(id))
                .header("Authorization", "Bearer " + token)
                .body(edital)
                .retrieve()
                .body(String.class);
    }

    public void deletarEdital(Long id, String token) {
        restClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/editais/{id}").build(id))
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(String.class);
    }

    public void deletarLance(Long id, String token) {
        restClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/lances/{id}").build(id))
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(String.class);
    }
}