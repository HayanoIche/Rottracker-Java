package br.com.fiap.rottracker.model;

public class Usuario
{
    private int id;
    private String nome;
    private String email;
    private String status;
    private String senha;

    public Usuario() {}

    public Usuario(int id, String nome, String email, String status, String senha)
    {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.status = status;
        this.senha = senha;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public String getSenha() { return senha; }

    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setStatus(String status) { this.status = status; }
    public void setSenha(String senha) { this.senha = senha; }
}
