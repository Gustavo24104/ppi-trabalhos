import java.sql.*;
import java.util.Calendar;

public class JogoDao {

    // a conexão com o banco de dados
    private Connection connection;

    public JogoDao() {
        this.connection = new ConnectionFactory().getConnection();
    }

    public void adiciona(Jogo jogo) {
        String sql = "INSERT INTO jogo (ano, dataJogada, categoria) VALUES (?, ?, ?)";

        try {
            // prepared statement para inserção
            PreparedStatement stmt = connection.prepareStatement(sql);

            // seta os valores
            stmt.setInt(1, jogo.getAno());
            stmt.setDate(2, converteData(jogo.getDataJogada()));
            stmt.setString(3, jogo.getCategoria());

            // executa
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Jogo busca(int ano) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM jogo WHERE ano=?");
            stmt.setInt(1, ano);
            ResultSet rs = stmt.executeQuery();

            Jogo jogo = null;
            if (rs.next()) {
                jogo = new Jogo();
                jogo.setAno(rs.getInt("ano"));
                jogo.setCategoria(rs.getString("categoria"));

                // montando a data através do Calendar
                if (rs.getDate("dataJogada") != null) {
                    Calendar data = Calendar.getInstance();
                    data.setTime(rs.getDate("dataJogada"));
                    jogo.setDataJogada(data);
                }
            }

            rs.close();
            stmt.close();
            return jogo;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // retorna true se algum registro foi alterado
    public boolean altera(Jogo jogo) {
        String sql = "UPDATE jogo SET dataJogada=?, categoria=? WHERE ano=?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setDate(1, converteData(jogo.getDataJogada()));
            stmt.setString(2, jogo.getCategoria());
            stmt.setInt(3, jogo.getAno());
            int linhas = stmt.executeUpdate();
            stmt.close();
            return linhas > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // retorna true se algum registro foi removido
    public boolean remove(int ano) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM jogo WHERE ano=?");
            stmt.setInt(1, ano);
            int linhas = stmt.executeUpdate();
            stmt.close();
            return linhas > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void fechaConexao() throws SQLException {
        connection.close();
    }

    // a data é opcional: sem data, grava NULL
    private Date converteData(Calendar data) {
        return data == null ? null : new Date(data.getTimeInMillis());
    }
}
