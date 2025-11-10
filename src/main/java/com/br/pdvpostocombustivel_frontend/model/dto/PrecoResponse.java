package com.br.pdvpostocombustivel_frontend.model.dto;

import java.math.BigDecimal;
import java.util.Date;

public record PrecoResponse(
        Long id,
        Long idProduto,
        String nomeProduto,
        BigDecimal valor,
        String dataAlteracao,
        Date horaAlteracao
) {}
