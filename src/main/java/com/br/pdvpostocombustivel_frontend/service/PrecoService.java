package com.br.pdvpostocombustivel_frontend.service;

import com.br.pdvpostocombustivel_frontend.model.dto.PrecoRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.PrecoResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class PrecoService {
    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080/api/v1/precos";

    public PrecoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PrecoResponse> listarPrecos() {
        ResponseEntity<List<PrecoResponse>> response = restTemplate.exchange(
                BASE_URL, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<PrecoResponse>>() {});
        return response.getBody();
    }

    public void salvarPreco(PrecoRequest request, Long id) {
        if (id == null) {
            restTemplate.postForEntity(BASE_URL, request, PrecoResponse.class);
        } else {
            restTemplate.put(BASE_URL + "/" + id, request);
        }
    }

    public void excluirPreco(Long id) {
        restTemplate.delete(BASE_URL + "/" + id);
    }
}