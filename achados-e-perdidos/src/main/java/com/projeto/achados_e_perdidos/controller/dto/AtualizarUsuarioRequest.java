package com.projeto.achados_e_perdidos.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarUsuarioRequest(
        @NotBlank @Size(max = 50) String nomeUsuario,
        @NotBlank @Email @Size(max = 255) String email,
        @Size(min = 8, max = 72) String senha,
        @NotBlank @Size(max = 1024) String urlFotoPerfil,
        @NotBlank @Size(max = 30) String telefone) {
}
