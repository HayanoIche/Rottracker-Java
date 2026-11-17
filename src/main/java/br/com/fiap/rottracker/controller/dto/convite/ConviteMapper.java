package br.com.fiap.rottracker.controller.dto.convite;

import br.com.fiap.rottracker.model.Convite;
import br.com.fiap.rottracker.model.Despensa;
import br.com.fiap.rottracker.model.Usuario;

public class ConviteMapper {

    // Converte Request DTO para Model
    public static Convite toModel(
            ConviteRequestDto dto,
            Despensa despensa,
            Usuario usuario
    ) {
        Convite convite = new Convite();

        convite.setEmailConvidado(dto.emailConvidado());
        convite.setDespensa(despensa);
        convite.setUsuario(usuario);

        return convite;
    }

    // Converte Model para Response DTO
    public static ConviteResponseDto toResponseDto(Convite convite) {
        return new ConviteResponseDto(
                convite.getId(),
                convite.getToken(),
                convite.getStatusConvite(),
                convite.getEmailConvidado(),
                convite.getDataCriacao(),
                convite.getDataExpiracao(),
                convite.getDespensa().getId(),
                convite.getDespensa().getNome(),
                convite.getUsuario().getId(),
                convite.getUsuario().getNome()
        );
    }
}
