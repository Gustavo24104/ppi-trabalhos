CREATE DATABASE IF NOT EXISTS yeardle;
CREATE USER IF NOT EXISTS 'yeardleADM'@'localhost' IDENTIFIED BY 'yeardlePass';
GRANT ALL ON yeardle.* TO 'yeardleADM'@'localhost';
FLUSH PRIVILEGES;
USE yeardle;

CREATE TABLE IF NOT EXISTS jogo (
  ano INT PRIMARY KEY CHECK (ano > 1900),
  dataJogada DATE,
  categoria ENUM('historia','cinema','literatura','musica','ciencias','esportes')
);
CREATE TABLE IF NOT EXISTS dica (
  dicaID INT PRIMARY KEY AUTO_INCREMENT,
  textoDica TEXT NOT NULL,
  anoDica INT,
  FOREIGN KEY (anoDica) REFERENCES jogo(ano) ON DELETE CASCADE ON UPDATE RESTRICT
);
CREATE TABLE IF NOT EXISTS users (
  userID INT PRIMARY KEY AUTO_INCREMENT,
  pontosTotais INT NOT NULL,
  nomeReal VARCHAR(100) NOT NULL,
  nickname VARCHAR(15) UNIQUE NOT NULL,
  email VARCHAR(50) UNIQUE NOT NULL,
  cidade VARCHAR(100) NOT NULL,
  anoFav INT CHECK (anoFav > 1900),
  categoriaFav ENUM('historia','cinema','literatura','musica','ciencias','esportes')
);
CREATE TABLE IF NOT EXISTS userGame (
  userID INT,
  ano INT,
  pontuacao INT NOT NULL,
  PRIMARY KEY (userID, ano),
  FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (ano) REFERENCES jogo(ano) ON DELETE CASCADE ON UPDATE CASCADE
);

-- dados de teste, exigidos pelas chaves estrangeiras
INSERT IGNORE INTO jogo (ano, categoria) VALUES (1958, 'esportes');
INSERT IGNORE INTO users (pontosTotais, nomeReal, nickname, email, cidade, anoFav, categoriaFav)
  VALUES (0, 'Joao da Silva', 'joaozinho', 'joao@gmail.com', 'Belo Horizonte', 1958, 'esportes');
