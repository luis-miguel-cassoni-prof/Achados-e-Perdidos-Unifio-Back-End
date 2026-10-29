package com.projeto.achados_e_perdidos.controller.dto;

import java.time.LocalDateTime;

import com.projeto.achados_e_perdidos.model.Publicacao;
import com.projeto.achados_e_perdidos.model.enums.StatusOcorrencia;
import com.projeto.achados_e_perdidos.model.enums.StatusPublicacao;

public record PublicacaoResponse(
        Integer id,
        Long idUsuario,
        String nomeUsuario,
        String nome,
        String descricao,
        LocalDateTime data,
        StatusOcorrencia statusOcorrencia,
        StatusPublicacao statusPublicacao) {

    public static PublicacaoResponse de(Publicacao publicacao) {
        return new PublicacaoResponse(
                publicacao.getId(),
                publicacao.getIdUsuario() == null ? null : publicacao.getIdUsuario().getId(),
                publicacao.getIdUsuario() == null ? null : publicacao.getIdUsuario().getNomeUsuario(),
                publicacao.getNome(),
                publicacao.getDescricao(),
                publicacao.getData(),
                publicacao.getStatusOcorrencia(),
                publicacao.getStatusPublicacao());
    }
}
