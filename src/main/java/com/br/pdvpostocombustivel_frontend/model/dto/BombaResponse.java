package com.br.pdvpostocombustivel_frontend.model.dto;

import java.math.BigDecimal;
import java.util.Date;

public record BombaResponse(
        Long id,
        String numeroBomba,
        Long idEstoque,
        String nomeProduto,
        Long idPreco,
        BigDecimal valorPreco,
        Date dataCriacao
) {}
