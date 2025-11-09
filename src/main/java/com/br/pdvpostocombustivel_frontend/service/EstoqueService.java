package com.br.pdvpostocombustivel_frontend.service;

import com.br.pdvpostocombustivel_frontend.model.dto.EstoqueRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.EstoqueResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class EstoqueService {

    private static final String BASE_URL = "http://localhost:8080/api/v1/estoques";

    // INJEÇÃO CORRETA
    private final RestTemplate restTemplate;

    // CONSTRUTOR COM INJEÇÃO AUTOMÁTICA
    public EstoqueService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<EstoqueResponse> listAll() {
        EstoqueResponse[] lista = restTemplate.getForObject(BASE_URL, EstoqueResponse[].class);
        return Arrays.asList(lista);
    }

    public EstoqueResponse getById(Long id) {
        return restTemplate.getForObject(BASE_URL + "/" + id, EstoqueResponse.class);
    }

    public EstoqueResponse create(EstoqueRequest request) {
        return restTemplate.postForObject(BASE_URL, request, EstoqueResponse.class);
    }

    public EstoqueResponse update(Long id, EstoqueRequest request) {
        restTemplate.put(BASE_URL + "/" + id, request);
        return getById(id);
    }

    public void delete(Long id) {
        restTemplate.delete(BASE_URL + "/" + id);
    }
}
