package com.br.pdvpostocombustivel_frontend.service;

import com.br.pdvpostocombustivel_frontend.model.dto.BombaRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.BombaResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class BombaService {

    private final RestTemplate restTemplate;
    private final String API_BASE_URL = "http://localhost:8080/api/v1/bombas";

    public BombaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<BombaResponse> listarTodas() {
        try {
            BombaResponse[] bombas = restTemplate.getForObject(API_BASE_URL, BombaResponse[].class);
            return bombas != null ? Arrays.asList(bombas) : Collections.emptyList();
        } catch (RestClientException e) {
            System.err.println("Erro ao buscar bombas: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public BombaResponse buscarPorId(Long id) {
        try {
            return restTemplate.getForObject(API_BASE_URL + "/" + id, BombaResponse.class);
        } catch (RestClientException e) {
            System.err.println("Erro ao buscar bomba por ID: " + e.getMessage());
            return null;
        }
    }

    public BombaResponse criar(BombaRequest request) {
        try {
            return restTemplate.postForObject(API_BASE_URL, request, BombaResponse.class);
        } catch (RestClientException e) {
            System.err.println("Erro ao criar bomba: " + e.getMessage());
            return null;
        }
    }

    public void atualizar(Long id, BombaRequest request) {
        try {
            restTemplate.put(API_BASE_URL + "/" + id, request);
        } catch (RestClientException e) {
            System.err.println("Erro ao atualizar bomba: " + e.getMessage());
        }
    }

    public void excluir(Long id) {
        try {
            restTemplate.delete(API_BASE_URL + "/" + id);
        } catch (RestClientException e) {
            System.err.println("Erro ao excluir bomba: " + e.getMessage());
        }
    }
}
