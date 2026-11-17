package br.com.fiap.rottracker.controller.dto.usuario;

public record UsuarioRequestDto(
        String nome,
        String email,
        String senha,
        String status
) {}
