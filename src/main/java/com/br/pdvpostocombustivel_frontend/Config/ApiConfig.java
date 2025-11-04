package com.br.pdvpostocombustivel_frontend.Config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiConfig {

    @Value("${api.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final String API_VERSION = "/api/v1";

    // Getters para cada endpoint
    public String getBombasUrl() {
        return baseUrl + API_VERSION + "/bombas";
    }

    public String getProdutosUrl() {
        return baseUrl + API_VERSION + "/produtos";
    }

    public String getPrecosUrl() {
        return baseUrl + API_VERSION + "/precos";
    }

    public String getVendasUrl() {
        return baseUrl + API_VERSION + "/vendas";
    }

    public String getAcessosUrl() {
        return baseUrl + API_VERSION + "/acessos";
    }

    public String getContatosUrl() {
        return baseUrl + API_VERSION + "/contatos";
    }

    public String getEstoquesUrl() {
        return baseUrl + API_VERSION + "/estoques";
    }

    public String getCustosUrl() {
        return baseUrl + API_VERSION + "/custos";
    }
}