package com.projeto.achados_e_perdidos.controller.dto;

import com.projeto.achados_e_perdidos.model.Usuario;
import com.projeto.achados_e_perdidos.model.enums.NivelAcesso;

public record UsuarioResponse(
        Long id,
        String nomeUsuario,
        String email,
        String urlFotoPerfil,
        String telefone,
        boolean bloqueado,
        NivelAcesso nivelDeAcesso) {

    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNomeUsuario(),
                usuario.getEmail(),
                usuario.getUrlFotoPerfil(),
                usuario.getTelefone(),
                usuario.isBloqueado(),
                usuario.getNivelDeAcesso());
    }
}
