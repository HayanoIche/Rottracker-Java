package br.com.fiap.rottracker.controller.dto.membro;

import java.time.LocalDate;

public record MembroRequestDto(
        int usuarioId,
        int despensaId,
        String permissao,
        LocalDate dataEntrada
) {}
