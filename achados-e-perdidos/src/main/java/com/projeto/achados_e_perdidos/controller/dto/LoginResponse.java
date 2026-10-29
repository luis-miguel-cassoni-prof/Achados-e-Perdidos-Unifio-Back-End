package com.projeto.achados_e_perdidos.controller.dto;

public record LoginResponse(String token, String tipo, long expiraEmSegundos, UsuarioResponse usuario) {
}
