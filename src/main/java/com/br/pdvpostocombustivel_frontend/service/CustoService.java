package com.br.pdvpostocombustivel_frontend.service;

import com.br.pdvpostocombustivel_frontend.model.dto.CustoRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.CustoResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class CustoService {
    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080/api/v1/custos";

    public CustoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<CustoResponse> listarCustos() {
        ResponseEntity<List<CustoResponse>> response = restTemplate.exchange(
                BASE_URL, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<CustoResponse>>() {});
        return response.getBody();
    }

    public void salvarCusto(CustoRequest request, Long id) {
        if (id == null) {
            restTemplate.postForEntity(BASE_URL, request, CustoResponse.class);
        } else {
            restTemplate.put(BASE_URL + "/" + id, request);
        }
    }

    public void excluirCusto(Long id) {
        restTemplate.delete(BASE_URL + "/" + id);
    }
}
