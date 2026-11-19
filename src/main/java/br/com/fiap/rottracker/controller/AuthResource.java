package br.com.fiap.rottracker.controller;

import br.com.fiap.rottracker.controller.dto.auth.LoginRequest;
import br.com.fiap.rottracker.controller.dto.auth.LoginResponse;
import br.com.fiap.rottracker.model.Usuario;
import br.com.fiap.rottracker.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthResource {

    private final UsuarioRepository usuarioRepository;

    public AuthResource(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        if (request.email() == null || request.senha() == null
                || request.email().isBlank() || request.senha().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("E-mail e senha são obrigatórios.");
        }

        Usuario usuario = usuarioRepository.readByEmail(request.email());

        if (usuario == null
                || !Objects.equals(usuario.getSenha(), request.senha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("E-mail ou senha inválidos.");
        }

        if (!"ATIVO".equalsIgnoreCase(usuario.getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Usuário inativo.");
        }

        LoginResponse response = new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getStatus()
        );

        return ResponseEntity.ok(response);
    }
}