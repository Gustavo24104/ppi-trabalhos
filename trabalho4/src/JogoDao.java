import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class JogoDao {

    // a conexão com o banco de dados
    private Connection connection;

    public JogoDao() {
        this.connection = new ConnectionFactory().getConnection();
    }

    public void adiciona(Jogo jogo) {
        String sql = "INSERT INTO jogo (ano, categoria) VALUES (?, ?)";

        try {
            // prepared statement para inserção
            PreparedStatement stmt = connection.prepareStatement(sql);

            // seta os valores
            stmt.setInt(1, jogo.getAno());
            stmt.setString(2, jogo.getCategoria());

            // executa
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Jogo> getLista() {
        try {
            PreparedStatement stmt = this.connection.prepareStatement("SELECT * FROM jogo");
            ResultSet rs = stmt.executeQuery();

            List<Jogo> jogos = new ArrayList<Jogo>();

            while (rs.next()) {
                // criando o objeto Jogo
                Jogo jogo = new Jogo();
                jogo.setAno(rs.getInt("ano"));
                jogo.setCategoria(rs.getString("categoria"));

                // montando a data através do Calendar
                if (rs.getDate("dataJogada") != null) {
                    Calendar data = Calendar.getInstance();
                    data.setTime(rs.getDate("dataJogada"));
                    jogo.setDataJogada(data);
                }

                // adicionando o objeto à lista
                jogos.add(jogo);
            }

            rs.close();
            stmt.close();

            return jogos;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void altera(Jogo jogo) {
        String sql = "UPDATE jogo SET dataJogada=? WHERE ano=1958";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setDate(1, new Date(jogo.getDataJogada().getTimeInMillis()));
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove() {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM jogo");
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void fechaConexao() throws SQLException {
        connection.close();
    }
}
