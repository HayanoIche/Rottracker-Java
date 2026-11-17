package br.com.fiap.rottracker.controller.dto.despensa;

public record DespensaRequestDto(
        String nome,
        String tipo,
        int usuarioId
) {}
