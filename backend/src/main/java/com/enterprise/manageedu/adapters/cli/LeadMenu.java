package com.enterprise.manageedu.adapters.cli;

import com.enterprise.manageedu.application.dto.LeadCandidatoRequestDTO;
import com.enterprise.manageedu.application.dto.LeadCandidatoResponseDTO;
import com.enterprise.manageedu.application.usecase.LeadCandidatoUseCase;
import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Scanner;

public class LeadMenu {

    private final Scanner sc;
    private final LeadCandidatoUseCase leadUseCase;

    public LeadMenu(Scanner sc, LeadCandidatoUseCase leadUseCase) {
        this.sc = sc;
        this.leadUseCase = leadUseCase;
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println();
            System.out.println("--- Gerenciar leads/candidatos ---");
            System.out.println("1. Cadastrar lead");
            System.out.println("2. Listar leads");
            System.out.println("3. Buscar lead por ID");
            System.out.println("4. Atualizar lead");
            System.out.println("5. Excluir lead");
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
            LeadCandidatoRequestDTO request = lerLead();
            LeadCandidatoResponseDTO criado = leadUseCase.cadastrar(request);
            System.out.println("Lead cadastrado com sucesso.");
            imprimirLead(criado);
        });
    }

    private void listar() {
        executar(() -> {
            List<LeadCandidatoResponseDTO> leads = leadUseCase.listarTodos();
            if (leads.isEmpty()) {
                System.out.println("Nenhum lead cadastrado.");
                return;
            }
            System.out.println("--- Leads ---");
            leads.forEach(this::imprimirLead);
        });
    }

    private void buscarPorId() {
        executar(() -> {
            Long id = lerLong("ID do lead: ");
            imprimirLead(leadUseCase.buscarPorId(id));
        });
    }

    private void atualizar() {
        executar(() -> {
            Long id = lerLong("ID do lead: ");
            LeadCandidatoRequestDTO request = lerLead();
            LeadCandidatoResponseDTO atualizado = leadUseCase.atualizar(id, request);
            System.out.println("Lead atualizado com sucesso.");
            imprimirLead(atualizado);
        });
    }

    private void excluir() {
        executar(() -> {
            Long id = lerLong("ID do lead: ");
            leadUseCase.remover(id);
            System.out.println("Lead excluído com sucesso.");
        });
    }

    private LeadCandidatoRequestDTO lerLead() {
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Telefone: ");
        String telefone = sc.nextLine();
        System.out.print("Origem: ");
        String origem = sc.nextLine();
        Long cursoId = lerLong("ID do curso de interesse: ");
        return new LeadCandidatoRequestDTO(nome, email, telefone, origem, cursoId);
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

    private void imprimirLead(LeadCandidatoResponseDTO lead) {
        System.out.println(
                "ID: " + lead.id()
                        + " | Nome: " + lead.nome()
                        + " | Email: " + lead.email()
                        + " | Telefone: " + lead.telefone()
                        + " | Origem: " + lead.origem()
                        + " | Curso interesse: [" + lead.cursoInteresseId() + "] " + lead.cursoInteresseNome()
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
