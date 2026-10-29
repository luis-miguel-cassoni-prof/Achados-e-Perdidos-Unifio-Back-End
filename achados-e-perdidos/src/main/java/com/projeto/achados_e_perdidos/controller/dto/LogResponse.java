package com.projeto.achados_e_perdidos.controller.dto;

import java.time.Instant;

import com.projeto.achados_e_perdidos.model.Logs;

public record LogResponse(Integer id, Long idUsuario, String acao, Instant data) {

    public static LogResponse de(Logs log) {
        return new LogResponse(
                log.getId(),
                log.getIdUsuario() == null ? null : log.getIdUsuario().getId(),
                log.getAcao(),
                log.getData());
    }
}
