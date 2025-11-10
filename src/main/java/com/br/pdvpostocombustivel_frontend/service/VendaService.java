package com.br.pdvpostocombustivel_frontend.service;

import com.br.pdvpostocombustivel_frontend.model.dto.ItemVendaRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.VendaRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.VendaResponse;
import com.br.pdvpostocombustivel_frontend.model.enums.TipoVenda;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class VendaService {

    private static final String BASE_URL = "http://localhost:8080/api/v1/vendas";
    private final RestTemplate restTemplate;

    public VendaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public VendaResponse registrar(VendaRequest request) {
        return restTemplate.postForObject(BASE_URL, request, VendaResponse.class);
    }

    public VendaResponse registrarVenda(Long idProduto, BigDecimal litros, BigDecimal precoUnitario, BigDecimal total) {
        ItemVendaRequest item = new ItemVendaRequest(idProduto, litros, precoUnitario, total);

        VendaRequest venda = new VendaRequest(
                LocalDateTime.now(),
                TipoVenda.DINHEIRO, // padrão, ou substituído depois por seleção
                total,
                List.of(item)
        );

        return restTemplate.postForObject(BASE_URL, venda, VendaResponse.class);
    }

    public List<VendaResponse> listar() {
        VendaResponse[] lista = restTemplate.getForObject(BASE_URL, VendaResponse[].class);
        return Arrays.asList(lista);
    }

    public VendaResponse buscarPorId(Long id) {
        return restTemplate.getForObject(BASE_URL + "/" + id, VendaResponse.class);
    }

    public void deletar(Long id) {
        restTemplate.delete(BASE_URL + "/" + id);
    }
}
