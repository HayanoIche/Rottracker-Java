package br.com.fiap.rottracker.controller.dto.despensa;

import br.com.fiap.rottracker.model.Despensa;

public class DespensaMapper {

    // Converte Request DTO para Model
    public static Despensa toModel(DespensaRequestDto dto) {
        Despensa despensa = new Despensa();

        despensa.setNome(dto.nome());
        despensa.setTipo(dto.tipo());

        return despensa;
    }

    // Converte Model para Response DTO
    public static DespensaResponseDto toResponseDto(Despensa despensa) {
        return new DespensaResponseDto(
                despensa.getId(),
                despensa.getNome(),
                despensa.getTipo(),
                despensa.getDataCriacao()
        );
    }
}

