package com.projeto.achados_e_perdidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projeto.achados_e_perdidos.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

}
