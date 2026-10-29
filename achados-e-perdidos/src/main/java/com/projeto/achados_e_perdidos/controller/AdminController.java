package com.projeto.achados_e_perdidos.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.achados_e_perdidos.controller.dto.DashboardResponse;
import com.projeto.achados_e_perdidos.model.enums.StatusPublicacao;
import com.projeto.achados_e_perdidos.security.UsuarioAuthorizationService;
import com.projeto.achados_e_perdidos.service.LogsService;
import com.projeto.achados_e_perdidos.service.PublicacaoService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final PublicacaoService publicacaoService;
    private final UsuarioAuthorizationService authorizationService;
    private final LogsService logsService;

    public AdminController(
            PublicacaoService publicacaoService,
            UsuarioAuthorizationService authorizationService,
            LogsService logsService) {
        this.publicacaoService = publicacaoService;
        this.authorizationService = authorizationService;
        this.logsService = logsService;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard(Authentication authentication) {
        authorizationService.exigirAdministrador(authentication);
        Long idAdministrador = authorizationService.obterIdAutenticado(authentication);
        DashboardResponse dashboard = new DashboardResponse(
                publicacaoService.contar(StatusPublicacao.ATIVA),
                publicacaoService.contar(StatusPublicacao.FINALIZADA),
                publicacaoService.contar(StatusPublicacao.CANCELADA));
        logsService.registrar(idAdministrador, "DASHBOARD_CONSULTADO");
        return dashboard;
    }
}
