// assume que o banco de dados ja existe
// codigo pra criar o BD:

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    static String databaseName = "yeardle";
    static String username = "yeardleADM";
    static String password = "yeardlePass";
    static String url = "jdbc:mysql://localhost:3306/" + databaseName + "?useSSL=false&useTimezone=true&serverTimezone=UTC";

    public Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

/*

CREATE DATABASE yeardle;

USE yeardle;

GRANT ALL ON yeardle.* TO yeardleADM IDENTIFIED BY 'yeardlePass';

FLUSH PRIVILEGES;

CREATE TABLE jogo (
      ano INT PRIMARY KEY CHECK (ano > 1900),
      dataJogada DATE,
      categoria ENUM('historia', 'cinema', 'literatura', 'musica', 'ciencias', 'esportes')
);

CREATE TABLE dica (
    dicaID INT PRIMARY KEY AUTO_INCREMENT,
    textoDica TEXT NOT NULL,
    anoDica INT,
    FOREIGN KEY (anoDica) REFERENCES jogo(ano) ON DELETE CASCADE ON UPDATE RESTRICT
);

CREATE TABLE users (
    userID INT PRIMARY KEY AUTO_INCREMENT,
    pontosTotais INT NOT NULL,
    nomeReal VARCHAR(100) NOT NULL,
    nickname VARCHAR(15) UNIQUE NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    anoFav INT CHECK (anoFav > 1900),
    categoriaFav ENUM('historia', 'cinema', 'literatura', 'musica', 'ciencias', 'esportes')
);

CREATE TABLE userGame(
    userID INT,
    ano INT,
    pontuacao INT NOT NULL,
    PRIMARY KEY (userID, ano),
    FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (ano) REFERENCES jogo(ano) ON DELETE CASCADE ON UPDATE CASCADE
);

 */
