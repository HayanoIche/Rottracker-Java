package br.com.fiap.rottracker.service;

import br.com.fiap.rottracker.exception.*;
import br.com.fiap.rottracker.model.Categoria;
import br.com.fiap.rottracker.repository.CategoriaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    // ---------------------------- Create ----------------------------

    @Transactional
    public Categoria create(Categoria categoria) {
        validar(categoria);

        categoria.setNome(categoria.getNome().trim());

        if (!repository.readByName(categoria.getNome()).isEmpty()) {
            throw new ConflictException("Categoria já cadastrada.");
        }

        repository.create(categoria);
        return categoria;
    }


    // ---------------------------- Read ----------------------------

    public Categoria read(int id) {
        validarId(id);

        Categoria categoria = repository.read(id);

        if (categoria == null) {
            throw new NotFoundException("Categoria não encontrada.");
        }

        return categoria;
    }

    public List<Categoria> readAll() {
        return repository.readAll();
    }

    public List<Categoria> readByName(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new BadRequestException("Nome inválido.");
        }

        List<Categoria> categorias = repository.readByName(nome.trim());

        if (categorias.isEmpty()) {
            throw new NotFoundException("Categoria não encontrada.");
        }

        return categorias;
    }

    // ---------------------------- Update ----------------------------

    @Transactional
    public Categoria update(Categoria categoria) {
        validar(categoria);
        validarId(categoria.getId());

        read(categoria.getId());

        categoria.setNome(categoria.getNome().trim());

        List<Categoria> existentes = repository.readByName(categoria.getNome());

        boolean nomeEmUso = existentes.stream()
                .anyMatch(c -> c.getId() != categoria.getId());

        if (nomeEmUso) {
            throw new ConflictException("Já existe uma categoria com esse nome.");
        }

        repository.update(categoria);
        return categoria;
    }

    // ---------------------------- Delete ----------------------------

    @Transactional
    public void delete(int id) {
        read(id);
        repository.delete(id);
    }

    // ---------------------------- Validation ----------------------------

    private void validar(Categoria categoria) {
        if (categoria == null) {
            throw new BadRequestException("Categoria inválida.");
        }

        if (categoria.getNome() == null || categoria.getNome().isBlank()) {
            throw new BadRequestException("Nome obrigatório.");
        }

        if (categoria.getNome().trim().length() > 50) {
            throw new BadRequestException("Nome deve ter no máximo 50 caracteres.");
        }
    }

    private void validarId(int id) {
        if (id <= 0) {
            throw new BadRequestException("ID inválido.");
        }
    }
}