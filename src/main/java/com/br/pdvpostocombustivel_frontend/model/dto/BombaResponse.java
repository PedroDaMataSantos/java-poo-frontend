package com.br.pdvpostocombustivel_frontend.model.dto;

import java.util.Date;

public record BombaResponse(
        Long id,
        String numeroBomba,
        Long idEstoque,
        String nomeProduto,
        Long idPreco,
        Date dataCriacao
) {}
