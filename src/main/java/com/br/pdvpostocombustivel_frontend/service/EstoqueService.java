package com.br.pdvpostocombustivel_frontend.service;

import com.br.pdvpostocombustivel_frontend.model.dto.EstoqueRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.EstoqueResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class EstoqueService {
    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080/api/estoques";

    public EstoqueService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<EstoqueResponse> listarEstoque() {
        ResponseEntity<List<EstoqueResponse>> response = restTemplate.exchange(
                BASE_URL, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<EstoqueResponse>>() {});
        return response.getBody();
    }

    public void salvarEstoque(EstoqueRequest request, Long id) {
        if (id == null) {
            restTemplate.postForEntity(BASE_URL, request, EstoqueResponse.class);
        } else {
            restTemplate.put(BASE_URL + "/" + id, request);
        }
    }

    public void excluirEstoque(Long id) {
        restTemplate.delete(BASE_URL + "/" + id);
    }
}
