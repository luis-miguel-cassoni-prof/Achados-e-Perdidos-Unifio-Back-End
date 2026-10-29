package com.projeto.achados_e_perdidos.model;

import com.projeto.achados_e_perdidos.model.enums.NivelAcesso;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
public class Usuario {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, length = 50)
    private String nomeUsuario;

    @Column (nullable = false, unique = true, length = 255)
    private String email;

    @Column (nullable = false, length = 300)
    private String hashSenha;

    @Column (nullable = false, length = 1024)
    private String urlFotoPerfil;

    @Column (nullable = false, length= 30)
    private String telefone;

    @Column (nullable = false)
    private boolean bloqueado = false;

    @Enumerated (EnumType.STRING)
    private NivelAcesso nivelDeAcesso;
}
