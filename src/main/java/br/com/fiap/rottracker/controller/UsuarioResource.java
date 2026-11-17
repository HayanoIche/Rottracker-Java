package br.com.fiap.rottracker.controller;

import br.com.fiap.rottracker.exception.ErrorResponse;
import br.com.fiap.rottracker.controller.dto.usuario.UsuarioMapper;
import br.com.fiap.rottracker.controller.dto.usuario.UsuarioRequestDto;
import br.com.fiap.rottracker.controller.dto.usuario.UsuarioResponseDto;
import br.com.fiap.rottracker.exception.BadRequestException;
import br.com.fiap.rottracker.exception.ConflictException;
import br.com.fiap.rottracker.exception.NotFoundException;
import br.com.fiap.rottracker.model.Usuario;
import br.com.fiap.rottracker.service.UsuarioService;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioResource {

    private final UsuarioService service;

    public UsuarioResource(UsuarioService service) {
        this.service = service;
    }

    // Criar usuário
    @PostMapping
    public ResponseEntity<?> create(@RequestBody UsuarioRequestDto dto) {
        try {
            Usuario usuario = UsuarioMapper.toModel(dto);
            Usuario salvo = service.create(usuario);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(UsuarioMapper.toResponseDto(salvo));

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/usuarios");
        }
    }

    // Buscar usuário por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> read(@PathVariable int id) {
        try {
            Usuario usuario = service.read(id);

            return ResponseEntity.ok(
                    UsuarioMapper.toResponseDto(usuario)
            );

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/usuarios/" + id);
        }
    }

    // Listar todos os usuários
    @GetMapping
    public ResponseEntity<?> readAll() {
        try {
            List<UsuarioResponseDto> usuarios = service.readAll()
                    .stream()
                    .map(UsuarioMapper::toResponseDto)
                    .toList();

            return ResponseEntity.ok(usuarios);

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/usuarios");
        }
    }

    // Buscar usuário por email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> readByEmail(@PathVariable String email) {
        try {
            Usuario usuario = service.readByEmail(email);

            return ResponseEntity.ok(
                    UsuarioMapper.toResponseDto(usuario)
            );

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/usuarios/email/" + email);
        }
    }

    // Buscar usuários por nome
    @GetMapping("/nome")
    public ResponseEntity<?> readByName(@RequestParam String nome) {
        try {
            List<UsuarioResponseDto> usuarios = service.readByName(nome)
                    .stream()
                    .map(UsuarioMapper::toResponseDto)
                    .toList();

            return ResponseEntity.ok(usuarios);

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/usuarios/nome");
        }
    }

    // Atualizar usuário
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable int id,
            @RequestBody UsuarioRequestDto dto
    ) {
        try {
            Usuario usuario = UsuarioMapper.toModel(dto);
            usuario.setId(id);

            Usuario atualizado = service.update(usuario);

            return ResponseEntity.ok(
                    UsuarioMapper.toResponseDto(atualizado)
            );

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/usuarios/" + id);
        }
    }

    // Tratamento de erros
    private ResponseEntity<ErrorResponse> tratarErro(
            RuntimeException e,
            String path
    ) {
        HttpStatus status;
        String mensagem = e.getMessage();

        if (e instanceof BadRequestException) {
            status = HttpStatus.BAD_REQUEST;

        } else if (e instanceof NotFoundException) {
            status = HttpStatus.NOT_FOUND;

        } else if (e instanceof ConflictException) {
            status = HttpStatus.CONFLICT;

        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            mensagem = "Erro interno no servidor.";
        }

        ErrorResponse error = new ErrorResponse(
                Instant.now(),
                status.value(),
                mensagem,
                path
        );

        return ResponseEntity.status(status).body(error);
    }
}

