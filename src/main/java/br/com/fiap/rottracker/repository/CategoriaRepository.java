
package br.com.fiap.rottracker.repository;

import br.com.fiap.rottracker.model.Categoria;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class    CategoriaRepository
{
    private final DataSource dataSource;
    public CategoriaRepository(DataSource dataSource) { this.dataSource = dataSource; }

    private static final String SQL_INSERT =
            "INSERT INTO tb_categorias(nome, perecivel) VALUES (?, ?)";

    private static final String SQL_SELECT_ALL =
            "SELECT categoria_id, nome, perecivel FROM tb_categorias ORDER BY categoria_id";

    private static final String SQL_SELECT_ID =
            "SELECT categoria_id, nome, perecivel FROM tb_categorias WHERE categoria_id = ?";

    private static final String SQL_SELECT_NOME =
            "SELECT * FROM tb_categorias WHERE LOWER(nome) = LOWER(?)";

    private static final String SQL_UPDATE =
            "UPDATE tb_categorias SET nome = ?, perecivel = ? WHERE categoria_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM tb_categorias WHERE categoria_id = ?";


    // ---------------------------- Create ----------------------------
    public void create(Categoria categoria) {
        Connection con = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = con.prepareStatement(SQL_INSERT, new String[]{"CATEGORIA_ID"})) {

            pstmt.setString(1, categoria.getNome());
            pstmt.setInt(2, categoria.isPerecivel() ? 1 : 0);

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) categoria.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir categoria", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }


    // ---------------------------- Read ----------------------------
    public List<Categoria> readAll() {
        List<Categoria> lista = new ArrayList<>();
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) lista.add(mapearCategoria(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar categorias", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return lista;
    }


    public Categoria read(int id) {
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_ID)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapearCategoria(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar categoria por ID", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return null;
    }


    public List<Categoria> readByName(String name) {
        List<Categoria> lista = new ArrayList<>();
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_NOME)) {
            pstmt.setString(1, name.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(mapearCategoria(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar categorias por nome", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return lista;
    }


    // ---------------------------- Update ----------------------------
    public void update(Categoria categoria) {
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_UPDATE)) {
            pstmt.setString(1, categoria.getNome());
            pstmt.setInt(2, categoria.isPerecivel() ? 1 : 0);
            pstmt.setInt(3, categoria.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar categoria", e);
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
            throw new RuntimeException("Erro ao excluir categoria", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }


    // ---------------------------- Função auxiliar ----------------------------
    private Categoria mapearCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();

        categoria.setId(rs.getInt("categoria_id"));
        categoria.setNome(rs.getString("nome"));
        categoria.setPerecivel(rs.getInt("perecivel") == 1);

        return categoria;
    }
}
