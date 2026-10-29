package com.projeto.achados_e_perdidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.projeto.achados_e_perdidos.model.Publicacao;
import com.projeto.achados_e_perdidos.model.enums.StatusPublicacao;

public interface PublicacaoRepository extends JpaRepository<Publicacao, Integer>, JpaSpecificationExecutor<Publicacao> {

    long countByStatusPublicacao(StatusPublicacao statusPublicacao);

    @Modifying
    @Query("update Publicacao p set p.idUsuario = null where p.idUsuario.id = :idUsuario")
    int desvincularUsuario(@Param("idUsuario") Long idUsuario);
}
