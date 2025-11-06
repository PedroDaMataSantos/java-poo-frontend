package com.br.pdvpostocombustivel_frontend.service;

import com.br.pdvpostocombustivel_frontend.model.dto.CustoRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.CustoResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class CustoService {

    private static final String BASE_URL = "http://localhost:8080/api/v1/custos";
    private final RestTemplate restTemplate;

    public CustoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<CustoResponse> listAll() {
        CustoResponse[] lista = restTemplate.getForObject(BASE_URL, CustoResponse[].class);
        return Arrays.asList(lista);
    }

    public CustoResponse getById(Long id) {
        return restTemplate.getForObject(BASE_URL + "/" + id, CustoResponse.class);
    }

    public CustoResponse create(CustoRequest request) {
        return restTemplate.postForObject(BASE_URL, request, CustoResponse.class);
    }

    public CustoResponse update(Long id, CustoRequest request) {
        restTemplate.put(BASE_URL + "/" + id, request);
        return getById(id);
    }

    public void delete(Long id) {
        restTemplate.delete(BASE_URL + "/" + id);
    }
}