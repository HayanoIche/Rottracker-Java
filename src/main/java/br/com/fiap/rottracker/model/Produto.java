package br.com.fiap.rottracker.model;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public class Produto
{
    private int id;
    private String descricao;
    private double quantidade;
    private String unidade;
    private LocalDate dataCompra;
    private LocalDate validade;
    private boolean congelado;
    private Categoria categoria;

    public Produto() {}

    public Produto(int id, String descricao, double quantidade, String unidade, LocalDate dataCompra, LocalDate validade, boolean congelado, Categoria categoria)
    {
        this.id = id;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.dataCompra = dataCompra;
        this.validade = validade;
        this.congelado = congelado;
        this.categoria = categoria;
    }

    public int getId() { return id; }
    public String getDescricao() { return descricao; }
    public double getQuantidade() { return quantidade; }
    public String getUnidade() { return unidade; }
    public LocalDate getDataCompra() { return dataCompra; }
    public LocalDate getValidade() { return validade; }
    public boolean isCongelado() { return congelado; }
    public Categoria getCategoria() { return categoria; }

    public void setId(int id) { this.id = id; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setQuantidade(double quantidade) { this.quantidade = quantidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }
    public void setDataCompra(LocalDate dataCompra) { this.dataCompra = dataCompra; }
    public void setValidade(LocalDate validade) { this.validade = validade; }
    public void setCongelado(boolean congelado) { this.congelado = congelado; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}
