package com.enterprise.manageedu.domain.model;

import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;

import java.time.LocalDateTime;

public class Anexo {

    private Long id;
    private final String nomeOriginal;
    private final String nomeInterno;
    private final String contentType;
    private final long tamanhoBytes;
    private final TipoAnexo tipo;
    private final Long leadId;
    private final Long oportunidadeId;
    private final Long usuarioUploadId;
    private final LocalDateTime dataUpload;

    public Anexo(
            String nomeOriginal,
            String nomeInterno,
            String contentType,
            long tamanhoBytes,
            TipoAnexo tipo,
            Long leadId,
            Long oportunidadeId,
            Long usuarioUploadId,
            LocalDateTime dataUpload
    ) {
        if (nomeOriginal == null || nomeOriginal.isBlank()) {
            throw new RegraDeNegocioException("O nome original do arquivo é obrigatório.");
        }
        if (nomeInterno == null || nomeInterno.isBlank()) {
            throw new RegraDeNegocioException("O nome interno do arquivo é obrigatório.");
        }
        if (contentType == null || contentType.isBlank()) {
            throw new RegraDeNegocioException("O tipo de conteúdo do arquivo é obrigatório.");
        }
        if (tamanhoBytes <= 0) {
            throw new RegraDeNegocioException("O arquivo não pode estar vazio.");
        }
        if (tipo == null) {
            throw new RegraDeNegocioException("O tipo do anexo é obrigatório.");
        }
        validarVinculo(tipo, leadId, oportunidadeId);

        this.nomeOriginal = nomeOriginal.trim();
        this.nomeInterno = nomeInterno.trim();
        this.contentType = contentType.trim();
        this.tamanhoBytes = tamanhoBytes;
        this.tipo = tipo;
        this.leadId = leadId;
        this.oportunidadeId = oportunidadeId;
        this.usuarioUploadId = usuarioUploadId;
        this.dataUpload = dataUpload != null ? dataUpload : LocalDateTime.now();
    }

    // Garante que fotos/anexos de lead apontem apenas para lead, e anexos de oportunidade apenas para oportunidade.
    private void validarVinculo(TipoAnexo tipo, Long leadId, Long oportunidadeId) {
        if (tipo == TipoAnexo.FOTO_LEAD || tipo == TipoAnexo.ANEXO_LEAD) {
            if (leadId == null || leadId <= 0 || oportunidadeId != null) {
                throw new RegraDeNegocioException("Anexo de lead deve estar vinculado apenas a um lead válido.");
            }
        } else if (tipo == TipoAnexo.ANEXO_OPORTUNIDADE) {
            if (oportunidadeId == null || oportunidadeId <= 0 || leadId != null) {
                throw new RegraDeNegocioException("Anexo de oportunidade deve estar vinculado apenas a uma oportunidade válida.");
            }
        }
    }

    public void definirId(Long id) {
        if (id == null || id <= 0) {
            throw new RegraDeNegocioException("ID inválido.");
        }
        if (this.id != null) {
            throw new RegraDeNegocioException("O ID já foi definido.");
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getNomeOriginal() {
        return nomeOriginal;
    }

    public String getNomeInterno() {
        return nomeInterno;
    }

    public String getContentType() {
        return contentType;
    }

    public long getTamanhoBytes() {
        return tamanhoBytes;
    }

    public TipoAnexo getTipo() {
        return tipo;
    }

    public Long getLeadId() {
        return leadId;
    }

    public Long getOportunidadeId() {
        return oportunidadeId;
    }

    public Long getUsuarioUploadId() {
        return usuarioUploadId;
    }

    public LocalDateTime getDataUpload() {
        return dataUpload;
    }
}

