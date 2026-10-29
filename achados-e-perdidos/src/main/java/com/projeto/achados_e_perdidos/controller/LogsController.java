package com.projeto.achados_e_perdidos.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.achados_e_perdidos.controller.dto.LogResponse;
import com.projeto.achados_e_perdidos.security.UsuarioAuthorizationService;
import com.projeto.achados_e_perdidos.service.LogsService;

@RestController
@RequestMapping("/logs")
public class LogsController {

    private final LogsService logsService;
    private final UsuarioAuthorizationService authorizationService;

    public LogsController(LogsService logsService, UsuarioAuthorizationService authorizationService) {
        this.logsService = logsService;
        this.authorizationService = authorizationService;
    }

    @GetMapping
    public Page<LogResponse> listar(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        authorizationService.exigirAdministrador(authentication);
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("A página deve ser >= 0 e o tamanho deve estar entre 1 e 100.");
        }
        Long idAdministrador = authorizationService.obterIdAutenticado(authentication);
        Page<LogResponse> logs = logsService.listar(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "data")))
                .map(LogResponse::de);
        logsService.registrar(idAdministrador, "LOGS_CONSULTADOS");
        return logs;
    }
}
