package com.br.pdvpostocombustivel_frontend.model.dto;

import com.br.pdvpostocombustivel_frontend.model.enums.TipoVenda;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VendaResponse(
        Long id,
        TipoVenda tipoVenda,
        BigDecimal valorTotal,
        LocalDateTime dataHora,
        List<ItemVendaResponse> itens
) {}
