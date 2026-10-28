package com.projeto.achados_e_perdidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projeto.achados_e_perdidos.model.Logs;

public interface LogsRepository extends JpaRepository<Logs, Integer> {

}
