package com.br.pdvpostocombustivel_frontend.model.dto;

import com.br.pdvpostocombustivel_frontend.model.enums.TipoAcesso;

// (Nota: Senha não é retornada por segurança)
public record AcessoResponse(
        Long id,
        String usuario,
        TipoAcesso perfil
) {}