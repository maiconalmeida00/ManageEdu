package com.enterprise.manageedu.application.dto;

import com.enterprise.manageedu.domain.model.PerfilUsuario;

public record UsuarioRequestDTO(
        String nome,
        String email,
        String senha,
        PerfilUsuario perfil,
        Boolean ativo
) {
}

