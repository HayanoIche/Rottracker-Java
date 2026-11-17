package br.com.fiap.rottracker.controller.dto.convite;

import java.time.LocalDate;

public record ConviteResponseDto(
        int id,
        String token,
        String statusConvite,
        String emailConvidado,
        LocalDate dataCriacao,
        LocalDate dataExpiracao,
        int despensaId,
        String despensaNome,
        int usuarioId,
        String usuarioNome
) {}
