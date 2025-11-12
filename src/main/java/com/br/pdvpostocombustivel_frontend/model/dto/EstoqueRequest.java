package com.br.pdvpostocombustivel_frontend.model.dto;

import java.math.BigDecimal;
import java.util.Date;

public record EstoqueRequest(
        BigDecimal quantidade,
        String localTanque,
        String loteEndereco,
        String loteFabricacao,
        Date dataValidade,
        Long idProduto
) {}