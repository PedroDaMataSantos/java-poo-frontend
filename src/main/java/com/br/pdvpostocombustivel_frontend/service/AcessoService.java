package com.br.pdvpostocombustivel_frontend.service;

import com.br.pdvpostocombustivel_frontend.model.dto.AcessoRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.AcessoResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class AcessoService {

    private static final String BASE_URL = "http://localhost:8080/api/v1/acessos";
    private final RestTemplate restTemplate;

    public AcessoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<AcessoResponse> listAll() {
        AcessoResponse[] lista = restTemplate.getForObject(BASE_URL, AcessoResponse[].class);
        return Arrays.asList(lista);
    }

    public AcessoResponse registrar(AcessoRequest request) {
        return restTemplate.postForObject(BASE_URL + "/registrar", request, AcessoResponse.class);
    }

    public void delete(Long id) {
        restTemplate.delete(BASE_URL + "/" + id);
    }
}