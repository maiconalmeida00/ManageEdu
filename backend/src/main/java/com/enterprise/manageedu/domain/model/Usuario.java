package com.enterprise.manageedu.domain.model;

import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;

import java.util.regex.Pattern;

public abstract class Usuario {

    private static final Pattern EMAIL_VALIDO = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private Long id;
    private String nome;
    private String email;
    private String senhaHash;
    private boolean ativo;

    protected Usuario(Long id, String nome) {
        if (id == null || id <= 0 || nome == null || nome.isBlank()) {
            throw new RegraDeNegocioException("Usuário deve possuir ID e nome válidos.");
        }
        this.id = id;
        this.nome = nome.trim();
        this.ativo = true;
    }

    protected Usuario(Long id, String nome, String email, String senhaHash, boolean ativo) {
        if (id != null && id <= 0) {
            throw new RegraDeNegocioException("ID do usuário inválido.");
        }
        validarNome(nome);
        validarEmail(email);
        validarSenhaHash(senhaHash);

        this.id = id;
        this.nome = nome.trim();
        this.email = email.trim().toLowerCase();
        this.senhaHash = senhaHash;
        this.ativo = ativo;
    }

    public static Usuario criar(
            Long id,
            String nome,
            String email,
            String senhaHash,
            PerfilUsuario perfil,
            boolean ativo
    ) {
        if (perfil == null) {
            throw new RegraDeNegocioException("O perfil do usuário é obrigatório.");
        }
        if (perfil == PerfilUsuario.ADMINISTRADOR) {
            return new Administrador(id, nome, email, senhaHash, ativo);
        }
        return new OperadorCaptacao(id, nome, email, senhaHash, ativo);
    }

    // Impede alteração do ID após o usuário já estar identificado/persistido.
    public void definirId(Long id) {
        if (id == null || id <= 0) {
            throw new RegraDeNegocioException("ID inválido.");
        }
        if (this.id != null) {
            throw new RegraDeNegocioException("O ID já foi definido.");
        }
        this.id = id;
    }

    public void atualizarDados(String nome, String email, boolean ativo) {
        validarNome(nome);
        validarEmail(email);
        this.nome = nome.trim();
        this.email = email.trim().toLowerCase();
        this.ativo = ativo;
    }

    public void atualizarSenhaHash(String novaSenhaHash) {
        validarSenhaHash(novaSenhaHash);
        this.senhaHash = novaSenhaHash;
    }

    public void alterarAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraDeNegocioException("O nome do usuário é obrigatório.");
        }
    }

    private void validarEmail(String email) {
        if (email == null || email.isBlank() || !EMAIL_VALIDO.matcher(email.trim()).matches()) {
            throw new RegraDeNegocioException("E-mail do usuário inválido.");
        }
    }

    private void validarSenhaHash(String senhaHash) {
        if (senhaHash == null || senhaHash.isBlank()) {
            throw new RegraDeNegocioException("A senha do usuário é obrigatória.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public abstract PerfilUsuario getPerfil();

    public abstract boolean podeAlterarStatus(
            StatusOportunidade statusAtual,
            StatusOportunidade novoStatus
    );

    public abstract boolean podeReabrirOportunidade();

    public boolean podeGerenciarUsuarios() {
        return false;
    }
}
