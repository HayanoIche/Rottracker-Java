package br.com.fiap.rottracker.service;

import br.com.fiap.rottracker.exception.*;
import br.com.fiap.rottracker.model.*;
import br.com.fiap.rottracker.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DespensaService {

    private final DespensaRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final MembroRepository membroRepository;
    private final ConviteRepository conviteRepository;

    public DespensaService(
            DespensaRepository repository,
            UsuarioRepository usuarioRepository,
            ProdutoRepository produtoRepository,
            MembroRepository membroRepository,
            ConviteRepository conviteRepository) {

        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.membroRepository = membroRepository;
        this.conviteRepository = conviteRepository;
    }

    // ---------------------------- Create ----------------------------

    @Transactional
    public Despensa create(Despensa despensa, int usuarioId) {
        validar(despensa);

        Usuario usuario = usuarioRepository.read(usuarioId);

        if (usuario == null) {
            throw new NotFoundException("Usuário não encontrado.");
        }

        despensa.setNome(despensa.getNome().trim());

        if (despensa.getDataCriacao() == null) {
            despensa.setDataCriacao(LocalDate.now());
        }

        repository.create(despensa);

        if (despensa.getId() <= 0) {
            throw new IllegalStateException("ID da despensa não foi gerado.");
        }

        Membro administrador = new Membro();
        administrador.setUsuario(usuario);
        administrador.setDespensa(despensa);
        administrador.setPermissao("ADMIN");
        administrador.setDataEntrada(LocalDate.now());

        membroRepository.create(administrador);

        return despensa;
    }

    // ---------------------------- Read ----------------------------

    public Despensa read(int id) {
        validarId(id);

        Despensa despensa = repository.read(id);

        if (despensa == null) {
            throw new NotFoundException("Despensa não encontrada.");
        }

        return despensa;
    }

    public List<Despensa> readAll() {
        return repository.readAll();
    }

    public List<Despensa> readByUsuario(int usuarioId) {
        if (usuarioId <= 0) {
            throw new BadRequestException("ID de usuário inválido.");
        }

        if (usuarioRepository.read(usuarioId) == null) {
            throw new NotFoundException("Usuário não encontrado.");
        }

        return repository.readByUsuario(usuarioId);
    }

    // ---------------------------- Update ----------------------------

    @Transactional
    public Despensa update(Despensa despensa) {
        if (despensa == null) {
            throw new BadRequestException("Despensa inválida.");
        }

        validarId(despensa.getId());
        read(despensa.getId());
        validar(despensa);

        despensa.setNome(despensa.getNome().trim());

        repository.update(despensa);
        return despensa;
    }

    // ---------------------------- Delete ----------------------------

    @Transactional
    public void delete(int id) {
        Despensa despensa = read(id);

        for (Convite convite : conviteRepository.readByDespensa(id)) {
            conviteRepository.delete(convite.getId());
        }

        for (Produto produto : produtoRepository.readByDespensa(id)) {
            produtoRepository.delete(produto.getId());
        }

        for (Membro membro : membroRepository.readByDespensa(id)) {
            membroRepository.delete(membro.getId());
        }

        repository.delete(despensa.getId());
    }

    // ---------------------------- Validation ----------------------------

    private void validar(Despensa despensa) {
        if (despensa == null) {
            throw new BadRequestException("Despensa inválida.");
        }

        if (despensa.getNome() == null || despensa.getNome().isBlank()) {
            throw new BadRequestException("Nome obrigatório.");
        }

        if (despensa.getNome().trim().length() > 100) {
            throw new BadRequestException("Nome deve ter no máximo 100 caracteres.");
        }

        if (despensa.getTipo() == null || despensa.getTipo().isBlank()
                || despensa.getTipo().length() > 10) {
            throw new BadRequestException("Tipo inválido.");
        }
    }

    private void validarId(int id) {
        if (id <= 0) {
            throw new BadRequestException("ID inválido.");
        }
    }
}