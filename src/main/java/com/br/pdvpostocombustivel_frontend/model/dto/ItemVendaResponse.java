package com.br.pdvpostocombustivel_frontend.model.dto;

import java.math.BigDecimal;

public record ItemVendaResponse(
        Long id,
        Long idProduto,
        BigDecimal quantidade,
        BigDecimal valorUnitario,
        BigDecimal subtotal
) {}
