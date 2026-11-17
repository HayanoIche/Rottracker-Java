package br.com.fiap.rottracker.service;

import br.com.fiap.rottracker.exception.*;
import br.com.fiap.rottracker.model.*;
import br.com.fiap.rottracker.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@Service
public class ConviteService {

    private final ConviteRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final DespensaRepository despensaRepository;
    private final MembroRepository membroRepository;

    private final SecureRandom random = new SecureRandom();

    public ConviteService(
            ConviteRepository repository,
            UsuarioRepository usuarioRepository,
            DespensaRepository despensaRepository,
            MembroRepository membroRepository) {

        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.despensaRepository = despensaRepository;
        this.membroRepository = membroRepository;
    }

    // ---------------------------- Create ----------------------------

    @Transactional
    public Convite create(Convite convite) {
        validar(convite);

        Usuario criador = usuarioRepository.read(
                convite.getUsuario().getId());

        Despensa despensa = despensaRepository.read(
                convite.getDespensa().getId());

        if (criador == null || despensa == null) {
            throw new NotFoundException(
                    "Usuário ou despensa não encontrado.");
        }

        String email = convite.getEmailConvidado()
                .trim()
                .toLowerCase();

        List<Convite> convites = repository.readByDespensa(despensa.getId());

        boolean pendente = convites.stream().anyMatch(c ->
                c.getEmailConvidado().equalsIgnoreCase(email)
                        && "PENDENTE".equalsIgnoreCase(c.getStatusConvite())
                        && c.getDataExpiracao() != null
                        && !c.getDataExpiracao().isBefore(LocalDate.now()));

        if (pendente) {
            throw new ConflictException(
                    "Já existe um convite pendente para esse e-mail.");
        }

        convite.setUsuario(criador);
        convite.setDespensa(despensa);
        convite.setEmailConvidado(email);
        convite.setToken(gerarTokenUnico());
        convite.setStatusConvite("PENDENTE");
        convite.setDataCriacao(LocalDate.now());
        convite.setDataExpiracao(LocalDate.now().plusDays(7));

        repository.create(convite);
        return convite;
    }

    // ---------------------------- Read ----------------------------

    public Convite read(int id) {
        validarId(id);

        Convite convite = repository.read(id);

        if (convite == null) {
            throw new NotFoundException("Convite não encontrado.");
        }

        return convite;
    }

    public List<Convite> readAll() {
        return repository.readAll();
    }

    public Convite readByToken(String token) {
        if (token == null || token.isBlank()) {
            throw new BadRequestException("Token inválido.");
        }

        Convite convite = repository.readByToken(token);

        if (convite == null) {
            throw new NotFoundException("Convite não encontrado.");
        }

        return convite;
    }

    public List<Convite> readByDespensa(int despensaId) {
        if (despensaRepository.read(despensaId) == null) {
            throw new NotFoundException("Despensa não encontrada.");
        }

        return repository.readByDespensa(despensaId);
    }

    public List<Convite> readByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("E-mail inválido.");
        }

        return repository.readByEmail(email.trim().toLowerCase());
    }

    public List<Convite> readByStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new BadRequestException("Status inválido.");
        }

        return repository.readByStatus(status.trim().toUpperCase());
    }

    // ---------------------------- Update ----------------------------

    @Transactional
    public Convite update(Convite convite) {
        if (convite == null) {
            throw new BadRequestException("Convite inválido.");
        }

        validarId(convite.getId());

        Convite atual = read(convite.getId());

        // Mantém os dados que não podem ser alterados pelo update comum.
        convite.setToken(atual.getToken());
        convite.setDataCriacao(atual.getDataCriacao());
        convite.setDataExpiracao(atual.getDataExpiracao());
        convite.setStatusConvite(atual.getStatusConvite());
        convite.setUsuario(atual.getUsuario());
        convite.setDespensa(atual.getDespensa());

        validar(convite);

        convite.setEmailConvidado(
                convite.getEmailConvidado().trim().toLowerCase());

        repository.update(convite);
        return convite;
    }

    // ---------------------------- Accept ----------------------------

    @Transactional
    public Convite aceitar(String token, int usuarioId) {
        Convite convite = validarConvitePendente(token);

        Usuario usuario = usuarioRepository.read(usuarioId);

        if (usuario == null) {
            throw new NotFoundException("Usuário não encontrado.");
        }

        if (!usuario.getEmail().equalsIgnoreCase(
                convite.getEmailConvidado())) {
            throw new ConflictException(
                    "O convite foi enviado para outro endereço de e-mail.");
        }

        Membro membro = new Membro();
        membro.setUsuario(usuario);
        membro.setDespensa(convite.getDespensa());
        membro.setPermissao("MEMBRO");
        membro.setDataEntrada(LocalDate.now());

        // O MembroService/Repository também protege a duplicidade.
        List<Membro> membros = membroRepository.readByDespensa(
                convite.getDespensa().getId());

        boolean jaMembro = membros.stream().anyMatch(m ->
                m.getUsuario().getId() == usuarioId);

        if (jaMembro) {
            throw new ConflictException(
                    "Usuário já pertence a essa despensa.");
        }

        membroRepository.create(membro);

        convite.setStatusConvite("ACEITO");
        repository.update(convite);

        return convite;
    }

    // ---------------------------- Cancel ----------------------------

    @Transactional
    public Convite cancelar(int id) {
        Convite convite = read(id);

        if (!"PENDENTE".equalsIgnoreCase(convite.getStatusConvite())) {
            throw new ConflictException(
                    "Somente convites pendentes podem ser cancelados.");
        }

        convite.setStatusConvite("CANCELADO");
        repository.update(convite);

        return convite;
    }

    // ---------------------------- Delete ----------------------------

    @Transactional
    public void delete(int id) {
        read(id);
        repository.delete(id);
    }

    // ---------------------------- Helpers ----------------------------

    private Convite validarConvitePendente(String token) {
        Convite convite = readByToken(token);

        if (!"PENDENTE".equalsIgnoreCase(convite.getStatusConvite())) {
            throw new ConflictException("Convite não está pendente.");
        }

        if (convite.getDataExpiracao() == null ||
                convite.getDataExpiracao().isBefore(LocalDate.now())) {
            throw new ConflictException("Convite expirado.");
        }

        return convite;
    }

    private String gerarTokenUnico() {
        String token;

        do {
            byte[] bytes = new byte[32];
            random.nextBytes(bytes);

            token = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(bytes);

        } while (repository.readByToken(token) != null);

        return token;
    }

    private void validar(Convite convite) {
        if (convite == null) {
            throw new BadRequestException("Convite inválido.");
        }

        if (convite.getUsuario() == null ||
                convite.getUsuario().getId() <= 0) {
            throw new BadRequestException("Usuário criador obrigatório.");
        }

        if (convite.getDespensa() == null ||
                convite.getDespensa().getId() <= 0) {
            throw new BadRequestException("Despensa obrigatória.");
        }

        if (convite.getEmailConvidado() == null ||
                !convite.getEmailConvidado().matches(
                        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new BadRequestException("E-mail do convidado inválido.");
        }
    }

    private void validarId(int id) {
        if (id <= 0) {
            throw new BadRequestException("ID inválido.");
        }
    }
}