package com.enterprise.manageedu.adapters.rest;

import com.enterprise.manageedu.application.dto.AnexoResponseDTO;
import com.enterprise.manageedu.application.usecase.AnexoUseCase;
import com.enterprise.manageedu.application.usecase.UsuarioUseCase;
import com.enterprise.manageedu.domain.model.Usuario;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AnexoController {

    private final AnexoUseCase anexoUseCase;
    private final UsuarioUseCase usuarioUseCase;

    public AnexoController(AnexoUseCase anexoUseCase, UsuarioUseCase usuarioUseCase) {
        this.anexoUseCase = anexoUseCase;
        this.usuarioUseCase = usuarioUseCase;
    }

    @PostMapping(value = "/leads/{leadId}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AnexoResponseDTO enviarFotoLead(
            @PathVariable Long leadId,
            @RequestParam("arquivo") MultipartFile arquivo,
            Authentication authentication
    ) {
        Usuario usuario = usuarioUseCase.buscarDominioPorEmail(authentication.getName());
        return anexoUseCase.enviarFotoLead(leadId, arquivo, usuario);
    }

    @GetMapping("/leads/{leadId}/foto")
    public ResponseEntity<Resource> exibirFotoLead(@PathVariable Long leadId) {
        AnexoUseCase.ArquivoDownload download = anexoUseCase.buscarFotoLead(leadId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.anexo().getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + download.anexo().getNomeOriginal() + "\"")
                .body(download.resource());
    }

    @PostMapping(value = "/leads/{leadId}/anexos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AnexoResponseDTO enviarAnexoLead(
            @PathVariable Long leadId,
            @RequestParam("arquivo") MultipartFile arquivo,
            Authentication authentication
    ) {
        Usuario usuario = usuarioUseCase.buscarDominioPorEmail(authentication.getName());
        return anexoUseCase.enviarAnexoLead(leadId, arquivo, usuario);
    }

    @GetMapping("/leads/{leadId}/anexos")
    public List<AnexoResponseDTO> listarAnexosLead(@PathVariable Long leadId) {
        return anexoUseCase.listarAnexosLead(leadId);
    }

    @PostMapping(value = "/oportunidades/{oportunidadeId}/anexos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AnexoResponseDTO enviarAnexoOportunidade(
            @PathVariable Long oportunidadeId,
            @RequestParam("arquivo") MultipartFile arquivo,
            Authentication authentication
    ) {
        Usuario usuario = usuarioUseCase.buscarDominioPorEmail(authentication.getName());
        return anexoUseCase.enviarAnexoOportunidade(oportunidadeId, arquivo, usuario);
    }

    @GetMapping("/oportunidades/{oportunidadeId}/anexos")
    public List<AnexoResponseDTO> listarAnexosOportunidade(@PathVariable Long oportunidadeId) {
        return anexoUseCase.listarAnexosOportunidade(oportunidadeId);
    }

    @GetMapping("/anexos/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        AnexoUseCase.ArquivoDownload download = anexoUseCase.buscarParaDownload(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.anexo().getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + download.anexo().getNomeOriginal() + "\"")
                .body(download.resource());
    }

    @DeleteMapping("/anexos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = usuarioUseCase.buscarDominioPorEmail(authentication.getName());
        anexoUseCase.remover(id, usuario);
    }
}

