package com.projeto.achados_e_perdidos.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import com.projeto.achados_e_perdidos.model.Usuario;
import com.projeto.achados_e_perdidos.model.enums.NivelAcesso;
import com.projeto.achados_e_perdidos.repository.UsuarioRepository;

@Component
public class UsuarioAuthorizationService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAuthorizationService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void exigirAcesso(Authentication authentication, Long idUsuario) {
        Usuario usuarioAutenticado = obterUsuarioAutenticado(authentication);
        if (!idUsuario.equals(usuarioAutenticado.getId())
                && usuarioAutenticado.getNivelDeAcesso() != NivelAcesso.ADMINISTRADOR) {
            throw new AccessDeniedException("Você não tem permissão para acessar este usuário.");
        }
    }

    public void exigirAdministrador(Authentication authentication) {
        if (obterUsuarioAutenticado(authentication).getNivelDeAcesso() != NivelAcesso.ADMINISTRADOR) {
            throw new AccessDeniedException("Apenas administradores podem realizar esta operação.");
        }
    }

    public Long obterIdAutenticado(Authentication authentication) {
        return obterUsuarioAutenticado(authentication).getId();
    }

    private Usuario obterUsuarioAutenticado(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            throw new AccessDeniedException("Token de autenticação inválido.");
        }
        Long idUsuario = obterId(jwtAuthentication);
        if (idUsuario == null) {
            throw new AccessDeniedException("Token de autenticação inválido.");
        }
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new AccessDeniedException("Usuário do token não existe mais."));
        if (usuario.isBloqueado()) {
            throw new AccessDeniedException("Usuário bloqueado.");
        }
        return usuario;
    }

    private Long obterId(JwtAuthenticationToken authentication) {
        Object id = authentication.getToken().getClaim("userId");
        if (id instanceof Number number) {
            return number.longValue();
        }
        return null;
    }
}
