package com.br.pdvpostocombustivel_frontend.service;

import com.br.pdvpostocombustivel_frontend.model.dto.ContatoRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.ContatoResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class ContatoService {
    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080/api/contatos";

    public ContatoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ContatoResponse> listarContatos() {
        ResponseEntity<List<ContatoResponse>> response = restTemplate.exchange(
                BASE_URL, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<ContatoResponse>>() {
                });
        return response.getBody();
    }

    public void salvarContato(ContatoRequest request, Long id) {
        if (id == null) {
            restTemplate.postForEntity(BASE_URL, request, ContatoResponse.class);
        } else {
            restTemplate.put(BASE_URL + "/" + id, request);
        }
    }

    public void excluirContato(Long id) {
        restTemplate.delete(BASE_URL + "/" + id);
    }
}