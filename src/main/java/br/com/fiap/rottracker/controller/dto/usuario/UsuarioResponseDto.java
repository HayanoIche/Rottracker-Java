package br.com.fiap.rottracker.controller.dto.usuario;

public record UsuarioResponseDto(
        int id,
        String nome,
        String email,
        String status
) {}
