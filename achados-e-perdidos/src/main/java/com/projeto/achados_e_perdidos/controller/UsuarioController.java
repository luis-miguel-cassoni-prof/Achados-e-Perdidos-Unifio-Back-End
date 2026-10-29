package com.projeto.achados_e_perdidos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

import com.projeto.achados_e_perdidos.controller.dto.AtualizarUsuarioRequest;
import com.projeto.achados_e_perdidos.controller.dto.CadastroUsuarioRequest;
import com.projeto.achados_e_perdidos.controller.dto.LoginRequest;
import com.projeto.achados_e_perdidos.controller.dto.LoginResponse;
import com.projeto.achados_e_perdidos.controller.dto.ModeracaoUsuarioRequest;
import com.projeto.achados_e_perdidos.controller.dto.UsuarioResponse;
import com.projeto.achados_e_perdidos.model.Usuario;
import com.projeto.achados_e_perdidos.security.JwtService;
import com.projeto.achados_e_perdidos.security.UsuarioAuthorizationService;
import com.projeto.achados_e_perdidos.service.LogsService;
import com.projeto.achados_e_perdidos.service.UsuarioService;

@RestController
@RequestMapping ("/usuario")
@Validated
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final UsuarioAuthorizationService authorizationService;
    private final LogsService logsService;

    public UsuarioController(
            UsuarioService usuarioService,
            JwtService jwtService,
            UsuarioAuthorizationService authorizationService,
            LogsService logsService) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.authorizationService = authorizationService;
        this.logsService = logsService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody CadastroUsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNomeUsuario(request.nomeUsuario());
        usuario.setEmail(request.email());
        usuario.setHashSenha(request.senha());
        usuario.setUrlFotoPerfil(request.urlFotoPerfil());
        usuario.setTelefone(request.telefone());
        Usuario cadastrado = usuarioService.cadastrarUsuario(usuario);
        logsService.registrar(cadastrado.getId(), "USUARIO_CADASTRADO");
        return ResponseEntity.created(URI.create("/usuario/" + cadastrado.getId()))
                .body(UsuarioResponse.de(cadastrado));
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioService.autenticar(request.email(), request.senha());
        logsService.registrar(usuario.getId(), "LOGIN_REALIZADO");
        return new LoginResponse(
                jwtService.criarToken(usuario),
                "Bearer",
                jwtService.getExpirationSeconds(),
                UsuarioResponse.de(usuario));
    }

    @GetMapping
    public List<UsuarioResponse> listar(Authentication authentication) {
        authorizationService.exigirAdministrador(authentication);
        Long idAutorDaAcao = authorizationService.obterIdAutenticado(authentication);
        var usuarios = usuarioService.listarUsuarios().stream().map(UsuarioResponse::de).toList();
        logsService.registrar(idAutorDaAcao, "USUARIOS_LISTADOS");
        return usuarios;
    }

    @GetMapping("/me")
    public UsuarioResponse obterUsuarioAutenticado(Authentication authentication) {
        Long id = authorizationService.obterIdAutenticado(authentication);
        UsuarioResponse usuario = UsuarioResponse.de(usuarioService.buscarUsuarioPorId(id));
        logsService.registrar(id, "PERFIL_CONSULTADO");
        return usuario;
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable Long id, Authentication authentication) {
        authorizationService.exigirAcesso(authentication, id);
        Long idAutorDaAcao = authorizationService.obterIdAutenticado(authentication);
        UsuarioResponse usuario = UsuarioResponse.de(usuarioService.buscarUsuarioPorId(id));
        logsService.registrar(idAutorDaAcao, "USUARIO_CONSULTADO:" + id);
        return usuario;
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarUsuarioRequest request,
            Authentication authentication) {
        authorizationService.exigirAcesso(authentication, id);
        Usuario usuario = new Usuario();
        usuario.setNomeUsuario(request.nomeUsuario());
        usuario.setEmail(request.email());
        usuario.setHashSenha(request.senha());
        usuario.setUrlFotoPerfil(request.urlFotoPerfil());
        usuario.setTelefone(request.telefone());
        UsuarioResponse atualizado = UsuarioResponse.de(usuarioService.atualizarUsuario(id, usuario));
        logsService.registrar(authorizationService.obterIdAutenticado(authentication), "USUARIO_ATUALIZADO:" + id);
        return atualizado;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, Authentication authentication) {
        authorizationService.exigirAcesso(authentication, id);
        Long idAutorDaAcao = authorizationService.obterIdAutenticado(authentication);
        usuarioService.deletarUsuario(id);
        logsService.registrar(idAutorDaAcao.equals(id) ? null : idAutorDaAcao, "USUARIO_EXCLUIDO:" + id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/bloqueio")
    public UsuarioResponse alterarBloqueio(
            @PathVariable Long id,
            @RequestBody ModeracaoUsuarioRequest request,
            Authentication authentication) {
        authorizationService.exigirAdministrador(authentication);
        Long idAdministrador = authorizationService.obterIdAutenticado(authentication);
        Usuario usuario = usuarioService.alterarBloqueio(id, request.bloqueado());
        logsService.registrar(idAdministrador,
                (request.bloqueado() ? "USUARIO_BLOQUEADO:" : "USUARIO_DESBLOQUEADO:") + id);
        return UsuarioResponse.de(usuario);
    }
}
