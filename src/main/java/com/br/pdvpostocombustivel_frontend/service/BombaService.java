package com.br.pdvpostocombustivel_frontend.service;

import com.br.pdvpostocombustivel_frontend.model.dto.BombaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class BombaService {

    @Value("${api.backend.url:http://localhost:8080/api/v1/bombas}")
    private String API_BASE_URL;

    private final RestTemplate restTemplate;

    public BombaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Lista todas as bombas existentes no sistema.
     */
    public List<BombaResponse> listarTodas() {
        try {
            BombaResponse[] bombas = restTemplate.getForObject(API_BASE_URL, BombaResponse[].class);
            return bombas != null ? Arrays.asList(bombas) : Collections.emptyList();
        } catch (RestClientException e) {
            System.err.println("Erro ao buscar bombas: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Busca uma bomba específica pelo ID.
     */
    public BombaResponse buscarPorId(Long id) {
        try {
            return restTemplate.getForObject(API_BASE_URL + "/" + id, BombaResponse.class);
        } catch (RestClientException e) {
            System.err.println("Erro ao buscar bomba por ID: " + e.getMessage());
            return null;
        }
    }

    // Removidos os métodos criar/atualizar/excluir pois o backend não suporta essas operações.
}
