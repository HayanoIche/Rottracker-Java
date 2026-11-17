package br.com.fiap.rottracker.controller;

import br.com.fiap.rottracker.exception.ErrorResponse;
import br.com.fiap.rottracker.controller.dto.membro.MembroMapper;
import br.com.fiap.rottracker.controller.dto.membro.MembroRequestDto;
import br.com.fiap.rottracker.controller.dto.membro.MembroResponseDto;
import br.com.fiap.rottracker.exception.BadRequestException;
import br.com.fiap.rottracker.exception.ConflictException;
import br.com.fiap.rottracker.exception.NotFoundException;
import br.com.fiap.rottracker.model.Despensa;
import br.com.fiap.rottracker.model.Membro;
import br.com.fiap.rottracker.model.Usuario;
import br.com.fiap.rottracker.service.DespensaService;
import br.com.fiap.rottracker.service.MembroService;
import br.com.fiap.rottracker.service.UsuarioService;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/membros")
public class MembroResource {

    private final MembroService service;
    private final UsuarioService usuarioService;
    private final DespensaService despensaService;

    public MembroResource(
            MembroService service,
            UsuarioService usuarioService,
            DespensaService despensaService
    ) {
        this.service = service;
        this.usuarioService = usuarioService;
        this.despensaService = despensaService;
    }

    // Criar membro
    @PostMapping
    public ResponseEntity<?> create(@RequestBody MembroRequestDto dto) {
        try {
            Usuario usuario = usuarioService.read(dto.usuarioId());
            Despensa despensa = despensaService.read(dto.despensaId());

            Membro membro = MembroMapper.toModel(dto, usuario, despensa);
            Membro salvo = service.create(membro);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(MembroMapper.toResponseDto(salvo));

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/membros");
        }
    }

    // Buscar membro por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> read(@PathVariable int id) {
        try {
            Membro membro = service.read(id);

            return ResponseEntity.ok(
                    MembroMapper.toResponseDto(membro)
            );

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/membros/" + id);
        }
    }

    // Listar todos os membros
    @GetMapping
    public ResponseEntity<?> readAll() {
        try {
            List<MembroResponseDto> membros = service.readAll()
                    .stream()
                    .map(MembroMapper::toResponseDto)
                    .toList();

            return ResponseEntity.ok(membros);

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/membros");
        }
    }

    // Listar membros de uma despensa
    @GetMapping("/despensa/{despensaId}")
    public ResponseEntity<?> readByDespensa(
            @PathVariable int despensaId
    ) {
        try {
            List<MembroResponseDto> membros = service.readByDespensa(despensaId)
                    .stream()
                    .map(MembroMapper::toResponseDto)
                    .toList();

            return ResponseEntity.ok(membros);

        } catch (RuntimeException e) {
            return tratarErro(
                    e,
                    "/api/v1/membros/despensa/" + despensaId
            );
        }
    }

    // Listar despensas às quais um usuário pertence
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> readByUsuario(
            @PathVariable int usuarioId
    ) {
        try {
            List<MembroResponseDto> membros = service.readByUsuario(usuarioId)
                    .stream()
                    .map(MembroMapper::toResponseDto)
                    .toList();

            return ResponseEntity.ok(membros);

        } catch (RuntimeException e) {
            return tratarErro(
                    e,
                    "/api/v1/membros/usuario/" + usuarioId
            );
        }
    }

    // Atualizar membro
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable int id,
            @RequestBody MembroRequestDto dto
    ) {
        try {
            Usuario usuario = usuarioService.read(dto.usuarioId());
            Despensa despensa = despensaService.read(dto.despensaId());

            Membro membro = MembroMapper.toModel(dto, usuario, despensa);
            membro.setId(id);

            Membro atualizado = service.update(membro);

            return ResponseEntity.ok(
                    MembroMapper.toResponseDto(atualizado)
            );

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/membros/" + id);
        }
    }

    // Excluir membro
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            service.delete(id);

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/membros/" + id);
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
