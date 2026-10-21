import java.sql.*;

public class UserGameDao {

    // a conexão com o banco de dados
    private Connection connection;

    public UserGameDao() {
        this.connection = new ConnectionFactory().getConnection();
    }

    public void adiciona(UserGame userGame) {
        String sql = "INSERT INTO userGame (userID, ano, pontuacao) VALUES (?, ?, ?)";

        try {
            // prepared statement para inserção
            PreparedStatement stmt = connection.prepareStatement(sql);

            // seta os valores
            stmt.setInt(1, userGame.getUserID());
            stmt.setInt(2, userGame.getAno());
            stmt.setInt(3, userGame.getPontuacao());

            // executa
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserGame busca(int userID, int ano) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM userGame WHERE userID=? AND ano=?");
            stmt.setInt(1, userID);
            stmt.setInt(2, ano);
            ResultSet rs = stmt.executeQuery();

            UserGame userGame = null;
            if (rs.next()) {
                userGame = new UserGame();
                userGame.setUserID(rs.getInt("userID"));
                userGame.setAno(rs.getInt("ano"));
                userGame.setPontuacao(rs.getInt("pontuacao"));
            }

            rs.close();
            stmt.close();
            return userGame;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // retorna true se algum registro foi alterado
    public boolean altera(UserGame userGame) {
        String sql = "UPDATE userGame SET pontuacao=? WHERE userID=? AND ano=?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, userGame.getPontuacao());
            stmt.setInt(2, userGame.getUserID());
            stmt.setInt(3, userGame.getAno());
            int linhas = stmt.executeUpdate();
            stmt.close();
            return linhas > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // retorna true se algum registro foi removido
    public boolean remove(int userID, int ano) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM userGame WHERE userID=? AND ano=?");
            stmt.setInt(1, userID);
            stmt.setInt(2, ano);
            int linhas = stmt.executeUpdate();
            stmt.close();
            return linhas > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void imprimeComJuncao() {
        String sql = "SELECT pontuacao, nickname, ano FROM userGame ug, users us WHERE us.userID = ug.userID";

        try {
            PreparedStatement stmt = this.connection.prepareStatement(sql);

            // executa um select
            ResultSet rs = stmt.executeQuery();

            // itera no ResultSet
            while (rs.next()) {
                System.out.println("Jogador : " + rs.getString("nickname") + "\nAno: "
                        + rs.getString("ano") + "\nPontuacao obtida: " + rs.getString("pontuacao"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void altera() {
        String sql = "UPDATE userGame SET pontuacao=10 WHERE userID=1";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove() {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM userGame");
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
