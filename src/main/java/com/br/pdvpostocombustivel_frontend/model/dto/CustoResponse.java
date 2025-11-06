package com.br.pdvpostocombustivel_frontend.model.dto;

import java.util.Date;

public record CustoResponse(
        Long id,
        Double imposto,
        Double custoVarivel,
        Double custoFixo,
        Double margemLucro,
        Date dataProcessamento
) {
}

