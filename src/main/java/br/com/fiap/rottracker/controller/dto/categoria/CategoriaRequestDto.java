package br.com.fiap.rottracker.controller.dto.categoria;

public record CategoriaRequestDto(
        String nome,
        boolean perecivel
) {}
