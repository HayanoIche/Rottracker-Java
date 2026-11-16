package br.com.fiap.rottracker.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Despensa
{
    private int id;
    private String nome;
    private String tipo;
    private LocalDate dataCriacao;

    private List<Produto> produtos = new ArrayList<>();
    private List<Membro> membros = new ArrayList<>();

    public Despensa() {}

    public Despensa(int id, String nome, String tipo, LocalDate dataCriacao)
    {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.dataCriacao = dataCriacao;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getTipo() { return tipo; }
    public LocalDate getDataCriacao() { return dataCriacao; }
    public List<Produto> getProdutos() { return produtos; }
    public List<Membro> getMembros() { return membros; }

    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }
    public void setProdutos(List<Produto> produtos) { this.produtos = produtos; }
    public void setMembros(List<Membro> membros) { this.membros = membros; }

    public void adicionaMembro(Membro membro) { this.membros.add(membro); }
    public void adicionaProduto(Produto produto) { this.produtos.add(produto); }
}
