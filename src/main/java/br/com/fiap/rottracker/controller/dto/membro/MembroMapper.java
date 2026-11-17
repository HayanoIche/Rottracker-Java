package br.com.fiap.rottracker.controller.dto.membro;

import br.com.fiap.rottracker.model.Membro;
import br.com.fiap.rottracker.model.Usuario;
import br.com.fiap.rottracker.model.Despensa;

public class MembroMapper {

    // Converte Request DTO para Model
    public static Membro toModel(
            MembroRequestDto dto,
            Usuario usuario,
            Despensa despensa
    ) {
        Membro membro = new Membro();

        membro.setUsuario(usuario);
        membro.setDespensa(despensa);
        membro.setPermissao(dto.permissao());
        membro.setDataEntrada(dto.dataEntrada());

        return membro;
    }

    // Converte Model para Response DTO
    public static MembroResponseDto toResponseDto(Membro membro) {
        return new MembroResponseDto(
                membro.getId(),
                membro.getUsuario().getId(),
                membro.getUsuario().getNome(),
                membro.getDespensa().getId(),
                membro.getDespensa().getNome(),
                membro.getPermissao(),
                membro.getDataEntrada()
        );
    }
}
