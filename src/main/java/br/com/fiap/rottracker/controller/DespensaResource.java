package br.com.fiap.rottracker.controller;

import br.com.fiap.rottracker.exception.ErrorResponse;
import br.com.fiap.rottracker.controller.dto.despensa.DespensaMapper;
import br.com.fiap.rottracker.controller.dto.despensa.DespensaRequestDto;
import br.com.fiap.rottracker.controller.dto.despensa.DespensaResponseDto;
import br.com.fiap.rottracker.exception.BadRequestException;
import br.com.fiap.rottracker.exception.ConflictException;
import br.com.fiap.rottracker.exception.NotFoundException;
import br.com.fiap.rottracker.model.Despensa;
import br.com.fiap.rottracker.service.DespensaService;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/despensas")
public class DespensaResource {

    private final DespensaService service;

    public DespensaResource(DespensaService service) {
        this.service = service;
    }

    // Criar despensa
    @PostMapping
    public ResponseEntity<?> create(@RequestBody DespensaRequestDto dto) {
        try {
            Despensa despensa = DespensaMapper.toModel(dto);

            Despensa salva = service.create(
                    despensa,
                    dto.usuarioId()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(DespensaMapper.toResponseDto(salva));

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/despensas");
        }
    }

    // Buscar despensa por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> read(@PathVariable int id) {
        try {
            Despensa despensa = service.read(id);

            return ResponseEntity.ok(
                    DespensaMapper.toResponseDto(despensa)
            );

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/despensas/" + id);
        }
    }

    // Listar todas as despensas
    @GetMapping
    public ResponseEntity<?> readAll() {
        try {
            List<DespensaResponseDto> despensas = service.readAll()
                    .stream()
                    .map(DespensaMapper::toResponseDto)
                    .toList();

            return ResponseEntity.ok(despensas);

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/despensas");
        }
    }

    // Buscar despensas de um usuário
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> readByUsuario(
            @PathVariable int usuarioId
    ) {
        try {
            List<DespensaResponseDto> despensas = service.readByUsuario(usuarioId)
                    .stream()
                    .map(DespensaMapper::toResponseDto)
                    .toList();

            return ResponseEntity.ok(despensas);

        } catch (RuntimeException e) {
            return tratarErro(
                    e,
                    "/api/v1/despensas/usuario/" + usuarioId
            );
        }
    }

    // Atualizar despensa
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable int id,
            @RequestBody DespensaRequestDto dto
    ) {
        try {
            Despensa despensa = DespensaMapper.toModel(dto);
            despensa.setId(id);

            Despensa atualizada = service.update(despensa);

            return ResponseEntity.ok(
                    DespensaMapper.toResponseDto(atualizada)
            );

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/despensas/" + id);
        }
    }

    // Excluir despensa
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            service.delete(id);

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/despensas/" + id);
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
