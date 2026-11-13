package com.enterprise.manageedu.application.dto;

import com.enterprise.manageedu.domain.model.PerfilUsuario;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        PerfilUsuario perfil,
        boolean ativo
) {
}

