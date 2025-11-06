package com.br.pdvpostocombustivel_frontend.model.dto;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

public record CustoRequest(
        Double imposto,
        Double custoVariavel,
        Double custoFixo,
        Double margemLucro,
        Date dataProcessameto
) {
}