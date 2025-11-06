package com.br.pdvpostocombustivel_frontend.service;

import com.br.pdvpostocombustivel_frontend.model.dto.ContatoRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.ContatoResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ContatoService {

    private static final String BASE_URL = "http://localhost:8080/api/v1/contatos";
    private final RestTemplate restTemplate;

    public ContatoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ContatoResponse> listAll() {
        ContatoResponse[] lista = restTemplate.getForObject(BASE_URL, ContatoResponse[].class);
        return Arrays.asList(lista);
    }

    public ContatoResponse getById(Long id) {
        return restTemplate.getForObject(BASE_URL + "/" + id, ContatoResponse.class);
    }

    public ContatoResponse create(ContatoRequest request) {
        return restTemplate.postForObject(BASE_URL, request, ContatoResponse.class);
    }

    public ContatoResponse update(Long id, ContatoRequest request) {
        restTemplate.put(BASE_URL + "/" + id, request);
        return getById(id);
    }

    public void delete(Long id) {
        restTemplate.delete(BASE_URL + "/" + id);
    }
}