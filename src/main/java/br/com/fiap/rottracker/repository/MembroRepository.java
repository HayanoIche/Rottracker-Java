
package br.com.fiap.rottracker.repository;

import br.com.fiap.rottracker.model.Membro;
import br.com.fiap.rottracker.model.Usuario;
import br.com.fiap.rottracker.model.Despensa;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MembroRepository
{
    private final DataSource dataSource;
    public MembroRepository(DataSource dataSource) { this.dataSource = dataSource; }

    private static final String SQL_INSERT =
            "INSERT INTO tb_membros(permissao, data_entrada, despensa_id, usuario_id) VALUES (?, ?, ?, ?)";

    private static final String SQL_SELECT_BASE =
            "SELECT m.membro_id, m.permissao, m.data_entrada, " +
                    "u.usuario_id, u.nome AS usuario_nome, u.email, u.status, u.senha_hash, " +
                    "d.despensa_id, d.nome AS despensa_nome, d.tipo, d.data_criacao AS despensa_data_criacao " +
                    "FROM tb_membros m " +
                    "INNER JOIN tb_usuarios u ON m.usuario_id = u.usuario_id " +
                    "INNER JOIN tb_despensas d ON m.despensa_id = d.despensa_id ";

    private static final String SQL_SELECT_ALL = SQL_SELECT_BASE + "ORDER BY m.membro_id";
    private static final String SQL_SELECT_ID = SQL_SELECT_BASE + "WHERE m.membro_id = ?";
    private static final String SQL_SELECT_DESPENSA = SQL_SELECT_BASE + "WHERE m.despensa_id = ? ORDER BY m.membro_id";
    private static final String SQL_SELECT_USUARIO = SQL_SELECT_BASE + "WHERE m.usuario_id = ? ORDER BY m.membro_id";

    private static final String SQL_UPDATE =
            "UPDATE tb_membros SET permissao = ?, data_entrada = ?, despensa_id = ?, usuario_id = ? WHERE membro_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM tb_membros WHERE membro_id = ?";


    // ---------------------------- Create ----------------------------
    public void create(Membro membro) {
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_INSERT, new String[]{"MEMBRO_ID"})) {

            pstmt.setString(1, membro.getPermissao());
            pstmt.setDate(2, membro.getDataEntrada() != null ? Date.valueOf(membro.getDataEntrada()) : null);
            pstmt.setInt(3, membro.getDespensa().getId());
            pstmt.setInt(4, membro.getUsuario().getId());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) membro.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir membro", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }


    // ---------------------------- Read ----------------------------
    public List<Membro> readAll() {
        List<Membro> lista = new ArrayList<>();
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) lista.add(mapearMembro(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar membros", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return lista;
    }


    public Membro read(int id) {
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_ID)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapearMembro(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar membro por ID", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return null;
    }


    public List<Membro> readByDespensa(int despensaId) {
        List<Membro> lista = new ArrayList<>();
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_DESPENSA)) {
            pstmt.setInt(1, despensaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(mapearMembro(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar membros por despensa", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return lista;
    }


    public List<Membro> readByUsuario(int usuarioId) {
        List<Membro> lista = new ArrayList<>();
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_USUARIO)) {
            pstmt.setInt(1, usuarioId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(mapearMembro(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar membros por usuário", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return lista;
    }


    // ---------------------------- Update ----------------------------
    public void update(Membro membro) {
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_UPDATE)) {

            pstmt.setString(1, membro.getPermissao());
            pstmt.setDate(2, membro.getDataEntrada() != null ? Date.valueOf(membro.getDataEntrada()) : null);
            pstmt.setInt(3, membro.getDespensa().getId());
            pstmt.setInt(4, membro.getUsuario().getId());
            pstmt.setInt(5, membro.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar membro", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }


    // ---------------------------- Delete ----------------------------
    public void delete(int id) {
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_DELETE)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir membro", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }


    // ---------------------------- Função auxiliar ----------------------------
    private Membro mapearMembro(ResultSet rs) throws SQLException {
        Membro membro = new Membro();

        membro.setId(rs.getInt("membro_id"));
        membro.setPermissao(rs.getString("permissao"));

        Date dataEntrada = rs.getDate("data_entrada");
        membro.setDataEntrada(dataEntrada != null ? dataEntrada.toLocalDate() : null);

        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("usuario_id"));
        usuario.setNome(rs.getString("usuario_nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setStatus(rs.getString("status"));
        usuario.setSenha(rs.getString("senha_hash"));
        membro.setUsuario(usuario);

        Despensa despensa = new Despensa();
        despensa.setId(rs.getInt("despensa_id"));
        despensa.setNome(rs.getString("despensa_nome"));
        despensa.setTipo(rs.getString("tipo"));

        Date dataCriacao = rs.getDate("despensa_data_criacao");
        despensa.setDataCriacao(dataCriacao != null ? dataCriacao.toLocalDate() : null);

        membro.setDespensa(despensa);

        return membro;
    }
}
