package br.com.fiap.rottracker.model;

import java.time.LocalDate;

public class Membro
{
    private Usuario usuario;
    private Despensa despensa;

    private int id;
    private String permissao;
    private LocalDate dataEntrada;

    public Membro() {}

    public Membro(Usuario usuario, Despensa despensa, int id, String permissao, LocalDate dataEntrada)
    {
        this.usuario = usuario;
        this.despensa = despensa;
        this.id = id;
        this.permissao = permissao;
        this.dataEntrada = dataEntrada;
    }

    public Usuario getUsuario() { return usuario; }
    public Despensa getDespensa() { return despensa; }
    public int getId() { return id; }
    public String getPermissao() { return permissao; }
    public LocalDate getDataEntrada() { return dataEntrada; }

    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setDespensa(Despensa despensa) { this.despensa = despensa; }
    public void setId(int id) { this.id = id; }
    public void setPermissao(String permissao) { this.permissao = permissao; }
    public void setDataEntrada(LocalDate dataEntrada) { this.dataEntrada = dataEntrada; }
}
