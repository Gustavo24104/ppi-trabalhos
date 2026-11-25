package yeardle;

import java.sql.*;

public class ProgressoDao {

    // a conexão com o banco de dados
    private Connection connection;

    public ProgressoDao() {
        this.connection = new ConnectionFactory().getConnection();
    }

    // retorna o progresso do usuario nesse jogo, ou null se nao ha
    public Progresso busca(int userID, int ano) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT tentativas, dicasReveladas, chutes FROM progresso WHERE userID=? AND ano=?");
            stmt.setInt(1, userID);
            stmt.setInt(2, ano);
            ResultSet rs = stmt.executeQuery();
            Progresso p = null;
            if (rs.next()) {
                p = new Progresso();
                p.setTentativas(rs.getInt("tentativas"));
                p.setDicasReveladas(rs.getInt("dicasReveladas"));
                p.setChutes(rs.getString("chutes"));
            }
            rs.close();
            stmt.close();
            return p;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void salva(int userID, int ano, Progresso p) {
        String sql = "INSERT INTO progresso (userID, ano, tentativas, dicasReveladas, chutes) VALUES (?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE tentativas=VALUES(tentativas), dicasReveladas=VALUES(dicasReveladas), chutes=VALUES(chutes)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, userID);
            stmt.setInt(2, ano);
            stmt.setInt(3, p.getTentativas());
            stmt.setInt(4, p.getDicasReveladas());
            stmt.setString(5, p.getChutes());
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(int userID, int ano) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM progresso WHERE userID=? AND ano=?");
            stmt.setInt(1, userID);
            stmt.setInt(2, ano);
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
