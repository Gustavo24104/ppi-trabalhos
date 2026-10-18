// assume que o banco de dados ja existe
// codigo pra criar o BD:

import java.sql.*;
import java.util.Calendar;

public class BancoDeDados {
    Connection cnt;

    public BancoDeDados() {
        try {
            String url = "jdbc:mysql://localhost:3306/yeardle";
            cnt = DriverManager.getConnection(url, "yeardleADM", "yeardlePass");
            System.out.println("Conectado com sucesso!");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void insereDados() throws SQLException {
        String insereJogo = "INSERT INTO jogo (ano, categoria) VALUES (?, ?)";
        String insereDica = "INSERT INTO dica (textoDica, anoDica, dicaID) VALUES (?, ?, ?)";
        String insereUser = "INSERT INTO users (pontosTotais, nomeReal, nickname, email, " +
                "cidade, anoFav, categoriaFav, userID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insereUserGame = "INSERT INTO userGame (userID, ano, pontuacao) VALUES (?, ?, ?)";

        PreparedStatement psJogo = cnt.prepareStatement(insereJogo);
        psJogo.setInt(1, 1958);
        psJogo.setString(2, "musica");

        PreparedStatement psDica = cnt.prepareStatement(insereDica);
        psDica.setString(1, "Michael Jackson nasceu nesse ano");
        psDica.setInt(2, 1958);
        psDica.setInt(3, 1);

        PreparedStatement psUser = cnt.prepareStatement(insereUser);
        psUser.setInt(1, 0);
        psUser.setString(2, "Joao da Silva");
        psUser.setString(3, "joaozinho");
        psUser.setString(4, "joao@gmail.com");
        psUser.setString(5, "Belo Horizonate");
        psUser.setInt(6, 1923);
        psUser.setString(7, "historia");
        psUser.setInt(8, 1);

        PreparedStatement psUserGame = cnt.prepareStatement(insereUserGame);
        psUserGame.setInt(1, 1);
        psUserGame.setInt(2, 1958);
        psUserGame.setInt(3, 0);

        psJogo.execute();
        psDica.execute();
        psUser.execute();
        psUserGame.execute();

        psUserGame.close();
        psUser.close();
        psDica.close();
        psJogo.close();

    }

    public void alteraDados() throws SQLException {
        String insereJogo = "UPDATE jogo SET dataJogada=? WHERE ano=1958";
        String insereDica = "UPDATE dica SET textoDica=? WHERE anoDica=1958";
        String insereUser = "UPDATE users SET categoriaFav=?, nickname=? WHERE userID=1";
        String insereUserGame = "UPDATE userGame SET pontuacao=10 WHERE userID=1";

        PreparedStatement psJogo = cnt.prepareStatement(insereJogo);
        psJogo.setDate(1, new java.sql.Date(Calendar.getInstance().getTimeInMillis()));

        PreparedStatement psDica = cnt.prepareStatement(insereDica);
        psDica.setString(1, "Brasil ganhou a copa pela primeira vez");

        PreparedStatement psUser = cnt.prepareStatement(insereUser);
        psUser.setString(1, "musica");
        psUser.setString(2, "joaozao");

        PreparedStatement psUserGame = cnt.prepareStatement(insereUserGame);

        psJogo.execute();
        psDica.execute();
        psUser.execute();
        psUserGame.execute();

        psUserGame.close();
        psUser.close();
        psDica.close();
        psJogo.close();
    }

    public void imprimeDados() throws SQLException {
        String insereJogo = "SELECT * FROM jogo";
        String insereDica = "SELECT * FROM dica";
        String insereUser = "SELECT * FROM users";
        String insereUserGame = "SELECT * FROM userGame";
        String selComJuncao = "SELECT pontuacao, nickname, ano FROM userGame ug, users us WHERE us.userID = ug.userID";

        ResultSet rs;

        PreparedStatement psJogo = cnt.prepareStatement(insereJogo);
        rs = psJogo.executeQuery();
        while(rs.next()) {
            System.out.println("Ano do jogo : " + rs.getString("ano") + "\nCategoria: "
                    + rs.getString("categoria"));
        }

        System.out.println();
        PreparedStatement psDica = cnt.prepareStatement(insereDica);
        rs = psDica.executeQuery();
        while(rs.next()) {
            System.out.println("Dica: " + rs.getString("textoDica"));
        }

        System.out.println();
        PreparedStatement psUser = cnt.prepareStatement(insereUser);
        rs = psUser.executeQuery();
        while(rs.next()) {
            System.out.println("Nome do usuario: " + rs.getString("nomeReal") + "\nApelido: "
                    + rs.getString("nickname") + "\nemail: " + rs.getString("email") +
                    "\nAno favorito: " + rs.getString("anoFav") + "\nCategoria favorita: " +
                    rs.getString("categoriaFav"));
        }

        System.out.println();
        PreparedStatement psComJuncao = cnt.prepareStatement(selComJuncao);
        rs = psComJuncao.executeQuery();
        while(rs.next()) {
            System.out.println("Jogador : " + rs.getString("nickname") + "\nAno: "
                    + rs.getString("ano") + "\nPontuacao obtida: " + rs.getString("pontuacao"));
        }

        psComJuncao.close();
        psUser.close();
        psDica.close();
        psJogo.close();

    }

    public void deletaDados() throws SQLException {
        String insereJogo = "DELETE FROM jogo";
        String insereDica = "DELETE FROM dica";
        String insereUser = "DELETE FROM users";
        String insereUserGame = "DELETE FROM userGame";

        PreparedStatement psJogo = cnt.prepareStatement(insereJogo);
        PreparedStatement psDica = cnt.prepareStatement(insereDica);
        PreparedStatement psUser = cnt.prepareStatement(insereUser);
        PreparedStatement psUserGame = cnt.prepareStatement(insereUserGame);

        psUserGame.execute();
        psUser.execute();
        psDica.execute();
        psJogo.execute();

        psUserGame.close();
        psUser.close();
        psDica.close();
        psJogo.close();

    }
}

/*

CREATE OR REPLACE TABLE jogo (
      ano INT PRIMARY KEY CHECK (ano > 1900),
      dataJogada DATE,
      categoria ENUM('historia', 'cinema', 'literatura', 'musica', 'ciencias', 'esportes')
);

CREATE OR REPLACE table dica (
    dicaID INT PRIMARY KEY AUTO_INCREMENT,
    textoDica TEXT NOT NULL,
    anoDica INT REFERENCES jogo(ano) ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE OR REPLACE TABLE users (
    userID INT PRIMARY KEY AUTO_INCREMENT,
    pontosTotais INT NOT NULL,
    nomeReal VARCHAR(100) NOT NULL,
    nickname VARCHAR(15) UNIQUE NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    anoFav INT CHECK (anoFav > 1900),
    categoriaFav ENUM('historia', 'cinema', 'literatura', 'musica', 'ciencias', 'esportes')
);

CREATE OR REPLACE TABLE userGame(
    userID INT REFERENCES users(userID) ON DELETE CASCADE ON UPDATE CASCADE,
    ano INT REFERENCES jogo(ano) ON DELETE CASCADE ON UPDATE CASCADE,
    pontuacao INT NOT NULL,
    PRIMARY KEY (userID, ano)
);

 */
