package com.br.pdvpostocombustivel_frontend.service;

import com.br.pdvpostocombustivel_frontend.model.dto.PrecoRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.PrecoResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class PrecoService {

    private static final String BASE_URL = "http://localhost:8080/api/v1/precos";
    private final RestTemplate restTemplate;

    public PrecoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 🔹 Lista todos os preços
    public List<PrecoResponse> listAll() {
        try {
            ResponseEntity<PrecoResponse[]> response =
                    restTemplate.getForEntity(BASE_URL, PrecoResponse[].class);

            PrecoResponse[] precos = response.getBody();
            return precos != null ? Arrays.asList(precos) : List.of();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar preços: " + e.getMessage());
        }
    }

    // 🔹 Cria novo preço
    public void create(PrecoRequest request) {
        try {
            restTemplate.postForObject(BASE_URL, request, Void.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar preço: " + e.getMessage());
        }
    }

    // 🔹 Atualiza preço existente
    public void update(Long id, PrecoRequest request) {
        try {
            restTemplate.put(BASE_URL + "/" + id, request);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar preço ID " + id + ": " + e.getMessage());
        }
    }

    // 🔹 Exclui preço
    public void delete(Long id) {
        try {
            restTemplate.delete(BASE_URL + "/" + id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir preço ID " + id + ": " + e.getMessage());
        }
    }

    // 🔹 Busca o preço mais recente de um produto
    public BigDecimal buscarPrecoAtual(Long idProduto) {
        try {
            String url = BASE_URL + "/atual/" + idProduto;
            ResponseEntity<PrecoResponse> response =
                    restTemplate.getForEntity(url, PrecoResponse.class);

            PrecoResponse preco = response.getBody();
            if (preco == null || preco.valor() == null) {
                return BigDecimal.ZERO; // se não houver preço, retorna 0
            }
            return preco.valor();

        } catch (Exception e) {
            System.err.println("⚠ Erro ao buscar preço atual: " + e.getMessage());
            return BigDecimal.ZERO; // evita crash da tela de bomba
        }
    }
}
