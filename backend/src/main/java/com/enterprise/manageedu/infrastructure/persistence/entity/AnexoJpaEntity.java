package com.enterprise.manageedu.infrastructure.persistence.entity;

import com.enterprise.manageedu.domain.model.TipoAnexo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "anexos")
public class AnexoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_original", nullable = false, length = 255)
    private String nomeOriginal;

    @Column(name = "nome_interno", nullable = false, unique = true, length = 255)
    private String nomeInterno;

    @Column(name = "content_type", nullable = false, length = 120)
    private String contentType;

    @Column(name = "tamanho_bytes", nullable = false)
    private long tamanhoBytes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoAnexo tipo;

    @Column(name = "lead_id")
    private Long leadId;

    @Column(name = "oportunidade_id")
    private Long oportunidadeId;

    @Column(name = "usuario_upload_id")
    private Long usuarioUploadId;

    @Column(name = "data_upload", nullable = false)
    private LocalDateTime dataUpload;

    public AnexoJpaEntity() {
    }

    public AnexoJpaEntity(
            Long id,
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
        this.id = id;
        this.nomeOriginal = nomeOriginal;
        this.nomeInterno = nomeInterno;
        this.contentType = contentType;
        this.tamanhoBytes = tamanhoBytes;
        this.tipo = tipo;
        this.leadId = leadId;
        this.oportunidadeId = oportunidadeId;
        this.usuarioUploadId = usuarioUploadId;
        this.dataUpload = dataUpload;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeOriginal() {
        return nomeOriginal;
    }

    public void setNomeOriginal(String nomeOriginal) {
        this.nomeOriginal = nomeOriginal;
    }

    public String getNomeInterno() {
        return nomeInterno;
    }

    public void setNomeInterno(String nomeInterno) {
        this.nomeInterno = nomeInterno;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getTamanhoBytes() {
        return tamanhoBytes;
    }

    public void setTamanhoBytes(long tamanhoBytes) {
        this.tamanhoBytes = tamanhoBytes;
    }

    public TipoAnexo getTipo() {
        return tipo;
    }

    public void setTipo(TipoAnexo tipo) {
        this.tipo = tipo;
    }

    public Long getLeadId() {
        return leadId;
    }

    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }

    public Long getOportunidadeId() {
        return oportunidadeId;
    }

    public void setOportunidadeId(Long oportunidadeId) {
        this.oportunidadeId = oportunidadeId;
    }

    public Long getUsuarioUploadId() {
        return usuarioUploadId;
    }

    public void setUsuarioUploadId(Long usuarioUploadId) {
        this.usuarioUploadId = usuarioUploadId;
    }

    public LocalDateTime getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(LocalDateTime dataUpload) {
        this.dataUpload = dataUpload;
    }
}

