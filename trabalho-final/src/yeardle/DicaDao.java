package yeardle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DicaDao {

    // a conexão com o banco de dados
    private Connection connection;

    public DicaDao() {
        this.connection = new ConnectionFactory().getConnection();
    }

    public void adiciona(Dica dica) {
        // dicaID <= 0: deixa o AUTO_INCREMENT gerar o valor
        boolean semId = dica.getDicaID() <= 0;
        String sql = semId
                ? "INSERT INTO dica (categoria, textoDica, anoDica) VALUES (?, ?, ?)"
                : "INSERT INTO dica (categoria, textoDica, anoDica, dicaID) VALUES (?, ?, ?, ?)";

        try {
            // prepared statement para inserção
            PreparedStatement stmt = connection.prepareStatement(sql);

            // seta os valores
            stmt.setString(1, dica.getCategoria());
            stmt.setString(2, dica.getTextoDica());
            setAno(stmt, 3, dica.getAnoDica());
            if (!semId) {
                stmt.setInt(4, dica.getDicaID());
            }

            // executa
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dica busca(int dicaID) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM dica WHERE dicaID=?");
            stmt.setInt(1, dicaID);
            ResultSet rs = stmt.executeQuery();

            Dica dica = null;
            if (rs.next()) {
                dica = new Dica();
                dica.setDicaID(rs.getInt("dicaID"));
                dica.setTextoDica(rs.getString("textoDica"));
                dica.setAnoDica(rs.getInt("anoDica"));
                dica.setCategoria(rs.getString("categoria"));
            }

            rs.close();
            stmt.close();
            return dica;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Dica> buscaPorJogo(int ano) {
        try {
            PreparedStatement stmt = this.connection.prepareStatement("SELECT * FROM dica WHERE anoDica=?");
            stmt.setInt(1, ano);
            ResultSet rs = stmt.executeQuery();

            List<Dica> dicas = new ArrayList<Dica>();

            while (rs.next()) {
                // criando o objeto Dica
                Dica dica = new Dica();
                dica.setDicaID(rs.getInt("dicaID"));
                dica.setTextoDica(rs.getString("textoDica"));
                dica.setAnoDica(rs.getInt("anoDica"));
                dica.setCategoria(rs.getString("categoria"));

                // adicionando o objeto à lista
                dicas.add(dica);
            }

            rs.close();
            stmt.close();

            return dicas;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Dica> getLista() {
        try {
            PreparedStatement stmt = this.connection.prepareStatement("SELECT * FROM dica ORDER BY dicaID");
            ResultSet rs = stmt.executeQuery();

            List<Dica> dicas = new ArrayList<Dica>();

            while (rs.next()) {
                // criando o objeto Dica
                Dica dica = new Dica();
                dica.setDicaID(rs.getInt("dicaID"));
                dica.setTextoDica(rs.getString("textoDica"));
                dica.setAnoDica(rs.getInt("anoDica"));
                dica.setCategoria(rs.getString("categoria"));

                // adicionando o objeto à lista
                dicas.add(dica);
            }

            rs.close();
            stmt.close();

            return dicas;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // retorna true se algum registro foi alterado
    public boolean altera(Dica dica) {
        String sql = "UPDATE dica SET categoria=?, textoDica=?, anoDica=? WHERE dicaID=?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, dica.getCategoria());
            stmt.setString(2, dica.getTextoDica());
            setAno(stmt, 3, dica.getAnoDica());
            stmt.setInt(4, dica.getDicaID());
            int linhas = stmt.executeUpdate();
            stmt.close();
            return linhas > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // retorna true se algum registro foi removido
    public boolean remove(int dicaID) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM dica WHERE dicaID=?");
            stmt.setInt(1, dicaID);
            int linhas = stmt.executeUpdate();
            stmt.close();
            return linhas > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeTudo() {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM dica");
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void fechaConexao() throws SQLException {
        connection.close();
    }

    // anoDica é opcional na tabela: 0 grava NULL (e NULL é lido como 0)
    private void setAno(PreparedStatement stmt, int indice, int ano) throws SQLException {
        if (ano > 0) {
            stmt.setInt(indice, ano);
        } else {
            stmt.setNull(indice, Types.INTEGER);
        }
    }
}
