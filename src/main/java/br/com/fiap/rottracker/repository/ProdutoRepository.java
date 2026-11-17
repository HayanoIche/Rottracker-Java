
package br.com.fiap.rottracker.repository;

import br.com.fiap.rottracker.model.Produto;
import br.com.fiap.rottracker.model.Categoria;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProdutoRepository
{
    private final DataSource dataSource;
    public ProdutoRepository(DataSource dataSource) { this.dataSource = dataSource; }

    private static final String SQL_INSERT =
            "INSERT INTO tb_produtos(descricao, quantidade, unidade, data_compra, validade, congelado, despensa_id, categoria_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_BASE =
            "SELECT p.produto_id, p.descricao, p.quantidade, p.unidade, p.data_compra, p.validade, p.congelado, " +
                    "c.categoria_id, c.nome AS categoria_nome, c.perecivel " +
                    "FROM tb_produtos p INNER JOIN tb_categorias c ON p.categoria_id = c.categoria_id ";

    private static final String SQL_SELECT_ALL =
            SQL_SELECT_BASE + "ORDER BY p.produto_id";

    private static final String SQL_SELECT_ID =
            SQL_SELECT_BASE + "WHERE p.produto_id = ?";

    private static final String SQL_SELECT_NOME =
            SQL_SELECT_BASE + "WHERE lower(p.descricao) LIKE ? ORDER BY p.descricao";

    private static final String SQL_SELECT_DESPENSA =
            SQL_SELECT_BASE + "WHERE p.despensa_id = ? ORDER BY p.produto_id";

    private static final String SQL_UPDATE =
            "UPDATE tb_produtos SET descricao = ?, quantidade = ?, unidade = ?, data_compra = ?, validade = ?, congelado = ?, despensa_id = ?, categoria_id = ? WHERE produto_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM tb_produtos WHERE produto_id = ?";


    // ---------------------------- Create ----------------------------
    public void create(Produto produto, int despensaId) {
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_INSERT, new String[]{"PRODUTO_ID"})) {

            pstmt.setString(1, produto.getDescricao());
            pstmt.setDouble(2, produto.getQuantidade());
            pstmt.setString(3, produto.getUnidade());
            pstmt.setDate(4, produto.getDataCompra() != null ? Date.valueOf(produto.getDataCompra()) : null);
            pstmt.setDate(5, produto.getValidade() != null ? Date.valueOf(produto.getValidade()) : null);
            pstmt.setInt(6, produto.isCongelado() ? 1 : 0);
            pstmt.setInt(7, despensaId);
            pstmt.setInt(8, produto.getCategoria().getId());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) produto.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir produto", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }


    // ---------------------------- Read ----------------------------
    public List<Produto> readAll() {
        List<Produto> lista = new ArrayList<>();
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) lista.add(mapearProduto(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return lista;
    }


    public Produto read(int id) {
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_ID)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapearProduto(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto por ID", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return null;
    }


    public List<Produto> readByName(String name) {
        List<Produto> lista = new ArrayList<>();
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_NOME)) {
            pstmt.setString(1, "%" + name.toLowerCase() + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(mapearProduto(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produtos por descrição", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return lista;
    }


    public List<Produto> readByDespensa(int despensaId) {
        List<Produto> lista = new ArrayList<>();
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_DESPENSA)) {
            pstmt.setInt(1, despensaId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(mapearProduto(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produtos por despensa", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
        return lista;
    }


    // ---------------------------- Update ----------------------------
    public void update(Produto produto, int despensaId) {
        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = con.prepareStatement(SQL_UPDATE)) {

            pstmt.setString(1, produto.getDescricao());
            pstmt.setDouble(2, produto.getQuantidade());
            pstmt.setString(3, produto.getUnidade());
            pstmt.setDate(4, produto.getDataCompra() != null ? Date.valueOf(produto.getDataCompra()) : null);
            pstmt.setDate(5, produto.getValidade() != null ? Date.valueOf(produto.getValidade()) : null);
            pstmt.setInt(6, produto.isCongelado() ? 1 : 0);
            pstmt.setInt(7, despensaId);
            pstmt.setInt(8, produto.getCategoria().getId());
            pstmt.setInt(9, produto.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto", e);
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
            throw new RuntimeException("Erro ao excluir produto", e);
        } finally {
            DataSourceUtils.releaseConnection(con, dataSource);
        }
    }


    // ---------------------------- Função auxiliar ----------------------------
    private Produto mapearProduto(ResultSet rs) throws SQLException {
        Produto produto = new Produto();

        produto.setId(rs.getInt("produto_id"));
        produto.setDescricao(rs.getString("descricao"));
        produto.setQuantidade(rs.getDouble("quantidade"));
        produto.setUnidade(rs.getString("unidade"));

        Date dataCompra = rs.getDate("data_compra");
        produto.setDataCompra(dataCompra != null ? dataCompra.toLocalDate() : null);

        Date validade = rs.getDate("validade");
        produto.setValidade(validade != null ? validade.toLocalDate() : null);

        produto.setCongelado(rs.getInt("congelado") == 1);

        Categoria categoria = new Categoria();
        categoria.setId(rs.getInt("categoria_id"));
        categoria.setNome(rs.getString("categoria_nome"));
        categoria.setPerecivel(rs.getInt("perecivel") == 1);

        produto.setCategoria(categoria);

        return produto;
    }
}
