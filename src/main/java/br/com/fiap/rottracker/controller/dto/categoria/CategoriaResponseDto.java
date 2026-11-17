package br.com.fiap.rottracker.controller.dto.categoria;

public record CategoriaResponseDto(
        int id,
        String nome,
        boolean perecivel
) {}
