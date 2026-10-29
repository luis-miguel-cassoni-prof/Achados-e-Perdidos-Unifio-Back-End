package com.projeto.achados_e_perdidos.controller.dto;

import java.time.LocalDateTime;

import com.projeto.achados_e_perdidos.model.enums.StatusOcorrencia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PublicacaoRequest(
        @NotBlank @Size(max = 50) String nome,
        @NotBlank @Size(max = 300) String descricao,
        @NotNull StatusOcorrencia statusOcorrencia,
        @NotNull LocalDateTime data) {
}
