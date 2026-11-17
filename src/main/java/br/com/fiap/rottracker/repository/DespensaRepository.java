
package br.com.fiap.rottracker.repository;

import br.com.fiap.rottracker.model.Despensa;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DespensaRepository
{
    private final DataSource dataSource;
    private final ProdutoRepository produtoRepository;
    private final MembroRepository membroRepository;

    public DespensaRepository(
            DataSource dataSource,
            ProdutoRepository produtoRepository,
            MembroRepository membroRepository) {
        this.dataSource = dataSource;
        this.produtoRepository = produtoRepository;
        this.membroRepository = membroRepository;
    }

    private static final String SQL_INSERT =
            "INSERT INTO tb_despensas(nome, tipo, data_criacao) VALUES (?, ?, ?)";

    private static final String SQL_SELECT_ALL =
            "SELECT despensa_id, nome, tipo, data_criacao FROM tb_despensas ORDER BY despensa_id";

    private static final String SQL_SELECT_ID =
            "SELECT despensa_id, nome, tipo, data_criacao FROM tb_despensas WHERE despensa_id = ?";

    private static final String SQL_SELECT_NOME =
            "SELECT despensa_id, nome, tipo, data_criacao FROM tb_despensas WHERE lower(nome) LIKE ? ORDER BY nome";

    private static final String SQL_SELECT_USUARIO =
            "SELECT d.despensa_id, d.nome, d.tipo, d.data_criacao " +
                    "FROM tb_despensas d " +
                    "INNER JOIN tb_membros m ON d.despensa_id = m.despensa_id " +
                    "WHERE m.usuario_id = ? ORDER BY d.despensa_id";

    private static final String SQL_UPDATE =
            "UPDATE tb_despensas SET nome = ?, tipo = ?, data_criacao = ? WHERE despensa_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM tb_despensas WHERE despensa_id = ?";


    // ---------------------------- Create ----------------------------
    public void create(Despensa despensa) {
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_INSERT, new String[]{"DESPENSA_ID"})) {

            pstmt.setString(1, despensa.getNome());
            pstmt.setString(2, despensa.getTipo());
            pstmt.setDate(3, despensa.getDataCriacao() != null ? Date.valueOf(despensa.getDataCriacao()) : null);

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) despensa.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir despensa", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }


    // ---------------------------- Read ----------------------------
    public List<Despensa> readAll() {
        List<Despensa> lista = new ArrayList<>();
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) lista.add(mapearDespensa(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar despensas", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return lista;
    }


    public Despensa read(int id) {
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_ID)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Despensa despensa = mapearDespensa(rs);

                    despensa.setProdutos(produtoRepository.readByDespensa(id));
                    despensa.setMembros(membroRepository.readByDespensa(id));

                    return despensa;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar despensa por ID", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return null;
    }


    public List<Despensa> readByName(String name) {
        List<Despensa> lista = new ArrayList<>();
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_NOME)) {
            pstmt.setString(1, "%" + name.toLowerCase() + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(mapearDespensa(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar despensas por nome", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return lista;
    }


    public List<Despensa> readByUsuario(int usuarioId) {
        List<Despensa> lista = new ArrayList<>();
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_USUARIO)) {
            pstmt.setInt(1, usuarioId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(mapearDespensa(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar despensas por usuário", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return lista;
    }


    // ---------------------------- Update ----------------------------
    public void update(Despensa despensa) {
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_UPDATE)) {

            pstmt.setString(1, despensa.getNome());
            pstmt.setString(2, despensa.getTipo());
            pstmt.setDate(3, despensa.getDataCriacao() != null ? Date.valueOf(despensa.getDataCriacao()) : null);
            pstmt.setInt(4, despensa.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar despensa", e);
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
            throw new RuntimeException("Erro ao excluir despensa", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }


    // ---------------------------- Função auxiliar ----------------------------
    private Despensa mapearDespensa(ResultSet rs) throws SQLException {
        Despensa despensa = new Despensa();

        despensa.setId(rs.getInt("despensa_id"));
        despensa.setNome(rs.getString("nome"));
        despensa.setTipo(rs.getString("tipo"));

        Date dataCriacao = rs.getDate("data_criacao");
        despensa.setDataCriacao(dataCriacao != null ? dataCriacao.toLocalDate() : null);

        return despensa;
    }
}
