package com.projeto.achados_e_perdidos.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity 
public class Logs {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne 
    private Usuario idUsuario;

    @Column (nullable = false)
    private String acao;

    @CreationTimestamp 
    @Column (nullable = false, updatable = false)
    private Instant data;
}
