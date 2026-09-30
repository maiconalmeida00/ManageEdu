package com.enterprise.manageedu.domain.model;

import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public enum StatusOportunidade {

    NOVO_LEAD,
    CONTATO,
    DOCUMENTACAO,
    MATRICULA_CONFIRMADA,
    DESISTENCIA;

    private static final Map<StatusOportunidade, Set<StatusOportunidade>> TRANSICOES = new EnumMap<>(StatusOportunidade.class);

    static {
        TRANSICOES.put(NOVO_LEAD, Set.of(CONTATO, DESISTENCIA));
        TRANSICOES.put(CONTATO, Set.of(DOCUMENTACAO, DESISTENCIA));
        TRANSICOES.put(DOCUMENTACAO, Set.of(MATRICULA_CONFIRMADA, DESISTENCIA));
        TRANSICOES.put(MATRICULA_CONFIRMADA, Set.of());
        TRANSICOES.put(DESISTENCIA, Set.of());
    }

    public static boolean transicaoPermitida(StatusOportunidade atual, StatusOportunidade novo) {
        if (atual == null || novo == null) {
            return false;
        }
        return TRANSICOES.get(atual).contains(novo);
    }

    public static void validarTransicao(StatusOportunidade atual, StatusOportunidade novo) {
        if (atual == null) {
            throw new RegraDeNegocioException("O status atual da oportunidade não pode ser nulo.");
        }
        if (novo == null) {
            throw new RegraDeNegocioException("O novo status da oportunidade não pode ser nulo.");
        }
        if (!transicaoPermitida(atual, novo)) {
            throw new RegraDeNegocioException("Transição de status não permitida.");
        }
    }
}