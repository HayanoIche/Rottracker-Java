package br.com.fiap.rottracker.controller.dto.auth;

public record LoginRequest(
        String email,
        String senha
) {}
