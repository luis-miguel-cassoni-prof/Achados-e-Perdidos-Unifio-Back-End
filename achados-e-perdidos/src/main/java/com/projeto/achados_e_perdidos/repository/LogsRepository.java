package com.projeto.achados_e_perdidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.projeto.achados_e_perdidos.model.Logs;

public interface LogsRepository extends JpaRepository<Logs, Integer> {

    @Modifying
    @Query("update Logs l set l.idUsuario = null where l.idUsuario.id = :idUsuario")
    int desvincularUsuario(@Param("idUsuario") Long idUsuario);
}
