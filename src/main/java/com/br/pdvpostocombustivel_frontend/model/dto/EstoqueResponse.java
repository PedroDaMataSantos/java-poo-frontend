package com.br.pdvpostocombustivel_frontend.model.dto;

import com.br.pdvpostocombustivel_frontend.model.enums.TipoEstoque;
import java.math.BigDecimal;
import java.util.Date;

public record EstoqueResponse(
        Long id,
        BigDecimal quantidade,
        String localTanque,
        String loteEndereco,
        String loteFabricacao,
        Date dataValidade,
        TipoEstoque tipo,
        Long idProduto


) {}