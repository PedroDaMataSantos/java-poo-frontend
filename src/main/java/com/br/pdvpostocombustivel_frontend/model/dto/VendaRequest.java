package com.br.pdvpostocombustivel_frontend.model.dto;

import com.br.pdvpostocombustivel_frontend.model.enums.TipoVenda;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VendaRequest(
        LocalDateTime dataHora,
        TipoVenda tipoVenda,
        BigDecimal valorTotal,
        List<ItemVendaRequest> itens
) {}
