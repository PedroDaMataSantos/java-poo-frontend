package com.br.pdvpostocombustivel_frontend.service;

import com.br.pdvpostocombustivel_frontend.model.dto.PrecoRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.PrecoResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class PrecoService {

    private static final String BASE_URL = "http://localhost:8080/api/v1/precos";
    private final RestTemplate restTemplate;

    public PrecoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 🔹 Listar todos os preços
    public List<PrecoResponse> listAll() {
        try {
            PrecoResponse[] response = restTemplate.getForObject(BASE_URL, PrecoResponse[].class);
            return Arrays.asList(response != null ? response : new PrecoResponse[0]);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar preços: " + e.getMessage());
        }
    }

    // 🔹 Criar novo preço
    public void create(PrecoRequest request) {
        try {
            restTemplate.postForObject(BASE_URL, request, Void.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar preço: " + e.getMessage());
        }
    }

    // 🔹 Atualizar preço existente
    public void update(Long id, PrecoRequest request) {
        try {
            restTemplate.put(BASE_URL + "/" + id, request);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar preço ID " + id + ": " + e.getMessage());
        }
    }

    // 🔹 Excluir preço
    public void delete(Long id) {
        try {
            restTemplate.delete(BASE_URL + "/" + id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir preço ID " + id + ": " + e.getMessage());
        }
    }

    // 🔹 Buscar o preço mais recente de um produto específico
    public BigDecimal buscarPrecoAtual(Long idProduto) {
        try {
            String url = BASE_URL + "/atual/" + idProduto;
            Map<?, ?> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.get("valor") != null) {
                return new BigDecimal(response.get("valor").toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar preço do produto ID " + idProduto + ": " + e.getMessage());
        }

        throw new RuntimeException("Preço não encontrado para o produto ID " + idProduto);
    }
}
