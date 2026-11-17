package br.com.fiap.rottracker.controller.dto.usuario;

import br.com.fiap.rottracker.model.Usuario;

public class UsuarioMapper
{
    // Converte Request DTO para Model
    public static Usuario toModel(UsuarioRequestDto dto) {
        Usuario usuario = new Usuario();

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());
        usuario.setStatus(dto.status());

        return usuario;
    }

    // Converte Model para Response DTO
    public static UsuarioResponseDto toResponseDto(Usuario usuario) {
        return new UsuarioResponseDto(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getStatus()
        );
    }
}
