
package br.com.fiap.rottracker.repository;

import br.com.fiap.rottracker.model.Convite;
import br.com.fiap.rottracker.model.Usuario;
import br.com.fiap.rottracker.model.Despensa;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ConviteRepository
{
    private final DataSource dataSource;
    public ConviteRepository(DataSource dataSource) { this.dataSource = dataSource; }

    private static final String SQL_INSERT =
            "INSERT INTO tb_convites(token, status_convite, email_convidado, data_criacao, data_expiracao, despensa_id, usuario_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_BASE =
            "SELECT c.convite_id, c.token, c.status_convite, c.email_convidado, " +
                    "c.data_criacao, c.data_expiracao, " +
                    "u.usuario_id, u.nome AS usuario_nome, u.email, u.status, u.senha_hash, " +
                    "d.despensa_id, d.nome AS despensa_nome, d.tipo, d.data_criacao AS despensa_data_criacao " +
                    "FROM tb_convites c " +
                    "INNER JOIN tb_usuarios u ON c.usuario_id = u.usuario_id " +
                    "INNER JOIN tb_despensas d ON c.despensa_id = d.despensa_id ";

    private static final String SQL_SELECT_ALL = SQL_SELECT_BASE + "ORDER BY c.convite_id";
    private static final String SQL_SELECT_ID = SQL_SELECT_BASE + "WHERE c.convite_id = ?";
    private static final String SQL_SELECT_TOKEN = SQL_SELECT_BASE + "WHERE c.token = ?";
    private static final String SQL_SELECT_DESPENSA = SQL_SELECT_BASE + "WHERE c.despensa_id = ? ORDER BY c.convite_id";
    private static final String SQL_SELECT_EMAIL = SQL_SELECT_BASE + "WHERE lower(c.email_convidado) = lower(?) ORDER BY c.convite_id";
    private static final String SQL_SELECT_STATUS = SQL_SELECT_BASE + "WHERE lower(c.status_convite) = lower(?) ORDER BY c.convite_id";

    private static final String SQL_UPDATE =
            "UPDATE tb_convites SET token = ?, status_convite = ?, email_convidado = ?, data_criacao = ?, data_expiracao = ?, despensa_id = ?, usuario_id = ? WHERE convite_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM tb_convites WHERE convite_id = ?";


    // ---------------------------- Create ----------------------------
    public void create(Convite convite) {
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_INSERT, new String[]{"CONVITE_ID"})) {

            pstmt.setString(1, convite.getToken());
            pstmt.setString(2, convite.getStatusConvite());
            pstmt.setString(3, convite.getEmailConvidado());
            pstmt.setDate(4, convite.getDataCriacao() != null ? Date.valueOf(convite.getDataCriacao()) : null);
            pstmt.setDate(5, convite.getDataExpiracao() != null ? Date.valueOf(convite.getDataExpiracao()) : null);
            pstmt.setInt(6, convite.getDespensa().getId());
            pstmt.setInt(7, convite.getUsuario().getId());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) convite.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir convite", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }


    // ---------------------------- Read ----------------------------
    public List<Convite> readAll() {
        List<Convite> lista = new ArrayList<>();
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) lista.add(mapearConvite(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar convites", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return lista;
    }


    public Convite read(int id) {
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_ID)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapearConvite(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar convite por ID", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return null;
    }


    public Convite readByToken(String token) {
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_TOKEN)) {
            pstmt.setString(1, token);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapearConvite(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar convite por token", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return null;
    }


    public List<Convite> readByDespensa(int despensaId) {
        return readLista(SQL_SELECT_DESPENSA, despensaId);
    }


    public List<Convite> readByEmail(String email) {
        return readLista(SQL_SELECT_EMAIL, email);
    }


    public List<Convite> readByStatus(String status) {
        return readLista(SQL_SELECT_STATUS, status);
    }


    private List<Convite> readLista(String sql, Object parametro) {
        List<Convite> lista = new ArrayList<>();
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {

            if (parametro instanceof Integer) pstmt.setInt(1, (Integer) parametro);
            else pstmt.setString(1, (String) parametro);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(mapearConvite(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar convites", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return lista;
    }


    // ---------------------------- Update ----------------------------
    public void update(Convite convite) {
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_UPDATE)) {

            pstmt.setString(1, convite.getToken());
            pstmt.setString(2, convite.getStatusConvite());
            pstmt.setString(3, convite.getEmailConvidado());
            pstmt.setDate(4, convite.getDataCriacao() != null ? Date.valueOf(convite.getDataCriacao()) : null);
            pstmt.setDate(5, convite.getDataExpiracao() != null ? Date.valueOf(convite.getDataExpiracao()) : null);
            pstmt.setInt(6, convite.getDespensa().getId());
            pstmt.setInt(7, convite.getUsuario().getId());
            pstmt.setInt(8, convite.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar convite", e);
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
            throw new RuntimeException("Erro ao excluir convite", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }


    // ---------------------------- Função auxiliar ----------------------------
    private Convite mapearConvite(ResultSet rs) throws SQLException {
        Convite convite = new Convite();

        convite.setId(rs.getInt("convite_id"));
        convite.setToken(rs.getString("token"));
        convite.setStatusConvite(rs.getString("status_convite"));
        convite.setEmailConvidado(rs.getString("email_convidado"));

        Date dataCriacao = rs.getDate("data_criacao");
        convite.setDataCriacao(dataCriacao != null ? dataCriacao.toLocalDate() : null);

        Date dataExpiracao = rs.getDate("data_expiracao");
        convite.setDataExpiracao(dataExpiracao != null ? dataExpiracao.toLocalDate() : null);

        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("usuario_id"));
        usuario.setNome(rs.getString("usuario_nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setStatus(rs.getString("status"));
        usuario.setSenha(rs.getString("senha_hash"));
        convite.setUsuario(usuario);

        Despensa despensa = new Despensa();
        despensa.setId(rs.getInt("despensa_id"));
        despensa.setNome(rs.getString("despensa_nome"));
        despensa.setTipo(rs.getString("tipo"));

        Date dataDespensa = rs.getDate("despensa_data_criacao");
        despensa.setDataCriacao(dataDespensa != null ? dataDespensa.toLocalDate() : null);
        convite.setDespensa(despensa);

        return convite;
    }
}
