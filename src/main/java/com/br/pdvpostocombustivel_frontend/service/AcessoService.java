package com.br.pdvpostocombustivel_frontend.service;

import com.br.pdvpostocombustivel_frontend.model.dto.AcessoRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.AcessoResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AcessoService {

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080/api/v1/acessos";

    public AcessoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<AcessoResponse> listarAcessos() {
        ResponseEntity<List<AcessoResponse>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<AcessoResponse>>() {}
        );
        return response.getBody();
    }

    public void salvarAcesso(AcessoRequest request, Long id) {
        if (id == null) {
            // POST - Criar novo
            restTemplate.postForEntity(BASE_URL, request, AcessoResponse.class);
        } else {
            // PUT - Atualizar existente
            restTemplate.put(BASE_URL + "/" + id, request);
        }
    }

    public void excluirAcesso(Long id) {
        restTemplate.delete(BASE_URL + "/" + id);
    }
}