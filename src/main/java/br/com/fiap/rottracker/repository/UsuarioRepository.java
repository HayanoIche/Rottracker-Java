
package br.com.fiap.rottracker.repository;

import br.com.fiap.rottracker.model.Usuario;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuarioRepository
{
    private final DataSource dataSource;
    public UsuarioRepository(DataSource dataSource) { this.dataSource = dataSource; }

    private static final String SQL_INSERT =
            "INSERT INTO tb_usuarios(nome, email, status, senha_hash) VALUES (?, ?, ?, ?)";

    private static final String SQL_SELECT_ALL =
            "SELECT usuario_id, nome, email, status, senha_hash FROM tb_usuarios ORDER BY usuario_id";

    private static final String SQL_SELECT_ID =
            "SELECT usuario_id, nome, email, status, senha_hash FROM tb_usuarios WHERE usuario_id = ?";

    private static final String SQL_SELECT_NOME =
            "SELECT usuario_id, nome, email, status, senha_hash FROM tb_usuarios WHERE lower(nome) LIKE ? ORDER BY nome";

    private static final String SQL_SELECT_EMAIL =
            "SELECT usuario_id, nome, email, status, senha_hash FROM tb_usuarios WHERE lower(email) = lower(?)";

    private static final String SQL_UPDATE =
            "UPDATE tb_usuarios SET nome = ?, email = ?, status = ?, senha_hash = ? WHERE usuario_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM tb_usuarios WHERE usuario_id = ?";


    // ---------------------------- Create ----------------------------
    public void create(Usuario user) {
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_INSERT, new String[]{"USUARIO_ID"})) {

            pstmt.setString(1, user.getNome());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getStatus());
            pstmt.setString(4, user.getSenha());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) user.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir usuário", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }


    // ---------------------------- Read ----------------------------
    public List<Usuario> readAll() {
        List<Usuario> lista = new ArrayList<>();
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) lista.add(mapearUsuario(rs));

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
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapearUsuario(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por ID", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return null;
    }


    public Usuario readByEmail(String email) {
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_EMAIL)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapearUsuario(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por email", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return null;
    }


    public List<Usuario> readByName(String name) {
        List<Usuario> lista = new ArrayList<>();
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_NOME)) {
            pstmt.setString(1, "%" + name.toLowerCase() + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuários por nome", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return lista;
    }


    // ---------------------------- Update ----------------------------
    public void update(Usuario user) {
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_UPDATE)) {

            pstmt.setString(1, user.getNome());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getStatus());
            pstmt.setString(4, user.getSenha());
            pstmt.setInt(5, user.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
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
