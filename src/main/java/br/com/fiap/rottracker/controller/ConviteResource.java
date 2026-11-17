package br.com.fiap.rottracker.controller;

import br.com.fiap.rottracker.exception.ErrorResponse;
import br.com.fiap.rottracker.controller.dto.convite.ConviteAceitarRequestDto;
import br.com.fiap.rottracker.controller.dto.convite.ConviteMapper;
import br.com.fiap.rottracker.controller.dto.convite.ConviteRequestDto;
import br.com.fiap.rottracker.controller.dto.convite.ConviteResponseDto;
import br.com.fiap.rottracker.exception.BadRequestException;
import br.com.fiap.rottracker.exception.ConflictException;
import br.com.fiap.rottracker.exception.NotFoundException;
import br.com.fiap.rottracker.model.Convite;
import br.com.fiap.rottracker.model.Despensa;
import br.com.fiap.rottracker.model.Usuario;
import br.com.fiap.rottracker.service.ConviteService;
import br.com.fiap.rottracker.service.DespensaService;
import br.com.fiap.rottracker.service.UsuarioService;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/convites")
public class ConviteResource {

    private final ConviteService service;
    private final UsuarioService usuarioService;
    private final DespensaService despensaService;

    public ConviteResource(
            ConviteService service,
            UsuarioService usuarioService,
            DespensaService despensaService
    ) {
        this.service = service;
        this.usuarioService = usuarioService;
        this.despensaService = despensaService;
    }

    // Criar convite
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ConviteRequestDto dto) {
        try {
            Usuario usuario = usuarioService.read(dto.usuarioId());
            Despensa despensa = despensaService.read(dto.despensaId());

            Convite convite = ConviteMapper.toModel(dto, despensa, usuario);
            Convite salvo = service.create(convite);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ConviteMapper.toResponseDto(salvo));

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/convites");
        }
    }

    // Buscar convite por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> read(@PathVariable int id) {
        try {
            Convite convite = service.read(id);

            return ResponseEntity.ok(
                    ConviteMapper.toResponseDto(convite)
            );

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/convites/" + id);
        }
    }

    // Listar todos os convites
    @GetMapping
    public ResponseEntity<?> readAll() {
        try {
            List<ConviteResponseDto> convites = service.readAll()
                    .stream()
                    .map(ConviteMapper::toResponseDto)
                    .toList();

            return ResponseEntity.ok(convites);

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/convites");
        }
    }

    // Buscar convite pelo token
    @GetMapping("/token/{token}")
    public ResponseEntity<?> readByToken(@PathVariable String token) {
        try {
            Convite convite = service.readByToken(token);

            return ResponseEntity.ok(
                    ConviteMapper.toResponseDto(convite)
            );

        } catch (RuntimeException e) {
            return tratarErro(
                    e,
                    "/api/v1/convites/token/" + token
            );
        }
    }

    // Listar convites de uma despensa
    @GetMapping("/despensa/{despensaId}")
    public ResponseEntity<?> readByDespensa(
            @PathVariable int despensaId
    ) {
        try {
            List<ConviteResponseDto> convites = service.readByDespensa(despensaId)
                    .stream()
                    .map(ConviteMapper::toResponseDto)
                    .toList();

            return ResponseEntity.ok(convites);

        } catch (RuntimeException e) {
            return tratarErro(
                    e,
                    "/api/v1/convites/despensa/" + despensaId
            );
        }
    }

    // Buscar convites pelo email
    @GetMapping("/email")
    public ResponseEntity<?> readByEmail(
            @RequestParam String email
    ) {
        try {
            List<ConviteResponseDto> convites = service.readByEmail(email)
                    .stream()
                    .map(ConviteMapper::toResponseDto)
                    .toList();

            return ResponseEntity.ok(convites);

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/convites/email");
        }
    }

    // Buscar convites pelo status
    @GetMapping("/status/{status}")
    public ResponseEntity<?> readByStatus(
            @PathVariable String status
    ) {
        try {
            List<ConviteResponseDto> convites = service.readByStatus(status)
                    .stream()
                    .map(ConviteMapper::toResponseDto)
                    .toList();

            return ResponseEntity.ok(convites);

        } catch (RuntimeException e) {
            return tratarErro(
                    e,
                    "/api/v1/convites/status/" + status
            );
        }
    }

    // Atualizar convite
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable int id,
            @RequestBody ConviteRequestDto dto
    ) {
        try {
            Usuario usuario = usuarioService.read(dto.usuarioId());
            Despensa despensa = despensaService.read(dto.despensaId());

            Convite convite = ConviteMapper.toModel(dto, despensa, usuario);
            convite.setId(id);

            Convite atualizado = service.update(convite);

            return ResponseEntity.ok(
                    ConviteMapper.toResponseDto(atualizado)
            );

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/convites/" + id);
        }
    }

    // Aceitar convite
    @PostMapping("/token/{token}/aceitar")
    public ResponseEntity<?> aceitar(
            @PathVariable String token,
            @RequestBody ConviteAceitarRequestDto dto
    ) {
        try {
            Convite convite = service.aceitar(token, dto.usuarioId());

            return ResponseEntity.ok(
                    ConviteMapper.toResponseDto(convite)
            );

        } catch (RuntimeException e) {
            return tratarErro(
                    e,
                    "/api/v1/convites/token/" + token + "/aceitar"
            );
        }
    }

    // Cancelar convite
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable int id) {
        try {
            Convite convite = service.cancelar(id);

            return ResponseEntity.ok(
                    ConviteMapper.toResponseDto(convite)
            );

        } catch (RuntimeException e) {
            return tratarErro(
                    e,
                    "/api/v1/convites/" + id + "/cancelar"
            );
        }
    }

    // Excluir convite
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            service.delete(id);

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            return tratarErro(e, "/api/v1/convites/" + id);
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

