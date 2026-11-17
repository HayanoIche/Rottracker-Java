package br.com.fiap.rottracker.controller.dto.categoria;

import br.com.fiap.rottracker.model.Categoria;

public class CategoriaMapper {

    // Converte Request DTO para Model
    public static Categoria toModel(CategoriaRequestDto dto) {
        Categoria categoria = new Categoria();

        categoria.setNome(dto.nome());
        categoria.setPerecivel(dto.perecivel());

        return categoria;
    }

    // Converte Model para Response DTO
    public static CategoriaResponseDto toResponseDto(Categoria categoria) {
        return new CategoriaResponseDto(
                categoria.getId(),
                categoria.getNome(),
                categoria.isPerecivel()
        );
    }
}
