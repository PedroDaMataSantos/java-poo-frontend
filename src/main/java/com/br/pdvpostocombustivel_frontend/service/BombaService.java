package com.br.pdvpostocombustivel_frontend.service;

import com.br.pdvpostocombustivel_frontend.model.dto.BombaRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.BombaResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class BombaService {

    private final RestTemplate restTemplate;
    private final String API_BASE_URL = "http://localhost:8080/api/v1/bombas";

    public BombaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<BombaResponse> listarTodas() {
        BombaResponse[] bombas = restTemplate.getForObject(API_BASE_URL, BombaResponse[].class);
        return bombas != null ? Arrays.asList(bombas) : List.of();
    }

    public BombaResponse buscarPorId(Long id) {
        return restTemplate.getForObject(API_BASE_URL + "/" + id, BombaResponse.class);
    }

    public BombaResponse criar(BombaRequest request) {
        return restTemplate.postForObject(API_BASE_URL, request, BombaResponse.class);
    }

    public void atualizar(Long id, BombaRequest request) {
        restTemplate.put(API_BASE_URL + "/" + id, request);
    }

    public void excluir(Long id) {
        restTemplate.delete(API_BASE_URL + "/" + id);
    }
}
