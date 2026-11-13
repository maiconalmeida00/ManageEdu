package com.enterprise.manageedu.infrastructure.storage;

import com.enterprise.manageedu.domain.exception.RegraDeNegocioException;
import com.enterprise.manageedu.domain.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ArmazenamentoLocalArquivo {

    private final Path diretorioRaiz;

    public ArmazenamentoLocalArquivo(@Value("${app.upload.dir:uploads}") String diretorioBase) {
        this.diretorioRaiz = Paths.get(diretorioBase).toAbsolutePath().normalize();
    }

    public String salvar(MultipartFile arquivo, String extensao) {
        try {
            Files.createDirectories(diretorioRaiz);

            // Gera nome interno no servidor para evitar colisão e caminhos arbitrários do cliente.
            String nomeInterno = UUID.randomUUID() + extensao;
            Path destino = resolverCaminhoSeguro(nomeInterno);

            try (InputStream inputStream = arquivo.getInputStream()) {
                Files.copy(inputStream, destino, StandardCopyOption.REPLACE_EXISTING);
            }
            return nomeInterno;
        } catch (IOException ex) {
            throw new RegraDeNegocioException("Não foi possível salvar o arquivo no servidor.");
        }
    }

    public Resource carregar(String nomeInterno) {
        try {
            Path arquivo = resolverCaminhoSeguro(nomeInterno);
            Resource resource = new UrlResource(arquivo.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("Arquivo físico não encontrado.");
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("Arquivo físico inválido.");
        }
    }

    public void removerSilenciosamente(String nomeInterno) {
        if (nomeInterno == null || nomeInterno.isBlank()) {
            return;
        }
        try {
            Path arquivo = resolverCaminhoSeguro(nomeInterno);
            Files.deleteIfExists(arquivo);
        } catch (IOException | RegraDeNegocioException ignored) {
            // Evita mascarar erro principal em caso de rollback de arquivo órfão.
        }
    }

    private Path resolverCaminhoSeguro(String nomeInterno) {
        Path caminho = diretorioRaiz.resolve(nomeInterno).normalize();
        if (!caminho.startsWith(diretorioRaiz)) {
            throw new RegraDeNegocioException("Caminho de arquivo inválido.");
        }
        return caminho;
    }
}

