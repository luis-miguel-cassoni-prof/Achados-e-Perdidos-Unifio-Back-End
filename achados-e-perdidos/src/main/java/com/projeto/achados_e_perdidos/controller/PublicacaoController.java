package com.projeto.achados_e_perdidos.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.achados_e_perdidos.controller.dto.PublicacaoRequest;
import com.projeto.achados_e_perdidos.controller.dto.PublicacaoResponse;
import com.projeto.achados_e_perdidos.model.Publicacao;
import com.projeto.achados_e_perdidos.model.enums.StatusOcorrencia;
import com.projeto.achados_e_perdidos.model.enums.StatusPublicacao;
import com.projeto.achados_e_perdidos.security.UsuarioAuthorizationService;
import com.projeto.achados_e_perdidos.service.LogsService;
import com.projeto.achados_e_perdidos.service.PublicacaoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/publicacoes")
@Validated
public class PublicacaoController {

    private static final int TAMANHO_MAXIMO_PAGINA = 100;

    private final PublicacaoService publicacaoService;
    private final UsuarioAuthorizationService authorizationService;
    private final LogsService logsService;

    public PublicacaoController(
            PublicacaoService publicacaoService,
            UsuarioAuthorizationService authorizationService,
            LogsService logsService) {
        this.publicacaoService = publicacaoService;
        this.authorizationService = authorizationService;
        this.logsService = logsService;
    }

    @PostMapping
    public ResponseEntity<PublicacaoResponse> criar(
            @Valid @RequestBody PublicacaoRequest request,
            Authentication authentication) {
        Long idUsuario = authorizationService.obterIdAutenticado(authentication);
        Publicacao salva = publicacaoService.criar(idUsuario, converter(request));
        return ResponseEntity.created(URI.create("/publicacoes/" + salva.getId()))
                .body(PublicacaoResponse.de(salva));
    }

    @GetMapping
    public Page<PublicacaoResponse> listar(
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) StatusOcorrencia ocorrencia,
            @RequestParam(required = false) StatusPublicacao status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        Long idUsuario = authorizationService.obterIdAutenticado(authentication);
        Page<PublicacaoResponse> resultados = publicacaoService.listar(termo, ocorrencia, status, criarPageable(page, size))
                .map(PublicacaoResponse::de);
        logsService.registrar(idUsuario, "PUBLICACOES_CONSULTADAS");
        return resultados;
    }

    @GetMapping("/minhas")
    public Page<PublicacaoResponse> listarMinhas(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long idUsuario = authorizationService.obterIdAutenticado(authentication);
        Page<PublicacaoResponse> resultados = publicacaoService.listarDoUsuario(idUsuario, criarPageable(page, size))
                .map(PublicacaoResponse::de);
        logsService.registrar(idUsuario, "PUBLICACOES_PROPRIAS_CONSULTADAS");
        return resultados;
    }

    @GetMapping("/{id}")
    public PublicacaoResponse buscar(@PathVariable Integer id, Authentication authentication) {
        Long idUsuario = authorizationService.obterIdAutenticado(authentication);
        PublicacaoResponse publicacao = PublicacaoResponse.de(publicacaoService.buscarPorId(id));
        logsService.registrar(idUsuario, "PUBLICACAO_CONSULTADA:" + id);
        return publicacao;
    }

    @PutMapping("/{id}")
    public PublicacaoResponse atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody PublicacaoRequest request,
            Authentication authentication) {
        Long idAutorDaAcao = authorizationService.obterIdAutenticado(authentication);
        Publicacao existente = publicacaoService.buscarPorId(id);
        autorizarPublicacao(authentication, existente);
        return PublicacaoResponse.de(publicacaoService.atualizar(id, idAutorDaAcao, converter(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Integer id, Authentication authentication) {
        Long idAutorDaAcao = authorizationService.obterIdAutenticado(authentication);
        Publicacao existente = publicacaoService.buscarPorId(id);
        autorizarPublicacao(authentication, existente);
        publicacaoService.cancelar(id, idAutorDaAcao);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/finalizar")
    public PublicacaoResponse finalizar(@PathVariable Integer id, Authentication authentication) {
        Long idAutorDaAcao = authorizationService.obterIdAutenticado(authentication);
        Publicacao existente = publicacaoService.buscarPorId(id);
        autorizarPublicacao(authentication, existente);
        return PublicacaoResponse.de(publicacaoService.finalizar(id, idAutorDaAcao));
    }

    private void autorizarPublicacao(Authentication authentication, Publicacao publicacao) {
        if (publicacao.getIdUsuario() == null) {
            authorizationService.exigirAdministrador(authentication);
        } else {
            authorizationService.exigirAcesso(authentication, publicacao.getIdUsuario().getId());
        }
    }

    private Publicacao converter(PublicacaoRequest request) {
        Publicacao publicacao = new Publicacao();
        publicacao.setNome(request.nome());
        publicacao.setDescricao(request.descricao());
        publicacao.setData(request.data());
        publicacao.setStatusOcorrencia(request.statusOcorrencia());
        return publicacao;
    }

    private PageRequest criarPageable(int page, int size) {
        if (page < 0 || size < 1 || size > TAMANHO_MAXIMO_PAGINA) {
            throw new IllegalArgumentException("A página deve ser >= 0 e o tamanho deve estar entre 1 e 100.");
        }
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "data"));
    }
}
