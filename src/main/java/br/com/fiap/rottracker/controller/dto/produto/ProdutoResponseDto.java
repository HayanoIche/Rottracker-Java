package br.com.fiap.rottracker.controller.dto.produto;

import java.time.LocalDate;

public record ProdutoResponseDto(
        int id,
        String descricao,
        double quantidade,
        String unidade,
        LocalDate dataCompra,
        LocalDate validade,
        boolean congelado,
        int categoriaId,
        String categoriaNome
) {}