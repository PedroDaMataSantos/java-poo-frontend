package com.br.pdvpostocombustivel_frontend.service;

import com.br.pdvpostocombustivel_frontend.model.dto.ProdutoRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.ProdutoResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class ProdutoService {
    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080/api/v1/produtos";

    public ProdutoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ProdutoResponse> listarProdutos() {
        ResponseEntity<List<ProdutoResponse>> response = restTemplate.exchange(
                BASE_URL, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<ProdutoResponse>>() {});
        return response.getBody();
    }

    public void salvarProduto(ProdutoRequest request, Long id) {
        if (id == null) {
            restTemplate.postForEntity(BASE_URL, request, ProdutoResponse.class);
        } else {
            restTemplate.put(BASE_URL + "/" + id, request);
        }
    }

    public void excluirProduto(Long id) {
        restTemplate.delete(BASE_URL + "/" + id);
    }
}
