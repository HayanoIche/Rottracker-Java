package br.com.fiap.rottracker.controller;

import br.com.fiap.rottracker.exception.ErrorResponse;
import br.com.fiap.rottracker.controller.dto.categoria.CategoriaMapper;
import br.com.fiap.rottracker.controller.dto.categoria.CategoriaRequestDto;
import br.com.fiap.rottracker.controller.dto.categoria.CategoriaResponseDto;
import br.com.fiap.rottracker.exception.BadRequestException;
import br.com.fiap.rottracker.exception.ConflictException;
import br.com.fiap.rottracker.exception.NotFoundException;
import br.com.fiap.rottracker.model.Categoria;
import br.com.fiap.rottracker.service.CategoriaService;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaResource {

    private final CategoriaService service;

    public CategoriaResource(CategoriaService service) {
        this.service = service;
    }

    // Criar categoria
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CategoriaRequestDto dto) {
        try {
            Categoria categoria = CategoriaMapper.toModel(dto);
            Categoria salva = service.create(categoria);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(CategoriaMapper.toResponseDto(salva));

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/categorias");
        }
    }

    // Buscar categoria por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> read(@PathVariable int id) {
        try {
            Categoria categoria = service.read(id);

            return ResponseEntity.ok(
                    CategoriaMapper.toResponseDto(categoria)
            );

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/categorias/" + id);
        }
    }

    // Listar todas as categorias
    @GetMapping
    public ResponseEntity<?> readAll() {
        try {
            List<CategoriaResponseDto> categorias = service.readAll()
                    .stream()
                    .map(CategoriaMapper::toResponseDto)
                    .toList();

            return ResponseEntity.ok(categorias);

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/categorias");
        }
    }

    // Buscar categoria por nome
    @GetMapping("/nome")
    public ResponseEntity<?> readByName(@RequestParam String nome) {
        try {
            List<Categoria> categorias = service.readByName(nome);

            return ResponseEntity.ok(
                    categorias.stream()
                            .map(CategoriaMapper::toResponseDto)
                            .toList()
            );

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/categorias/nome");
        }
    }

    // Atualizar categoria
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable int id,
            @RequestBody CategoriaRequestDto dto
    ) {
        try {
            Categoria categoria = CategoriaMapper.toModel(dto);
            categoria.setId(id);

            Categoria atualizada = service.update(categoria);

            return ResponseEntity.ok(
                    CategoriaMapper.toResponseDto(atualizada)
            );

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/categorias/" + id);
        }
    }

    // Excluir categoria
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            service.delete(id);

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/categorias/" + id);
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
