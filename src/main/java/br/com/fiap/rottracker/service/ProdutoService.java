package br.com.fiap.rottracker.service;

import br.com.fiap.rottracker.exception.*;
import br.com.fiap.rottracker.model.*;
import br.com.fiap.rottracker.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;
    private final CategoriaRepository categoriaRepository;
    private final DespensaRepository despensaRepository;

    public ProdutoService(
            ProdutoRepository repository,
            CategoriaRepository categoriaRepository,
            DespensaRepository despensaRepository) {

        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
        this.despensaRepository = despensaRepository;
    }

    // ---------------------------- Create ----------------------------

    @Transactional
    public Produto create(Produto produto, int despensaId) {
        validar(produto);
        validarDespensa(despensaId);
        validarCategoria(produto.getCategoria());

        produto.setDescricao(produto.getDescricao().trim());
        produto.setUnidade(produto.getUnidade().trim());

        repository.create(produto, despensaId);
        return produto;
    }

    // ---------------------------- Read ----------------------------

    public Produto read(int id) {
        validarId(id);

        Produto produto = repository.read(id);

        if (produto == null) {
            throw new NotFoundException("Produto não encontrado.");
        }

        return produto;
    }

    public List<Produto> readAll() {
        return repository.readAll();
    }

    public List<Produto> readByDespensa(int despensaId) {
        validarDespensa(despensaId);
        return repository.readByDespensa(despensaId);
    }

    public List<Produto> readByName(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new BadRequestException("Descrição inválida.");
        }

        List<Produto> produtos = repository.readByName(nome.trim());

        if (produtos.isEmpty()) {
            throw new NotFoundException("Nenhum produto encontrado.");
        }

        return produtos;
    }

    // ---------------------------- Update ----------------------------

    @Transactional
    public Produto update(Produto produto, int despensaId) {
        if (produto == null) {
            throw new BadRequestException("Produto inválido.");
        }

        validarId(produto.getId());
        validar(produto);
        validarDespensa(despensaId);
        validarCategoria(produto.getCategoria());

        boolean pertence = repository.readByDespensa(despensaId)
                .stream()
                .anyMatch(p -> p.getId() == produto.getId());

        if (!pertence) {
            throw new NotFoundException(
                    "Produto não encontrado nessa despensa.");
        }

        produto.setDescricao(produto.getDescricao().trim());
        produto.setUnidade(produto.getUnidade().trim());

        repository.update(produto, despensaId);
        return produto;
    }

    // ---------------------------- Delete ----------------------------

    @Transactional
    public void delete(int id) {
        read(id);
        repository.delete(id);
    }

    // ---------------------------- Validation ----------------------------

    private void validar(Produto produto) {
        if (produto == null) {
            throw new BadRequestException("Produto inválido.");
        }

        if (produto.getDescricao() == null ||
                produto.getDescricao().isBlank()) {
            throw new BadRequestException("Descrição obrigatória.");
        }

        if (produto.getDescricao().trim().length() > 100) {
            throw new BadRequestException("Descrição muito longa.");
        }

        if (!Double.isFinite(produto.getQuantidade()) ||
                produto.getQuantidade() < 0) {
            throw new BadRequestException("Quantidade inválida.");
        }

        if (produto.getUnidade() == null ||
                produto.getUnidade().isBlank() ||
                produto.getUnidade().length() > 5) {
            throw new BadRequestException("Unidade inválida.");
        }

        if (produto.getDataCompra() != null &&
                produto.getDataCompra().isAfter(LocalDate.now())) {
            throw new BadRequestException("Data de compra não pode ser futura.");
        }

        if (produto.getDataCompra() != null &&
                produto.getValidade() != null &&
                produto.getValidade().isBefore(produto.getDataCompra())) {
            throw new BadRequestException(
                    "Validade não pode ser anterior à data de compra.");
        }
    }

    private void validarCategoria(Categoria categoria) {
        if (categoria == null || categoria.getId() <= 0) {
            throw new BadRequestException("Categoria obrigatória.");
        }

        if (categoriaRepository.read(categoria.getId()) == null) {
            throw new NotFoundException("Categoria não encontrada.");
        }
    }

    private void validarDespensa(int id) {
        validarId(id);

        if (despensaRepository.read(id) == null) {
            throw new NotFoundException("Despensa não encontrada.");
        }
    }

    private void validarId(int id) {
        if (id <= 0) {
            throw new BadRequestException("ID inválido.");
        }
    }
}