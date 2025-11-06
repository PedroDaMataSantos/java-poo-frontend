package com.br.pdvpostocombustivel_frontend.model.dto;

import java.util.Date;
import java.math.BigDecimal;

public record PrecoRequest(

        BigDecimal valor,
        String dataAlteracao,
        Date horaAlteracao
) {}