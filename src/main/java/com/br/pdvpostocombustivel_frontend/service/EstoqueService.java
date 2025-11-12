package com.br.pdvpostocombustivel_frontend.service;

import com.br.pdvpostocombustivel_frontend.model.dto.EstoqueRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.EstoqueResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class EstoqueService {

    @Value("${api.backend.url:http://localhost:8080/api/v1/estoques}")
    private String BASE_URL;

    private final RestTemplate restTemplate;

    public EstoqueService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<EstoqueResponse> listAll() {
        try {
            EstoqueResponse[] lista = restTemplate.getForObject(BASE_URL, EstoqueResponse[].class);
            return lista != null ? Arrays.asList(lista) : Collections.emptyList();
        } catch (RestClientException e) {
            System.err.println("Erro ao listar estoques: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public EstoqueResponse getById(Long id) {
        try {
            return restTemplate.getForObject(BASE_URL + "/" + id, EstoqueResponse.class);
        } catch (RestClientException e) {
            System.err.println("Erro ao buscar estoque: " + e.getMessage());
            return null;
        }
    }

    public EstoqueResponse create(EstoqueRequest request) {
        try {
            return restTemplate.postForObject(BASE_URL, request, EstoqueResponse.class);
        } catch (RestClientException e) {
            System.err.println("Erro ao criar estoque: " + e.getMessage());
            return null;
        }
    }

    public EstoqueResponse update(Long id, EstoqueRequest request) {
        try {
            restTemplate.put(BASE_URL + "/" + id, request);
            return getById(id);
        } catch (RestClientException e) {
            System.err.println("Erro ao atualizar estoque: " + e.getMessage());
            return null;
        }
    }

    public void delete(Long id) {
        try {
            restTemplate.delete(BASE_URL + "/" + id);
        } catch (RestClientException e) {
            System.err.println("Erro ao excluir estoque: " + e.getMessage());
        }
    }
}
