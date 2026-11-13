package com.enterprise.manageedu.infrastructure.persistence;

import com.enterprise.manageedu.domain.model.Anexo;
import com.enterprise.manageedu.domain.model.TipoAnexo;
import com.enterprise.manageedu.domain.repository.AnexoRepository;
import com.enterprise.manageedu.infrastructure.persistence.entity.AnexoJpaEntity;
import com.enterprise.manageedu.infrastructure.persistence.repository.SpringDataAnexoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class AnexoRepositoryImpl implements AnexoRepository {

    private final SpringDataAnexoRepository springDataAnexoRepository;

    public AnexoRepositoryImpl(SpringDataAnexoRepository springDataAnexoRepository) {
        this.springDataAnexoRepository = springDataAnexoRepository;
    }

    @Override
    @Transactional
    public Anexo salvar(Anexo anexo) {
        AnexoJpaEntity entity = new AnexoJpaEntity(
                anexo.getId(),
                anexo.getNomeOriginal(),
                anexo.getNomeInterno(),
                anexo.getContentType(),
                anexo.getTamanhoBytes(),
                anexo.getTipo(),
                anexo.getLeadId(),
                anexo.getOportunidadeId(),
                anexo.getUsuarioUploadId(),
                anexo.getDataUpload()
        );
        entity = springDataAnexoRepository.save(entity);
        if (anexo.getId() == null) {
            anexo.definirId(entity.getId());
        }
        return anexo;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Anexo> buscarPorId(Long id) {
        return springDataAnexoRepository.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Anexo> listarPorLeadIdETipo(Long leadId, TipoAnexo tipo) {
        return springDataAnexoRepository.findByLeadIdAndTipoOrderByDataUploadDesc(leadId, tipo).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Anexo> listarPorOportunidadeId(Long oportunidadeId) {
        return springDataAnexoRepository.findByOportunidadeIdOrderByDataUploadDesc(oportunidadeId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void remover(Long id) {
        springDataAnexoRepository.deleteById(id);
    }

    private Anexo toDomain(AnexoJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Anexo anexo = new Anexo(
                entity.getNomeOriginal(),
                entity.getNomeInterno(),
                entity.getContentType(),
                entity.getTamanhoBytes(),
                entity.getTipo(),
                entity.getLeadId(),
                entity.getOportunidadeId(),
                entity.getUsuarioUploadId(),
                entity.getDataUpload()
        );
        anexo.definirId(entity.getId());
        return anexo;
    }
}

