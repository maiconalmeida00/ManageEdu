package com.enterprise.manageedu.domain.model;

public class OperadorCaptacao extends Usuario {

    public OperadorCaptacao(Long id, String nome) {
        super(id, nome);
    }

    @Override
    public boolean podeAlterarStatus(StatusOportunidade statusAtual, StatusOportunidade novoStatus) {
        return StatusOportunidade.transicaoPermitida(statusAtual, novoStatus)
                && novoStatus != StatusOportunidade.MATRICULA_CONFIRMADA;
    }
}