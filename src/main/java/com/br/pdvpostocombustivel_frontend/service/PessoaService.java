package com.br.pdvpostocombustivel_frontend.service;

import com.br.pdvpostocombustivel_frontend.model.PessoaPageResponse;
import com.br.pdvpostocombustivel_frontend.model.dto.PessoaRequest;
import com.br.pdvpostocombustivel_frontend.model.dto.PessoaResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class PessoaService {
    private final RestTemplate restTemplate;
    private final String API_BASE_URL = "http://localhost:8080/api/v1/pessoas";

    public PessoaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PessoaResponse> listarPessoas() {
        PessoaPageResponse pageResponse = restTemplate.getForObject(API_BASE_URL + "?size=100", PessoaPageResponse.class);
        return pageResponse.getContent();
    }

    public PessoaResponse salvarPessoa(PessoaRequest pessoaRequest, Long id) {
        if (id == null) {
            return restTemplate.postForObject(API_BASE_URL, pessoaRequest, PessoaResponse.class);
        } else {
            restTemplate.put(API_BASE_URL + "/" + id, pessoaRequest);
            return new PessoaResponse(id, pessoaRequest.nomeCompleto(), pessoaRequest.cpfCnpj(),
                    pessoaRequest.numeroCtps(), pessoaRequest.dataNascimento(), pessoaRequest.tipoPessoa());
        }
    }

    public void excluirPessoa(Long id) {
        restTemplate.delete(API_BASE_URL + "/" + id);
    }
}