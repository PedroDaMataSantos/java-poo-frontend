package com.br.pdvpostocombustivel_frontend.model.dto;

public record BombaRequest(
        String numeroBomba,
        Long idEstoque,
        Long idPreco
) {}
