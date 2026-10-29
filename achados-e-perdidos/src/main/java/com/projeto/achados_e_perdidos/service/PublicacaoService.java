package com.projeto.achados_e_perdidos.service;

import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto.achados_e_perdidos.model.Publicacao;
import com.projeto.achados_e_perdidos.model.Usuario;
import com.projeto.achados_e_perdidos.model.enums.StatusOcorrencia;
import com.projeto.achados_e_perdidos.model.enums.StatusPublicacao;
import com.projeto.achados_e_perdidos.repository.PublicacaoRepository;
import com.projeto.achados_e_perdidos.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PublicacaoService {

    private final PublicacaoRepository publicacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LogsService logsService;

    public PublicacaoService(
            PublicacaoRepository publicacaoRepository,
            UsuarioRepository usuarioRepository,
            LogsService logsService) {
        this.publicacaoRepository = publicacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.logsService = logsService;
    }

    @Transactional
    public Publicacao criar(Long idUsuario, Publicacao dados) {
        Publicacao publicacao = new Publicacao();
        publicacao.setIdUsuario(obterUsuario(idUsuario));
        publicacao.setNome(dados.getNome().trim());
        publicacao.setDescricao(dados.getDescricao().trim());
        publicacao.setData(dados.getData());
        publicacao.setStatusOcorrencia(dados.getStatusOcorrencia());
        publicacao.setStatusPublicacao(StatusPublicacao.ATIVA);
        Publicacao salva = publicacaoRepository.save(publicacao);
        logsService.registrar(idUsuario, "PUBLICACAO_CRIADA:" + salva.getId());
        return salva;
    }

    @Transactional(readOnly = true)
    public Page<Publicacao> listar(
            String termo,
            StatusOcorrencia ocorrencia,
            StatusPublicacao status,
            Pageable pageable) {
        String termoNormalizado = termo == null || termo.isBlank()
                ? null
                : termo.trim().toLowerCase(Locale.ROOT);

        Specification<Publicacao> filtro = (root, query, builder) -> builder.conjunction();
        if (termoNormalizado != null) {
            filtro = filtro.and((root, query, builder) -> builder.or(
                    builder.like(builder.lower(root.get("nome")), "%" + termoNormalizado + "%"),
                    builder.like(builder.lower(root.get("descricao")), "%" + termoNormalizado + "%")));
        }
        if (ocorrencia != null) {
            filtro = filtro.and((root, query, builder) ->
                    builder.equal(root.get("statusOcorrencia"), ocorrencia));
        }
        if (status != null) {
            filtro = filtro.and((root, query, builder) ->
                    builder.equal(root.get("statusPublicacao"), status));
        }
        return publicacaoRepository.findAll(filtro, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Publicacao> listarDoUsuario(Long idUsuario, Pageable pageable) {
        return publicacaoRepository.findAll(
                (root, query, builder) -> builder.equal(root.get("idUsuario").get("id"), idUsuario),
                pageable);
    }

    @Transactional(readOnly = true)
    public Publicacao buscarPorId(Integer id) {
        return publicacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Publicação não encontrada: " + id));
    }

    @Transactional
    public Publicacao atualizar(Integer id, Long idAutorDaAcao, Publicacao dados) {
        Publicacao publicacao = buscarPorId(id);
        exigirAtiva(publicacao);
        publicacao.setNome(dados.getNome().trim());
        publicacao.setDescricao(dados.getDescricao().trim());
        publicacao.setData(dados.getData());
        publicacao.setStatusOcorrencia(dados.getStatusOcorrencia());
        Publicacao salva = publicacaoRepository.save(publicacao);
        logsService.registrar(idAutorDaAcao, "PUBLICACAO_ATUALIZADA:" + id);
        return salva;
    }

    @Transactional
    public Publicacao cancelar(Integer id, Long idAutorDaAcao) {
        Publicacao publicacao = buscarPorId(id);
        exigirAtiva(publicacao);
        publicacao.setStatusPublicacao(StatusPublicacao.CANCELADA);
        Publicacao salva = publicacaoRepository.save(publicacao);
        logsService.registrar(idAutorDaAcao, "PUBLICACAO_CANCELADA:" + id);
        return salva;
    }

    @Transactional
    public Publicacao finalizar(Integer id, Long idAutorDaAcao) {
        Publicacao publicacao = buscarPorId(id);
        exigirAtiva(publicacao);
        publicacao.setStatusPublicacao(StatusPublicacao.FINALIZADA);
        Publicacao salva = publicacaoRepository.save(publicacao);
        logsService.registrar(idAutorDaAcao, "PUBLICACAO_FINALIZADA:" + id);
        return salva;
    }

    @Transactional(readOnly = true)
    public long contar(StatusPublicacao status) {
        return publicacaoRepository.countByStatusPublicacao(status);
    }

    private Usuario obterUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + idUsuario));
    }

    private void exigirAtiva(Publicacao publicacao) {
        if (publicacao.getStatusPublicacao() != StatusPublicacao.ATIVA) {
            throw new IllegalStateException("Publicações finalizadas ou canceladas não podem ser alteradas.");
        }
    }
}
