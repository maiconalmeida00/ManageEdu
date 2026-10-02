package com.enterprise.manageedu.domain.model;

import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;

import java.util.regex.Pattern;

public class LeadCandidato {

    private static final Pattern EMAIL_VALIDO = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String origem;
    private Curso cursoInteresse;

    public LeadCandidato(
            String nome,
            String email,
            String telefone,
            String origem,
            Curso cursoInteresse
    ) {
        definirDados(nome, email, telefone, origem, cursoInteresse);
    }

    public void definirId(Long id) {
        if (id == null || id <= 0) {
            throw new RegraDeNegocioException("ID inválido.");
        }

        if (this.id != null) {
            throw new RegraDeNegocioException("O ID já foi definido.");
        }

        this.id = id;
    }

    public void atualizarDados(
            String nome,
            String email,
            String telefone,
            String origem,
            Curso cursoInteresse
    ) {
        definirDados(nome, email, telefone, origem, cursoInteresse);
    }

    private void definirDados(
            String nome,
            String email,
            String telefone,
            String origem,
            Curso cursoInteresse
    ) {
        if (nome == null || nome.isBlank()) {
            throw new RegraDeNegocioException("O nome do lead é obrigatório.");
        }

        validarEmail(email);
        validarTelefone(telefone);

        if (origem == null || origem.isBlank()) {
            throw new RegraDeNegocioException("A origem do lead é obrigatória.");
        }

        if (cursoInteresse == null) {
            throw new RegraDeNegocioException(
                    "O curso de interesse do lead é obrigatório."
            );
        }

        this.nome = nome.trim();
        this.email = email.trim();
        this.telefone = telefone.trim();
        this.origem = origem.trim();
        this.cursoInteresse = cursoInteresse;
    }

    private void validarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RegraDeNegocioException("E-mail é obrigatório.");
        }

        if (!EMAIL_VALIDO.matcher(email.trim()).matches()) {
            throw new RegraDeNegocioException("E-mail inválido.");
        }
    }

    private void validarTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            throw new RegraDeNegocioException("Telefone é obrigatório.");
        }

        String somenteNumeros = telefone.replaceAll("\\D", "");

        if (somenteNumeros.length() < 10 || somenteNumeros.length() > 11) {
            throw new RegraDeNegocioException("Telefone inválido.");
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

    public String getTelefone() {
        return telefone;
    }

    public String getOrigem() {
        return origem;
    }

    public Curso getCursoInteresse() {
        return cursoInteresse;
    }
}