package com.enterprise.manageedu.adapters.cli;

import com.enterprise.manageedu.application.dto.CursoRequestDTO;
import com.enterprise.manageedu.application.dto.CursoResponseDTO;
import com.enterprise.manageedu.application.usecase.CursoUseCase;
import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import com.enterprise.manageedu.domain.model.ModalidadeCurso;
import com.enterprise.manageedu.domain.model.TurnoCurso;

import java.util.List;
import java.util.Scanner;

public class CursoMenu {

    private final Scanner sc;
    private final CursoUseCase cursoUseCase;

    public CursoMenu(Scanner sc, CursoUseCase cursoUseCase) {
        this.sc = sc;
        this.cursoUseCase = cursoUseCase;
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println();
            System.out.println("--- Gerenciar cursos ---");
            System.out.println("1. Cadastrar curso");
            System.out.println("2. Listar cursos");
            System.out.println("3. Buscar curso por ID");
            System.out.println("4. Atualizar curso");
            System.out.println("5. Excluir curso");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");

            String opcao = sc.nextLine().trim();
            switch (opcao) {
                case "1" -> cadastrar();
                case "2" -> listar();
                case "3" -> buscarPorId();
                case "4" -> atualizar();
                case "5" -> excluir();
                case "0" -> voltar = true;
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private void cadastrar() {
        executar(() -> {
            CursoRequestDTO request = lerCurso();
            CursoResponseDTO criado = cursoUseCase.cadastrar(request);
            System.out.println("Curso cadastrado com sucesso.");
            imprimirCurso(criado);
        });
    }

    private void listar() {
        executar(() -> {
            List<CursoResponseDTO> cursos = cursoUseCase.listarTodos();
            if (cursos.isEmpty()) {
                System.out.println("Nenhum curso cadastrado.");
                return;
            }
            System.out.println("--- Cursos ---");
            cursos.forEach(this::imprimirCurso);
        });
    }

    private void buscarPorId() {
        executar(() -> {
            Long id = lerLong("ID do curso: ");
            imprimirCurso(cursoUseCase.buscarPorId(id));
        });
    }

    private void atualizar() {
        executar(() -> {
            Long id = lerLong("ID do curso: ");
            CursoRequestDTO request = lerCurso();
            CursoResponseDTO atualizado = cursoUseCase.atualizar(id, request);
            System.out.println("Curso atualizado com sucesso.");
            imprimirCurso(atualizado);
        });
    }

    private void excluir() {
        executar(() -> {
            Long id = lerLong("ID do curso: ");
            cursoUseCase.remover(id);
            System.out.println("Curso excluído com sucesso.");
        });
    }

    private CursoRequestDTO lerCurso() {
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        String modalidade = lerModalidade();
        String turno = lerTurno();
        return new CursoRequestDTO(nome, modalidade, turno);
    }

    private String lerModalidade() {
        System.out.println("Modalidades: PRESENCIAL, EAD, HIBRIDO");
        System.out.print("Modalidade: ");
        String valor = sc.nextLine().trim().toUpperCase();
        try {
            ModalidadeCurso.valueOf(valor);
            return valor;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Modalidade inválida.");
        }
    }

    private String lerTurno() {
        System.out.println("Turnos: MATUTINO, VESPERTINO, NOTURNO, INTEGRAL");
        System.out.print("Turno: ");
        String valor = sc.nextLine().trim().toUpperCase();
        try {
            TurnoCurso.valueOf(valor);
            return valor;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Turno inválido.");
        }
    }

    private Long lerLong(String prompt) {
        System.out.print(prompt);
        String valor = sc.nextLine().trim();
        try {
            return Long.parseLong(valor);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID inválido. Informe um número.");
        }
    }

    private void imprimirCurso(CursoResponseDTO curso) {
        System.out.println(
                "ID: " + curso.id()
                        + " | Nome: " + curso.nome()
                        + " | Modalidade: " + curso.modalidade()
                        + " | Turno: " + curso.turno()
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
