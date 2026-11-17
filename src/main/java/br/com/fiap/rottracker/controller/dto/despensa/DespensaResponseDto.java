package br.com.fiap.rottracker.controller.dto.despensa;

import java.time.LocalDate;

public record DespensaResponseDto(
        int id,
        String nome,
        String tipo,
        LocalDate dataCriacao
) {}
