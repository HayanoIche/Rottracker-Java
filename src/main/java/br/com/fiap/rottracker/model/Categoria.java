package br.com.fiap.rottracker.model;

public class Categoria
{
    private int id;
    private String nome;
    private boolean perecivel;

    public Categoria() {}

    public Categoria(int id, String nome, boolean perecivel)
    {
        this.id = id;
        this.nome = nome;
        this.perecivel = perecivel;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public boolean isPerecivel() { return perecivel; }

    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setPerecivel(boolean perecivel) { this.perecivel = perecivel; }
}
