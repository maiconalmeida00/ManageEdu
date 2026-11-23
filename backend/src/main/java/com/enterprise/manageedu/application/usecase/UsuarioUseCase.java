package com.enterprise.manageedu.application.usecase;

import com.enterprise.manageedu.application.dto.UsuarioRequestDTO;
import com.enterprise.manageedu.application.dto.UsuarioResponseDTO;
import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.Usuario;
import com.enterprise.manageedu.domain.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public class UsuarioUseCase {

    private static final Logger log = LoggerFactory.getLogger(UsuarioUseCase.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioUseCase(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO request, Usuario usuarioAutenticado) {
        validarPermissaoAdministrador(usuarioAutenticado);
        validarRequisicao(request);
        if (request.senha() == null || request.senha().isBlank() || request.senha().trim().length() < 6) {
            throw new RegraDeNegocioException("A senha deve ter pelo menos 6 caracteres.");
        }

        String emailNormalizado = normalizarEmail(request.email());
        if (usuarioRepository.existePorEmail(emailNormalizado)) {
            throw new RegraDeNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }

        String senhaHash = passwordEncoder.encode(request.senha());
        boolean ativo = request.ativo() == null || request.ativo();

        Usuario novoUsuario = Usuario.criar(null, request.nome(), emailNormalizado, senhaHash, request.perfil(), ativo);
        Usuario salvo = usuarioRepository.salvar(novoUsuario);

        log.info("Ação=CADASTRO_USUARIO | Responsavel={} | UsuarioId={} | Email={} | Perfil={}",
                usuarioAutenticado.getEmail(), salvo.getId(), salvo.getEmail(), salvo.getPerfil());

        return toResponse(salvo);
    }

    public List<UsuarioResponseDTO> listarTodos(Usuario usuarioAutenticado) {
        validarPermissaoAdministrador(usuarioAutenticado);
        return usuarioRepository.listarTodos().stream()
                .map(this::toResponse)
                .toList();
    }

    public UsuarioResponseDTO buscarPorId(Long id, Usuario usuarioAutenticado) {
        validarPermissaoAdministrador(usuarioAutenticado);
        return toResponse(buscarUsuarioOuFalhar(id));
    }

    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO request, Usuario usuarioAutenticado) {
        validarPermissaoAdministrador(usuarioAutenticado);
        validarId(id);
        validarRequisicao(request);

        Usuario existente = buscarUsuarioOuFalhar(id);

        String emailNormalizado = normalizarEmail(request.email());
        usuarioRepository.buscarPorEmail(emailNormalizado)
                .filter(outro -> !outro.getId().equals(id))
                .ifPresent(outro -> {
                    throw new RegraDeNegocioException("Já existe outro usuário cadastrado com este e-mail.");
                });

        boolean ativo = request.ativo() != null ? request.ativo() : existente.isAtivo();
        if (!ativo && existente.getId().equals(usuarioAutenticado.getId())) {
            throw new RegraDeNegocioException("O administrador não pode desativar o próprio usuário.");
        }

        String senhaHash = existente.getSenhaHash();
        if (request.senha() != null && !request.senha().isBlank()) {
            if (request.senha().trim().length() < 6) {
                throw new RegraDeNegocioException("A nova senha deve ter pelo menos 6 caracteres.");
            }
            senhaHash = passwordEncoder.encode(request.senha());
        }

        // Recria a instância conforme o perfil escolhido para manter o polimorfismo correto.
        Usuario atualizado = Usuario.criar(
                existente.getId(),
                request.nome(),
                emailNormalizado,
                senhaHash,
                request.perfil(),
                ativo
        );
        Usuario salvo = usuarioRepository.salvar(atualizado);

        log.info("Ação=ATUALIZACAO_USUARIO | Responsavel={} | UsuarioId={} | Email={} | Perfil={} | Ativo={}",
                usuarioAutenticado.getEmail(), salvo.getId(), salvo.getEmail(), salvo.getPerfil(), salvo.isAtivo());

        return toResponse(salvo);
    }

    public UsuarioResponseDTO alterarAtivo(Long id, boolean ativo, Usuario usuarioAutenticado) {
        validarPermissaoAdministrador(usuarioAutenticado);
        validarId(id);

        if (!ativo && id.equals(usuarioAutenticado.getId())) {
            throw new RegraDeNegocioException("O administrador não pode desativar o próprio usuário.");
        }

        Usuario existente = buscarUsuarioOuFalhar(id);
        existente.alterarAtivo(ativo);
        Usuario salvo = usuarioRepository.salvar(existente);

        log.info("Ação=STATUS_USUARIO | Responsavel={} | UsuarioId={} | Email={} | Ativo={}",
                usuarioAutenticado.getEmail(), salvo.getId(), salvo.getEmail(), salvo.isAtivo());

        return toResponse(salvo);
    }

    public Usuario buscarDominioPorEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RegraDeNegocioException("Usuário autenticado não identificado.");
        }
        return usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado."));
    }

    public UsuarioResponseDTO toResponse(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.isAtivo()
        );
    }

    private void validarRequisicao(UsuarioRequestDTO request) {
        if (request == null) {
            throw new RegraDeNegocioException("Requisição de usuário não pode ser nula.");
        }
        if (request.perfil() == null) {
            throw new RegraDeNegocioException("O perfil do usuário é obrigatório.");
        }
    }

    private Usuario buscarUsuarioOuFalhar(Long id) {
        validarId(id);
        return usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }

    private void validarPermissaoAdministrador(Usuario usuario) {
        if (usuario == null || !usuario.podeGerenciarUsuarios()) {
            throw new RegraDeNegocioException("Somente administradores podem gerenciar usuários.");
        }
    }

    private String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RegraDeNegocioException("O e-mail do usuário é obrigatório.");
        }
        return email.trim().toLowerCase();
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new RegraDeNegocioException("ID do usuário inválido.");
        }
    }
}

