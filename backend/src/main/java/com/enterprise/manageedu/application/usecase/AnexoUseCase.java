package com.enterprise.manageedu.application.usecase;

import com.enterprise.manageedu.application.dto.AnexoResponseDTO;
import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.Anexo;
import com.enterprise.manageedu.domain.model.TipoAnexo;
import com.enterprise.manageedu.domain.model.Usuario;
import com.enterprise.manageedu.domain.repository.AnexoRepository;
import com.enterprise.manageedu.domain.repository.LeadRepository;
import com.enterprise.manageedu.domain.repository.OportunidadeRepository;
import com.enterprise.manageedu.infrastructure.storage.ArmazenamentoLocalArquivo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnexoUseCase {

    private static final Logger log = LoggerFactory.getLogger(AnexoUseCase.class);

    private static final long TAMANHO_MAXIMO_FOTO = 5L * 1024 * 1024;
    private static final long TAMANHO_MAXIMO_ANEXO = 10L * 1024 * 1024;

    private static final Map<String, String> EXTENSOES_POR_CONTENT_TYPE_FOTO = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    private static final Map<String, String> EXTENSOES_POR_CONTENT_TYPE_ANEXO = Map.of(
            "application/pdf", ".pdf",
            "image/jpeg", ".jpg",
            "image/png", ".png"
    );

    private static final Set<String> EXTENSOES_FOTO = Set.of(".jpg", ".jpeg", ".png", ".webp");
    private static final Set<String> EXTENSOES_ANEXO = Set.of(".pdf", ".jpg", ".jpeg", ".png");

    private final AnexoRepository anexoRepository;
    private final LeadRepository leadRepository;
    private final OportunidadeRepository oportunidadeRepository;
    private final ArmazenamentoLocalArquivo armazenamento;

    public AnexoUseCase(
            AnexoRepository anexoRepository,
            LeadRepository leadRepository,
            OportunidadeRepository oportunidadeRepository,
            ArmazenamentoLocalArquivo armazenamento
    ) {
        this.anexoRepository = anexoRepository;
        this.leadRepository = leadRepository;
        this.oportunidadeRepository = oportunidadeRepository;
        this.armazenamento = armazenamento;
    }

    public AnexoResponseDTO enviarFotoLead(Long leadId, MultipartFile arquivo, Usuario usuario) {
        validarUsuario(usuario);
        validarId(leadId, "ID do lead inválido.");
        leadRepository.buscarPorId(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead não encontrado."));

        String extensao = validarArquivo(arquivo, TAMANHO_MAXIMO_FOTO, EXTENSOES_POR_CONTENT_TYPE_FOTO, EXTENSOES_FOTO,
                "A foto deve ser JPG, PNG ou WEBP com no máximo 5 MB.");

        Anexo salvo = salvarArquivoEVinculo(arquivo, extensao, TipoAnexo.FOTO_LEAD, leadId, null, usuario);
        log.info("Ação=UPLOAD_FOTO_LEAD | Responsavel={} | LeadId={} | AnexoId={}",
                usuario.getEmail(), leadId, salvo.getId());
        return toResponse(salvo);
    }

    public AnexoResponseDTO enviarAnexoLead(Long leadId, MultipartFile arquivo, Usuario usuario) {
        validarUsuario(usuario);
        validarId(leadId, "ID do lead inválido.");
        leadRepository.buscarPorId(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead não encontrado."));

        String extensao = validarArquivo(arquivo, TAMANHO_MAXIMO_ANEXO, EXTENSOES_POR_CONTENT_TYPE_ANEXO, EXTENSOES_ANEXO,
                "O anexo deve ser PDF, JPG ou PNG com no máximo 10 MB.");

        Anexo salvo = salvarArquivoEVinculo(arquivo, extensao, TipoAnexo.ANEXO_LEAD, leadId, null, usuario);
        log.info("Ação=UPLOAD_ANEXO_LEAD | Responsavel={} | LeadId={} | AnexoId={}",
                usuario.getEmail(), leadId, salvo.getId());
        return toResponse(salvo);
    }

    public AnexoResponseDTO enviarAnexoOportunidade(Long oportunidadeId, MultipartFile arquivo, Usuario usuario) {
        validarUsuario(usuario);
        validarId(oportunidadeId, "ID da oportunidade inválido.");
        oportunidadeRepository.buscarPorId(oportunidadeId)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidade não encontrada."));

        String extensao = validarArquivo(arquivo, TAMANHO_MAXIMO_ANEXO, EXTENSOES_POR_CONTENT_TYPE_ANEXO, EXTENSOES_ANEXO,
                "O anexo deve ser PDF, JPG ou PNG com no máximo 10 MB.");

        Anexo salvo = salvarArquivoEVinculo(arquivo, extensao, TipoAnexo.ANEXO_OPORTUNIDADE, null, oportunidadeId, usuario);
        log.info("Ação=UPLOAD_ANEXO_OPORTUNIDADE | Responsavel={} | OportunidadeId={} | AnexoId={}",
                usuario.getEmail(), oportunidadeId, salvo.getId());
        return toResponse(salvo);
    }

    public List<AnexoResponseDTO> listarAnexosLead(Long leadId) {
        validarId(leadId, "ID do lead inválido.");
        leadRepository.buscarPorId(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead não encontrado."));
        return anexoRepository.listarPorLeadIdETipo(leadId, TipoAnexo.ANEXO_LEAD).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AnexoResponseDTO> listarAnexosOportunidade(Long oportunidadeId) {
        validarId(oportunidadeId, "ID da oportunidade inválido.");
        oportunidadeRepository.buscarPorId(oportunidadeId)
                .orElseThrow(() -> new ResourceNotFoundException("Oportunidade não encontrada."));
        return anexoRepository.listarPorOportunidadeId(oportunidadeId).stream()
                .map(this::toResponse)
                .toList();
    }

    public ArquivoDownload buscarFotoLead(Long leadId) {
        validarId(leadId, "ID do lead inválido.");
        List<Anexo> fotos = anexoRepository.listarPorLeadIdETipo(leadId, TipoAnexo.FOTO_LEAD);
        if (fotos.isEmpty()) {
            throw new ResourceNotFoundException("Foto do lead não encontrada.");
        }
        Anexo foto = fotos.get(0);
        return new ArquivoDownload(foto, armazenamento.carregar(foto.getNomeInterno()));
    }

    public ArquivoDownload buscarParaDownload(Long anexoId) {
        validarId(anexoId, "ID do anexo inválido.");
        Anexo anexo = anexoRepository.buscarPorId(anexoId)
                .orElseThrow(() -> new ResourceNotFoundException("Anexo não encontrado."));
        return new ArquivoDownload(anexo, armazenamento.carregar(anexo.getNomeInterno()));
    }

    public void remover(Long anexoId, Usuario usuario) {
        validarUsuario(usuario);
        validarId(anexoId, "ID do anexo inválido.");
        Anexo anexo = anexoRepository.buscarPorId(anexoId)
                .orElseThrow(() -> new ResourceNotFoundException("Anexo não encontrado."));

        anexoRepository.remover(anexoId);
        armazenamento.removerSilenciosamente(anexo.getNomeInterno());

        log.info("Ação=REMOCAO_ANEXO | Responsavel={} | AnexoId={} | Tipo={}",
                usuario.getEmail(), anexoId, anexo.getTipo());
    }

    private Anexo salvarArquivoEVinculo(
            MultipartFile arquivo,
            String extensao,
            TipoAnexo tipo,
            Long leadId,
            Long oportunidadeId,
            Usuario usuario
    ) {
        String nomeInterno = armazenamento.salvar(arquivo, extensao);
        try {
            String nomeOriginal = arquivo.getOriginalFilename() != null
                    ? arquivo.getOriginalFilename()
                    : "arquivo" + extensao;

            Anexo anexo = new Anexo(
                    nomeOriginal,
                    nomeInterno,
                    arquivo.getContentType(),
                    arquivo.getSize(),
                    tipo,
                    leadId,
                    oportunidadeId,
                    usuario.getId(),
                    LocalDateTime.now()
            );
            return anexoRepository.salvar(anexo);
        } catch (RuntimeException ex) {
            // Evita deixar arquivo órfão no disco caso a persistência no banco falhe.
            armazenamento.removerSilenciosamente(nomeInterno);
            throw ex;
        }
    }

    private String validarArquivo(
            MultipartFile arquivo,
            long tamanhoMaximo,
            Map<String, String> extensoesPorContentType,
            Set<String> extensoesPermitidas,
            String mensagemErroTipo
    ) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new RegraDeNegocioException("Nenhum arquivo foi enviado.");
        }
        if (arquivo.getSize() > tamanhoMaximo) {
            throw new RegraDeNegocioException(mensagemErroTipo);
        }

        String contentType = arquivo.getContentType();
        if (contentType == null || !extensoesPorContentType.containsKey(contentType.toLowerCase())) {
            throw new RegraDeNegocioException(mensagemErroTipo);
        }

        String nomeOriginal = arquivo.getOriginalFilename();
        if (nomeOriginal != null && nomeOriginal.contains(".")) {
            String ext = nomeOriginal.substring(nomeOriginal.lastIndexOf('.')).toLowerCase();
            if (!extensoesPermitidas.contains(ext)) {
                throw new RegraDeNegocioException(mensagemErroTipo);
            }
        }

        return extensoesPorContentType.get(contentType.toLowerCase());
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new RegraDeNegocioException("Usuário autenticado é obrigatório.");
        }
    }

    private void validarId(Long id, String mensagem) {
        if (id == null || id <= 0) {
            throw new RegraDeNegocioException(mensagem);
        }
    }

    private AnexoResponseDTO toResponse(Anexo anexo) {
        return new AnexoResponseDTO(
                anexo.getId(),
                anexo.getNomeOriginal(),
                anexo.getContentType(),
                anexo.getTamanhoBytes(),
                anexo.getTipo(),
                anexo.getLeadId(),
                anexo.getOportunidadeId(),
                anexo.getDataUpload(),
                "/api/anexos/" + anexo.getId() + "/download"
        );
    }

    public record ArquivoDownload(Anexo anexo, Resource resource) {
    }
}

