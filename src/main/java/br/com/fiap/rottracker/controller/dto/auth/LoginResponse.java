package br.com.fiap.rottracker.controller.dto.auth;

public record LoginResponse(
        Integer id,
        String nome,
        String email,
        String status
) {}
