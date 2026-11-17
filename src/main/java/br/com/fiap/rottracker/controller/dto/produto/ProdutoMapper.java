package br.com.fiap.rottracker.controller.dto.produto;

import br.com.fiap.rottracker.model.Categoria;
import br.com.fiap.rottracker.model.Produto;

public class ProdutoMapper
{
    // Converte Request DTO para Model
    public static Produto toModel(
            ProdutoRequestDto dto,
            Categoria categoria
    ) {
        Produto produto = new Produto();

        produto.setDescricao(dto.descricao());
        produto.setQuantidade(dto.quantidade());
        produto.setUnidade(dto.unidade());
        produto.setDataCompra(dto.dataCompra());
        produto.setValidade(dto.validade());
        produto.setCongelado(dto.congelado());
        produto.setCategoria(categoria);

        return produto;
    }

    // Converte Model para Response DTO
    public static ProdutoResponseDto toResponseDto(Produto produto) {
        return new ProdutoResponseDto(
                produto.getId(),
                produto.getDescricao(),
                produto.getQuantidade(),
                produto.getUnidade(),
                produto.getDataCompra(),
                produto.getValidade(),
                produto.isCongelado(),
                produto.getCategoria().getId(),
                produto.getCategoria().getNome()
        );
    }
}
