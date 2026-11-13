package com.enterprise.manageedu.domain.model;

public class Administrador extends Usuario {

    public Administrador(Long id, String nome) {
        super(id, nome);
    }

    public Administrador(Long id, String nome, String email, String senhaHash, boolean ativo) {
        super(id, nome, email, senhaHash, ativo);
    }

    @Override
    public PerfilUsuario getPerfil() {
        return PerfilUsuario.ADMINISTRADOR;
    }

    @Override
    public boolean podeAlterarStatus(StatusOportunidade statusAtual, StatusOportunidade novoStatus) {
        return StatusOportunidade.transicaoPermitida(statusAtual, novoStatus);
    }

    @Override
    public boolean podeReabrirOportunidade() {
        return true;
    }

    @Override
    public boolean podeGerenciarUsuarios() {
        return true;
    }
}
