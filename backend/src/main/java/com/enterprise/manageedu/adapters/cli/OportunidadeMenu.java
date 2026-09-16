package com.enterprise.manageedu.adapters.cli;

import com.enterprise.manageedu.application.dto.AtualizarStatusOportunidadeDTO;
import com.enterprise.manageedu.application.dto.OportunidadeMatriculaRequestDTO;
import com.enterprise.manageedu.application.dto.OportunidadeMatriculaResponseDTO;
import com.enterprise.manageedu.application.usecase.OportunidadeMatriculaUseCase;
import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.StatusOportunidade;
import com.enterprise.manageedu.domain.model.Usuario;

import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

public class OportunidadeMenu {

    private final Scanner sc;
    private final OportunidadeMatriculaUseCase oportunidadeUseCase;
    private final Supplier<Usuario> usuarioAtivo;

    public OportunidadeMenu(
            Scanner sc,
            OportunidadeMatriculaUseCase oportunidadeUseCase,
            Supplier<Usuario> usuarioAtivo
    ) {
        this.sc = sc;
        this.oportunidadeUseCase = oportunidadeUseCase;
        this.usuarioAtivo = usuarioAtivo;
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println();
            System.out.println("--- Gerenciar oportunidades de matrícula ---");
            System.out.println("1. Criar oportunidade");
            System.out.println("2. Listar oportunidades");
            System.out.println("3. Buscar oportunidade por ID");
            System.out.println("4. Atualizar dados da oportunidade");
            System.out.println("5. Alterar status da oportunidade");
            System.out.println("6. Excluir oportunidade");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");

            String opcao = sc.nextLine().trim();
            switch (opcao) {
                case "1" -> criar();
                case "2" -> listar();
                case "3" -> buscarPorId();
                case "4" -> atualizarDados();
                case "5" -> alterarStatus();
                case "6" -> excluir();
                case "0" -> voltar = true;
                default -> System.out.println("Opção invalida. Tente novamente.");
            }
        }
    }

    private void criar() {
        executar(() -> {
            OportunidadeMatriculaRequestDTO request = lerOportunidade();
            OportunidadeMatriculaResponseDTO criada = oportunidadeUseCase.criar(request);
            System.out.println("Oportunidade criada com sucesso. Status inicial: NOVO_LEAD.");
            imprimirOportunidade(criada);
        });
    }

    private void listar() {
        executar(() -> {
            List<OportunidadeMatriculaResponseDTO> oportunidades = oportunidadeUseCase.listarTodos();
            if (oportunidades.isEmpty()) {
                System.out.println("Nenhuma oportunidade cadastrada.");
                return;
            }
            System.out.println("--- Oportunidades ---");
            oportunidades.forEach(this::imprimirOportunidade);
        });
    }

    private void buscarPorId() {
        executar(() -> {
            Long id = lerLong("ID da oportunidade: ");
            imprimirOportunidade(oportunidadeUseCase.buscarPorId(id));
        });
    }

    private void atualizarDados() {
        executar(() -> {
            Long id = lerLong("ID da oportunidade: ");
            OportunidadeMatriculaRequestDTO request = lerOportunidade();
            OportunidadeMatriculaResponseDTO atualizada = oportunidadeUseCase.atualizarDados(id, request);
            System.out.println("Dados da oportunidade atualizados com sucesso. O status nao foi alterado.");
            imprimirOportunidade(atualizada);
        });
    }

    private void alterarStatus() {
        executar(() -> {
            Long id = lerLong("ID da oportunidade: ");
            StatusOportunidade novoStatus = lerStatus();
            OportunidadeMatriculaResponseDTO atualizada = oportunidadeUseCase.alterarStatus(
                    id,
                    new AtualizarStatusOportunidadeDTO(novoStatus),
                    usuarioAtivo.get()
            );
            System.out.println("Status da oportunidade alterado com sucesso.");
            imprimirOportunidade(atualizada);
        });
    }

    private void excluir() {
        executar(() -> {
            Long id = lerLong("ID da oportunidade: ");
            oportunidadeUseCase.remover(id);
            System.out.println("Oportunidade excluida com sucesso.");
        });
    }

    private OportunidadeMatriculaRequestDTO lerOportunidade() {
        Long leadId = lerLong("ID do lead: ");
        Long cursoId = lerLong("ID do curso: ");
        System.out.print("Observacao (opcional): ");
        String observacao = sc.nextLine();
        return new OportunidadeMatriculaRequestDTO(leadId, cursoId, observacao);
    }

    private StatusOportunidade lerStatus() {
        System.out.println("Status: NOVO_LEAD, CONTATO, DOCUMENTACAO, MATRICULA_CONFIRMADA, DESISTENCIA");
        System.out.print("Novo status: ");
        String valor = sc.nextLine().trim().toUpperCase();
        try {
            return StatusOportunidade.valueOf(valor);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido.");
        }
    }

    private Long lerLong(String prompt) {
        System.out.print(prompt);
        String valor = sc.nextLine().trim();
        try {
            return Long.parseLong(valor);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID inválido. Informe um numero.");
        }
    }

    public void imprimirOportunidade(OportunidadeMatriculaResponseDTO oportunidade) {
        System.out.println(
                "ID: " + oportunidade.id()
                        + " | Candidato: " + oportunidade.leadNome()
                        + " | Curso: " + oportunidade.cursoNome()
                        + " | Status: " + oportunidade.status()
                        + " | Criação: " + oportunidade.dataCriacao()
                        + " | Observação: " + (oportunidade.observacao() == null ? "-" : oportunidade.observacao())
        );
    }

    private void executar(Runnable acao) {
        try {
            acao.run();
        } catch (ResourceNotFoundException | RegraDeNegocioException | IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
