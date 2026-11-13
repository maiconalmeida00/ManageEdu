package com.enterprise.manageedu.application.dto;

import com.enterprise.manageedu.domain.model.TipoAnexo;

import java.time.LocalDateTime;

public record AnexoResponseDTO(
        Long id,
        String nomeOriginal,
        String contentType,
        long tamanhoBytes,
        TipoAnexo tipo,
        Long leadId,
        Long oportunidadeId,
        LocalDateTime dataUpload,
        String urlDownload
) {
}

