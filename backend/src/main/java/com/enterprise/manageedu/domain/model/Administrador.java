package com.enterprise.manageedu.domain.model;

public class Administrador extends Usuario {

    public Administrador(Long id, String nome) {
        super(id, nome);
    }

    @Override
    public boolean podeAlterarStatus(StatusOportunidade statusAtual, StatusOportunidade novoStatus) {
        return StatusOportunidade.transicaoPermitida(statusAtual, novoStatus);
    }

    @Override
    public boolean podeReabrirOportunidade() {
        return true;
    }
}