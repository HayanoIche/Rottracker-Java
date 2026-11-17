package br.com.fiap.rottracker.controller.dto.convite;

public record ConviteRequestDto(
        String emailConvidado,
        int despensaId,
        int usuarioId
) {}
