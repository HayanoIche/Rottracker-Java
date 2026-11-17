package br.com.fiap.rottracker.service;

import br.com.fiap.rottracker.exception.*;
import br.com.fiap.rottracker.model.Usuario;
import br.com.fiap.rottracker.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    // ---------------------------- Create ----------------------------

    @Transactional
    public Usuario create(Usuario usuario) {
        validar(usuario);

        usuario.setNome(usuario.getNome().trim());
        usuario.setEmail(usuario.getEmail().trim().toLowerCase());

        if (repository.readByEmail(usuario.getEmail()) != null) {
            throw new ConflictException("E-mail já cadastrado.");
        }

        repository.create(usuario);
        return usuario;
    }

    // ---------------------------- Read ----------------------------

    public Usuario read(int id) {
        validarId(id);

        Usuario usuario = repository.read(id);

        if (usuario == null) {
            throw new NotFoundException("Usuário não encontrado.");
        }

        return usuario;
    }

    public List<Usuario> readAll() {
        return repository.readAll();
    }

    public Usuario readByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("E-mail inválido.");
        }

        Usuario usuario = repository.readByEmail(email.trim().toLowerCase());

        if (usuario == null) {
            throw new NotFoundException("Usuário não encontrado.");
        }

        return usuario;
    }

    public List<Usuario> readByName(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new BadRequestException("Nome inválido.");
        }

        List<Usuario> usuarios = repository.readByName(nome.trim());

        if (usuarios.isEmpty()) {
            throw new NotFoundException("Nenhum usuário encontrado.");
        }

        return usuarios;
    }

    // ---------------------------- Update ----------------------------

    @Transactional
    public Usuario update(Usuario usuario) {
        if (usuario == null) {
            throw new BadRequestException("Usuário inválido.");
        }

        validarId(usuario.getId());
        read(usuario.getId());
        validar(usuario);

        usuario.setNome(usuario.getNome().trim());
        usuario.setEmail(usuario.getEmail().trim().toLowerCase());

        Usuario existente = repository.readByEmail(usuario.getEmail());

        if (existente != null && existente.getId() != usuario.getId()) {
            throw new ConflictException("E-mail já cadastrado.");
        }

        repository.update(usuario);
        return usuario;
    }


    // ---------------------------- Validation ----------------------------

    private void validar(Usuario usuario) {
        if (usuario == null) {
            throw new BadRequestException("Usuário inválido.");
        }

        if (usuario.getNome() == null || usuario.getNome().isBlank()) {
            throw new BadRequestException("Nome obrigatório.");
        }

        if (usuario.getNome().trim().length() > 200) {
            throw new BadRequestException("Nome deve ter no máximo 200 caracteres.");
        }

        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new BadRequestException("E-mail obrigatório.");
        }

        if (!usuario.getEmail().matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new BadRequestException("E-mail inválido.");
        }

        if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
            throw new BadRequestException("Senha obrigatória.");
        }

        if (usuario.getSenha().length() > 255) {
            throw new BadRequestException("Senha muito longa.");
        }

        if (usuario.getStatus() == null || usuario.getStatus().isBlank()
                || usuario.getStatus().length() > 10) {
            throw new BadRequestException("Status inválido.");
        }
    }

    private void validarId(int id) {
        if (id <= 0) {
            throw new BadRequestException("ID inválido.");
        }
    }
}