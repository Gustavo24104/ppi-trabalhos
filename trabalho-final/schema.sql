CREATE DATABASE IF NOT EXISTS yeardle;
CREATE USER IF NOT EXISTS 'yeardleADM'@'localhost' IDENTIFIED BY 'yeardlePass';
GRANT ALL ON yeardle.* TO 'yeardleADM'@'localhost';
FLUSH PRIVILEGES;
USE yeardle;

CREATE TABLE IF NOT EXISTS jogo (
  ano INT PRIMARY KEY CHECK (ano > 1900),
  dataJogada DATE
);

CREATE TABLE IF NOT EXISTS dica (
  dicaID INT PRIMARY KEY AUTO_INCREMENT,
  textoDica TEXT NOT NULL,
  anoDica INT,
  categoria ENUM('historia','cinema','literatura','musica','ciencias','esportes'),
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
  senha VARCHAR(150),
  ehAdm BOOL,
  categoriaFav ENUM('historia','cinema','literatura','musica','ciencias','esportes')
);

-- dados dos arquivos de imagens (foto de perfil e placar) enviados pelo usuário
CREATE TABLE IF NOT EXISTS upload (
  arquivoID INT PRIMARY KEY AUTO_INCREMENT,
  userID INT NULL,
  ano INT NULL,
  contexto ENUM('perfil', 'partida', 'jogo') NOT NULL,
  nomeOriginal VARCHAR(255) NOT NULL,
  nomeSalvo VARCHAR(255) NOT NULL,
  tipoConteudo VARCHAR(100),
  tamanhoBytes BIGINT NOT NULL,
  dataUpload TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (ano) REFERENCES jogo(ano) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS userGame (
    userID INT,
    ano INT,
    pontuacao INT NOT NULL,
    screenshot INT,
    PRIMARY KEY (userID, ano),
    FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (ano) REFERENCES jogo(ano) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (screenshot) REFERENCES upload(arquivoID) ON DELETE CASCADE ON UPDATE CASCADE
);




-- dados de teste, exigidos pelas chaves estrangeiras
INSERT IGNORE INTO jogo (ano) VALUES (1958);
INSERT IGNORE INTO users (pontosTotais, nomeReal, nickname, email, cidade, anoFav, categoriaFav, senha, ehAdm)
  VALUES (0, 'Joao da Silva', 'joaozinho', 'joao@gmail.com', 'Belo Horizonte', 1958, 'esportes', '1234', 0);

INSERT IGNORE INTO users (pontosTotais, nomeReal, nickname, email, cidade, anoFav, categoriaFav, senha, ehAdm)
  VALUES (0, 'Vinicius', 'adm', 'adm@gmail.com', 'Belo Horizonte', 1958, 'esportes', 'adm', 1);

-- bancos já existentes: o INSERT IGNORE acima não altera linhas que já existem
UPDATE users SET senha = '1234' WHERE nickname = 'joaozinho' AND senha IS NULL;
UPDATE users SET ehAdm = 0 WHERE nickname = 'joaozinho' AND ehAdm IS NULL;
UPDATE users SET senha = 'adm' WHERE nickname = 'adm' AND senha IS NULL;
UPDATE users SET ehAdm = 1 WHERE nickname = 'adm' AND ehAdm IS NULL;
