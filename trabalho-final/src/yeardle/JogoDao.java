package yeardle;

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
        String sql = "INSERT INTO jogo (ano, dataJogada) VALUES (?, ?)";

        try {
            // prepared statement para inserção
            PreparedStatement stmt = connection.prepareStatement(sql);

            // seta os valores
            stmt.setInt(1, jogo.getAno());
            stmt.setDate(2, converteData(jogo.getDataJogada()));

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
                jogo = monta(rs);
            }

            rs.close();
            stmt.close();
            return jogo;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // teoricamente devia ser único...
    public Jogo buscaPorDataJogada(Calendar data) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM jogo WHERE dataJogada=?");
            stmt.setDate(1, converteData(data));
            ResultSet rs = stmt.executeQuery();

            Jogo jogo = null;
            if (rs.next()) {
                jogo = monta(rs);
            }
            rs.close();
            stmt.close();
            return jogo;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    // menor dataJogada da tabela (null se nenhum jogo tem data)
    public Calendar menorDataJogada() {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT MIN(dataJogada) FROM jogo");
            ResultSet rs = stmt.executeQuery();

            Calendar data = null;
            if (rs.next() && rs.getDate(1) != null) {
                data = Calendar.getInstance();
                data.setTime(rs.getDate(1));
            }
            rs.close();
            stmt.close();
            return data;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Jogo> getLista() {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM jogo ORDER BY ano");
            ResultSet rs = stmt.executeQuery();

            List<Jogo> jogos = new ArrayList<Jogo>();
            while (rs.next()) {
                jogos.add(monta(rs));
            }

            rs.close();
            stmt.close();
            return jogos;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // data do jogo mais recente (<= hoje) que o usuario ainda nao jogou; null se nao houver
    public java.time.LocalDate proximaDataNaoJogada(int userID, java.time.LocalDate hoje) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT j.dataJogada FROM jogo j WHERE j.dataJogada IS NOT NULL AND j.dataJogada <= ? " +
                    "AND NOT EXISTS (SELECT 1 FROM userGame u WHERE u.userID = ? AND u.ano = j.ano) " +
                    "ORDER BY j.dataJogada DESC LIMIT 1");
            stmt.setDate(1, java.sql.Date.valueOf(hoje));
            stmt.setInt(2, userID);
            ResultSet rs = stmt.executeQuery();
            java.time.LocalDate r = rs.next() ? rs.getDate(1).toLocalDate() : null;
            rs.close();
            stmt.close();
            return r;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Jogo> getNaoJogados() {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM jogo WHERE dataJogada is NULL");
            ResultSet rs = stmt.executeQuery();

            List<Jogo> jogos = new ArrayList<Jogo>();
            while (rs.next()) {
                jogos.add(monta(rs));
            }

            rs.close();
            stmt.close();
            return jogos;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // retorna true se algum registro foi alterado
    public boolean altera(Jogo jogo) {
        String sql = "UPDATE jogo SET dataJogada=? WHERE ano=?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setDate(1, converteData(jogo.getDataJogada()));
            stmt.setInt(2, jogo.getAno());
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

    // cria o Jogo a partir da linha atual do ResultSet
    private Jogo monta(ResultSet rs) throws SQLException {
        Jogo jogo = new Jogo();
        jogo.setAno(rs.getInt("ano"));

        // montando a data através do Calendar
        if (rs.getDate("dataJogada") != null) {
            Calendar data = Calendar.getInstance();
            data.setTime(rs.getDate("dataJogada"));
            jogo.setDataJogada(data);
        }
        return jogo;
    }

    // a data é opcional: sem data, grava NULL
    private Date converteData(Calendar data) {
        return data == null ? null : new Date(data.getTimeInMillis());
    }
}
