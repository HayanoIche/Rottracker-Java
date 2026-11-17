package br.com.fiap.rottracker.controller.dto.membro;

import java.time.LocalDate;

public record MembroResponseDto(
        int id,
        int usuarioId,
        String usuarioNome,
        int despensaId,
        String despensaNome,
        String permissao,
        LocalDate dataEntrada
) {}
