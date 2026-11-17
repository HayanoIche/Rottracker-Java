package br.com.fiap.rottracker.service;

import br.com.fiap.rottracker.exception.*;
import br.com.fiap.rottracker.model.*;
import br.com.fiap.rottracker.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MembroService {

    private final MembroRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final DespensaRepository despensaRepository;

    public MembroService(
            MembroRepository repository,
            UsuarioRepository usuarioRepository,
            DespensaRepository despensaRepository) {

        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.despensaRepository = despensaRepository;
    }

    // ---------------------------- Create ----------------------------

    @Transactional
    public Membro create(Membro membro) {
        validar(membro);

        Usuario usuario = usuarioRepository.read(
                membro.getUsuario().getId());

        Despensa despensa = despensaRepository.read(
                membro.getDespensa().getId());

        if (usuario == null) {
            throw new NotFoundException("Usuário não encontrado.");
        }

        if (despensa == null) {
            throw new NotFoundException("Despensa não encontrada.");
        }

        verificarDuplicidade(usuario.getId(), despensa.getId(), 0);

        membro.setUsuario(usuario);
        membro.setDespensa(despensa);

        if (membro.getDataEntrada() == null) {
            membro.setDataEntrada(LocalDate.now());
        }

        repository.create(membro);
        return membro;
    }

    // ---------------------------- Read ----------------------------

    public Membro read(int id) {
        validarId(id);

        Membro membro = repository.read(id);

        if (membro == null) {
            throw new NotFoundException("Membro não encontrado.");
        }

        return membro;
    }

    public List<Membro> readAll() {
        return repository.readAll();
    }

    public List<Membro> readByDespensa(int despensaId) {
        validarDespensa(despensaId);
        return repository.readByDespensa(despensaId);
    }

    public List<Membro> readByUsuario(int usuarioId) {
        validarId(usuarioId);

        if (usuarioRepository.read(usuarioId) == null) {
            throw new NotFoundException("Usuário não encontrado.");
        }

        return repository.readByUsuario(usuarioId);
    }

    // ---------------------------- Update ----------------------------

    @Transactional
    public Membro update(Membro membro) {
        if (membro == null) {
            throw new BadRequestException("Membro inválido.");
        }

        validarId(membro.getId());
        validar(membro);

        Membro atual = read(membro.getId());

        Usuario usuario = usuarioRepository.read(
                membro.getUsuario().getId());

        Despensa despensa = despensaRepository.read(
                membro.getDespensa().getId());

        if (usuario == null || despensa == null) {
            throw new NotFoundException(
                    "Usuário ou despensa não encontrado.");
        }

        verificarDuplicidade(usuario.getId(), despensa.getId(), membro.getId());

        if ("ADMIN".equalsIgnoreCase(atual.getPermissao())
                && !"ADMIN".equalsIgnoreCase(membro.getPermissao())) {
            verificarUltimoAdministrador(atual);
        }

        membro.setUsuario(usuario);
        membro.setDespensa(despensa);

        repository.update(membro);
        return membro;
    }

    // ---------------------------- Delete ----------------------------

    @Transactional
    public void delete(int id) {
        Membro membro = read(id);

        if ("ADMIN".equalsIgnoreCase(membro.getPermissao())) {
            verificarUltimoAdministrador(membro);
        }

        repository.delete(id);
    }

    // ---------------------------- Validation ----------------------------

    private void validar(Membro membro) {
        if (membro == null) {
            throw new BadRequestException("Membro inválido.");
        }

        if (membro.getUsuario() == null ||
                membro.getUsuario().getId() <= 0) {
            throw new BadRequestException("Usuário obrigatório.");
        }

        if (membro.getDespensa() == null ||
                membro.getDespensa().getId() <= 0) {
            throw new BadRequestException("Despensa obrigatória.");
        }

        if (membro.getPermissao() == null ||
                membro.getPermissao().isBlank() ||
                membro.getPermissao().length() > 6) {
            throw new BadRequestException("Permissão inválida.");
        }

        String permissao = membro.getPermissao().toUpperCase();

        if (!permissao.equals("ADMIN") && !permissao.equals("MEMBRO")) {
            throw new BadRequestException("Permissão deve ser ADMIN ou MEMBRO.");
        }

        membro.setPermissao(permissao);
    }

    private void verificarDuplicidade(
            int usuarioId, int despensaId, int membroAtualId) {

        boolean duplicado = repository.readByDespensa(despensaId)
                .stream()
                .anyMatch(m ->
                        m.getUsuario().getId() == usuarioId
                                && m.getId() != membroAtualId);

        if (duplicado) {
            throw new ConflictException(
                    "Usuário já pertence a essa despensa.");
        }
    }

    private void verificarUltimoAdministrador(Membro membro) {
        long administradores = repository
                .readByDespensa(membro.getDespensa().getId())
                .stream()
                .filter(m -> "ADMIN".equalsIgnoreCase(m.getPermissao()))
                .count();

        if (administradores <= 1) {
            throw new ConflictException(
                    "A despensa precisa ter pelo menos um administrador.");
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