package br.com.fiap.rottracker.controller;

import br.com.fiap.rottracker.exception.ErrorResponse;
import br.com.fiap.rottracker.controller.dto.produto.ProdutoMapper;
import br.com.fiap.rottracker.controller.dto.produto.ProdutoRequestDto;
import br.com.fiap.rottracker.controller.dto.produto.ProdutoResponseDto;
import br.com.fiap.rottracker.exception.BadRequestException;
import br.com.fiap.rottracker.exception.ConflictException;
import br.com.fiap.rottracker.exception.NotFoundException;
import br.com.fiap.rottracker.model.Categoria;
import br.com.fiap.rottracker.model.Produto;
import br.com.fiap.rottracker.service.CategoriaService;
import br.com.fiap.rottracker.service.ProdutoService;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/produtos")
public class ProdutoResource {

    private final ProdutoService service;
    private final CategoriaService categoriaService;

    public ProdutoResource(
            ProdutoService service,
            CategoriaService categoriaService
    ) {
        this.service = service;
        this.categoriaService = categoriaService;
    }

    // Criar produto em uma despensa
    @PostMapping("/despensa/{despensaId}")
    public ResponseEntity<?> create(
            @PathVariable int despensaId,
            @RequestBody ProdutoRequestDto dto
    ) {
        try {
            Categoria categoria = categoriaService.read(dto.categoriaId());

            Produto produto = ProdutoMapper.toModel(dto, categoria);

            Produto salvo = service.create(produto, despensaId);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ProdutoMapper.toResponseDto(salvo));

        } catch (RuntimeException e) {
            return tratarErro(
                    e,
                    "/api/v1/produtos/despensa/" + despensaId
            );
        }
    }

    // Buscar produto por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> read(@PathVariable int id) {
        try {
            Produto produto = service.read(id);

            return ResponseEntity.ok(
                    ProdutoMapper.toResponseDto(produto)
            );

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/produtos/" + id);
        }
    }

    // Listar todos os produtos
    @GetMapping
    public ResponseEntity<?> readAll() {
        try {
            List<ProdutoResponseDto> produtos = service.readAll()
                    .stream()
                    .map(ProdutoMapper::toResponseDto)
                    .toList();

            return ResponseEntity.ok(produtos);

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/produtos");
        }
    }

    // Listar produtos de uma despensa
    @GetMapping("/despensa/{despensaId}")
    public ResponseEntity<?> readByDespensa(
            @PathVariable int despensaId
    ) {
        try {
            List<ProdutoResponseDto> produtos = service.readByDespensa(despensaId)
                    .stream()
                    .map(ProdutoMapper::toResponseDto)
                    .toList();

            return ResponseEntity.ok(produtos);

        } catch (RuntimeException e) {
            return tratarErro(
                    e,
                    "/api/v1/produtos/despensa/" + despensaId
            );
        }
    }

    // Buscar produtos por nome
    @GetMapping("/nome")
    public ResponseEntity<?> readByName(@RequestParam String nome) {
        try {
            List<ProdutoResponseDto> produtos = service.readByName(nome).stream().map(ProdutoMapper::toResponseDto).toList();

            return ResponseEntity.ok(produtos);

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/produtos/nome");
        }
    }

    // Atualizar produto
    @PutMapping("/{id}/despensa/{despensaId}")
    public ResponseEntity<?> update(
            @PathVariable int id,
            @PathVariable int despensaId,
            @RequestBody ProdutoRequestDto dto
    ) {
        try {
            Categoria categoria = categoriaService.read(dto.categoriaId());

            Produto produto = ProdutoMapper.toModel(dto, categoria);
            produto.setId(id);

            Produto atualizado = service.update(produto, despensaId);

            return ResponseEntity.ok(
                    ProdutoMapper.toResponseDto(atualizado)
            );

        } catch (RuntimeException e) {
            return tratarErro(
                    e,
                    "/api/v1/produtos/" + id + "/despensa/" + despensaId
            );
        }
    }

    // Excluir produto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            service.delete(id);

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/produtos/" + id);
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
