package br.com.fiap.rottracker.repository;

import br.com.fiap.rottracker.model.Usuario;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuarioRepository
{
    private final DataSource dataSource;
    public UsuarioRepository(DataSource dataSource) { this.dataSource = dataSource; }

    private static final String SQL_SELECT_ALL = "SELECT usuario_id, nome, email, status, senha_hash FROM tb_usuarios ORDER BY usuario_id";
    private static final String SQL_SELECT_ID = "SELECT usuario_id, nome, email, status, senha_hash FROM tb_usuarios WHERE usuario_id = ?";
    private static final String SQL_SELECT_NOME = "SELECT usuario_id, nome, email, status, senha_hash FROM tb_usuarios WHERE lower(nome) LIKE ? ORDER BY nome";


    // ---------------------------- Read ----------------------------
    public List<Usuario> readAll()
    {
        List<Usuario> lista = new ArrayList<>();
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Usuario user = mapearUsuario(rs);
                lista.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return lista;
    }


    public Usuario read(int id) {
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_ID)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario user = mapearUsuario(rs);
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por ID", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return null;
    }


    public List<Usuario> readByName(String name)
    {
        List<Usuario> lista = new ArrayList<>();
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_NOME)) {
            pstmt.setString(1, "%" + name.toLowerCase() + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Usuario user = mapearUsuario(rs);
                    lista.add(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuários por nome", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return lista;
    }
    

    // ---------------------------- Função auxiliar ----------------------------
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario user = new Usuario();

        user.setId(rs.getInt("usuario_id"));
        user.setNome(rs.getString("nome"));
        user.setEmail(rs.getString("email"));
        user.setStatus(rs.getString("status"));
        user.setSenha(rs.getString("senha_hash"));
        return user;
    }


}
