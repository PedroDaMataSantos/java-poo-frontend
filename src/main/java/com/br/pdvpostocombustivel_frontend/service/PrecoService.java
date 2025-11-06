package com.br.pdvpostocombustivel_frontend.service;



import com.br.pdvpostocombustivel_frontend.model.dto.PrecoRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.PrecoResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class PrecoService {

    private static final String BASE_URL = "http://localhost:8080/api/v1/precos";
    private final RestTemplate restTemplate;

    public PrecoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PrecoResponse> listAll() {
        PrecoResponse[] lista = restTemplate.getForObject(BASE_URL, PrecoResponse[].class);
        return Arrays.asList(lista);
    }

    public PrecoResponse getById(Long id) {
        return restTemplate.getForObject(BASE_URL + "/" + id, PrecoResponse.class);
    }

    public PrecoResponse create(PrecoRequest request) {
        return restTemplate.postForObject(BASE_URL, request, PrecoResponse.class);
    }

    public PrecoResponse update(Long id, PrecoRequest request) {
        restTemplate.put(BASE_URL + "/" + id, request);
        return getById(id);
    }

    public void delete(Long id) {
        restTemplate.delete(BASE_URL + "/" + id);
    }
}