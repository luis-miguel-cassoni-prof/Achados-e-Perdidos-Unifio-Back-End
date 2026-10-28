package com.projeto.achados_e_perdidos.model;

import java.time.LocalDateTime;

import com.projeto.achados_e_perdidos.model.enums.StatusOcorrencia;
import com.projeto.achados_e_perdidos.model.enums.StatusPublicacao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
public class Publicacao {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Usuario idUsuario;

    @Column (nullable = false, length = 50)
    private String nome;

    @Column (nullable = false, length = 300)
    private String descricao;

    private LocalDateTime data;

    @Enumerated (EnumType.STRING)
    private StatusOcorrencia statusOcorrencia;

    @Enumerated (EnumType.STRING)
    private StatusPublicacao statusPublicacao;
}
