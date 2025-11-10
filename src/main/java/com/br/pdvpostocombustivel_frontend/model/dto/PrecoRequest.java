package com.br.pdvpostocombustivel_frontend.model.dto;

import java.math.BigDecimal;
import java.util.Date;

public record PrecoRequest(
        Long idProduto,
        BigDecimal valor,
        String dataAlteracao,
        Date horaAlteracao
) {}
