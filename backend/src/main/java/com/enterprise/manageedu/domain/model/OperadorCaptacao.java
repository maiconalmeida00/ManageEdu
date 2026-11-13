package com.enterprise.manageedu.domain.model;

public class OperadorCaptacao extends Usuario {

    public OperadorCaptacao(Long id, String nome) {
        super(id, nome);
    }

    public OperadorCaptacao(Long id, String nome, String email, String senhaHash, boolean ativo) {
        super(id, nome, email, senhaHash, ativo);
    }

    @Override
    public PerfilUsuario getPerfil() {
        return PerfilUsuario.OPERADOR_CAPTACAO;
    }

    @Override
    public boolean podeAlterarStatus(StatusOportunidade statusAtual, StatusOportunidade novoStatus) {
        // Operador de captação não pode confirmar matrícula diretamente.
        return StatusOportunidade.transicaoPermitida(statusAtual, novoStatus)
                && novoStatus != StatusOportunidade.MATRICULA_CONFIRMADA;
    }

    @Override
    public boolean podeReabrirOportunidade() {
        return false;
    }
}
