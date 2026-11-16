package br.com.fiap.rottracker.model;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public class Convite
{
    private int id;
    private String token;
    private String statusConvite;
    private String emailConvidado;
    private LocalDate dataCriacao;
    private LocalDate dataExpiracao;
    private Despensa despensa;
    private Usuario usuario;

    public Convite() {}

    public Convite(int id, String token, String statusConvite, String emailConvidado, LocalDate dataCriacao, LocalDate dataExpiracao, Despensa despensa, Usuario usuario)
    {
        this.id = id;
        this.token = token;
        this.statusConvite = statusConvite;
        this.emailConvidado = emailConvidado;
        this.dataCriacao = dataCriacao;
        this.dataExpiracao = dataExpiracao;
        this.despensa = despensa;
        this.usuario = usuario;
    }

    public int getId() { return id; }
    public String getToken() { return token; }
    public String getStatusConvite() { return statusConvite; }
    public String getEmailConvidado() { return emailConvidado; }
    public LocalDate getDataCriacao() { return dataCriacao; }
    public LocalDate getDataExpiracao() { return dataExpiracao; }
    public Despensa getDespensa() { return despensa; }
    public Usuario getUsuario() { return usuario; }

    public void setId(int id) { this.id = id; }
    public void setToken(String token) { this.token = token; }
    public void setStatusConvite(String statusConvite) { this.statusConvite = statusConvite; }
    public void setEmailConvidado(String emailConvidado) { this.emailConvidado = emailConvidado; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }
    public void setDataExpiracao(LocalDate dataExpiracao) { this.dataExpiracao = dataExpiracao; }
    public void setDespensa(Despensa despensa) { this.despensa = despensa; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
