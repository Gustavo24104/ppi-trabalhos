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

-- dados dos arquivos de imagens (foto de perfil e foto marcante do ano) enviados pelo usuário
CREATE TABLE IF NOT EXISTS upload (
  arquivoID INT PRIMARY KEY AUTO_INCREMENT,
  userID INT NULL,
  ano INT NULL,
  contexto ENUM('perfil', 'jogo') NOT NULL,
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
    PRIMARY KEY (userID, ano),
    FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (ano) REFERENCES jogo(ano) ON DELETE CASCADE ON UPDATE CASCADE
);




-- dados de teste, exigidos pelas chaves estrangeiras
-- ---------------------------------------------------------------
-- Anos de jogo e dicas
-- Padrao: cada ano tem exatamente 5 dicas, inseridas sempre na mesma
-- ordem de categorias (a ordem de insercao define a ordem exibida):
--   1 esportes, 2 cinema, 3 musica, 4 ciencias, 5 historia
-- A especificidade cresce da dica 1 (mais vaga) ate a dica 5 (mais direta).
-- O DELETE antes de cada bloco mantem o script re-executavel sem duplicar.
-- ---------------------------------------------------------------
INSERT IGNORE INTO jogo (ano) VALUES
  (1930), (1950), (1958), (1969), (1986), (1992), (1994), (2002), (2014), (2020);

-- 1930
DELETE FROM dica WHERE anoDica = 1930;
INSERT INTO dica (textoDica, anoDica, categoria) VALUES
  ('Treze seleções disputaram a primeira edição de um torneio mundial de futebol, vencido pelo país anfitrião.', 1930, 'esportes'),
  ('Estreou o filme antiguerra ''Nada de Novo no Front'', baseado no romance de Erich Maria Remarque.', 1930, 'cinema'),
  ('Noel Rosa lançou seu primeiro sucesso, o samba ''Com que roupa?''.', 1930, 'musica'),
  ('Clyde Tombaugh descobriu Plutão, então considerado o nono planeta do Sistema Solar.', 1930, 'ciencias'),
  ('Getúlio Vargas assumiu o poder após a Revolução que pôs fim à República Velha no Brasil.', 1930, 'historia');

-- 1950
DELETE FROM dica WHERE anoDica = 1950;
INSERT INTO dica (textoDica, anoDica, categoria) VALUES
  ('Um torneio mundial de futebol voltou a ser disputado após um intervalo de 12 anos causado pela guerra.', 1950, 'esportes'),
  ('Estreou ''Crepúsculo dos Deuses'', de Billy Wilder, sobre uma atriz decadente de Hollywood.', 1950, 'cinema'),
  ('Nat King Cole liderou as paradas americanas com ''Mona Lisa'', um de seus maiores sucessos.', 1950, 'musica'),
  ('O astrônomo Jan Oort propôs a existência de uma nuvem de cometas nos confins do Sistema Solar.', 1950, 'ciencias'),
  ('Foi inaugurada a TV Tupi, a primeira emissora de televisão do Brasil.', 1950, 'historia');

-- 1958
DELETE FROM dica WHERE anoDica = 1958;
INSERT INTO dica (textoDica, anoDica, categoria) VALUES
  ('Um jovem de 17 anos brilhou numa Copa do Mundo disputada na Suécia.', 1958, 'esportes'),
  ('Estreou ''Um Corpo que Cai'' (Vertigo), de Alfred Hitchcock, com James Stewart.', 1958, 'cinema'),
  ('João Gilberto gravou ''Chega de Saudade'', marco inicial da Bossa Nova.', 1958, 'musica'),
  ('Foi implantado pela primeira vez um marca-passo cardíaco em um ser humano.', 1958, 'ciencias'),
  ('No governo de Juscelino Kubitschek, o Brasil construía Brasília e comemorou seu primeiro título mundial de futebol, com Pelé.', 1958, 'historia');

-- 1969
DELETE FROM dica WHERE anoDica = 1969;
INSERT INTO dica (textoDica, anoDica, categoria) VALUES
  ('Pelé marcou seu milésimo gol na carreira, de pênalti, no Maracanã.', 1969, 'esportes'),
  ('Estreou ''Easy Rider'' (Sem Destino), símbolo da contracultura no cinema.', 1969, 'cinema'),
  ('O Festival de Woodstock reuniu cerca de 400 mil pessoas numa fazenda no estado de Nova York.', 1969, 'musica'),
  ('Foi estabelecida a primeira conexão da ARPANET, precursora da internet.', 1969, 'ciencias'),
  ('Neil Armstrong foi o primeiro homem a pisar na Lua, na missão Apollo 11.', 1969, 'historia');

-- 1986
DELETE FROM dica WHERE anoDica = 1986;
INSERT INTO dica (textoDica, anoDica, categoria) VALUES
  ('Maradona marcou o gol da ''mão de Deus'' contra a Inglaterra, na Copa do Mundo do México.', 1986, 'esportes'),
  ('Estreou ''Top Gun - Ases Indomáveis'', com Tom Cruise como piloto da Marinha.', 1986, 'cinema'),
  ('A Legião Urbana lançou o álbum ''Dois'', com ''Eduardo e Mônica'' e ''Tempo Perdido''.', 1986, 'musica'),
  ('O cometa Halley passou perto da Terra, em sua visita periódica a cada cerca de 76 anos.', 1986, 'ciencias'),
  ('Ocorreu o acidente nuclear de Chernobyl, na então União Soviética.', 1986, 'historia');

-- 1992
DELETE FROM dica WHERE anoDica = 1992;
INSERT INTO dica (textoDica, anoDica, categoria) VALUES
  ('Os Jogos Olímpicos de Barcelona tiveram o ''Dream Team'' americano de basquete, com astros da NBA.', 1992, 'esportes'),
  ('A Disney lançou ''Aladdin'', animação com o Gênio dublado por Robin Williams na versão original.', 1992, 'cinema'),
  ('Whitney Houston gravou ''I Will Always Love You'' para a trilha de ''O Guarda-Costas''.', 1992, 'musica'),
  ('Foi confirmada a descoberta dos primeiros planetas fora do Sistema Solar, ao redor de um pulsar.', 1992, 'ciencias'),
  ('O Rio de Janeiro sediou a Conferência das Nações Unidas sobre Meio Ambiente e Desenvolvimento, e o presidente Fernando Collor sofreu impeachment.', 1992, 'historia');

-- 1994
DELETE FROM dica WHERE anoDica = 1994;
INSERT INTO dica (textoDica, anoDica, categoria) VALUES
  ('Pela primeira vez uma final de Copa do Mundo foi decidida nos pênaltis, entre Brasil e Itália.', 1994, 'esportes'),
  ('Estreou ''Pulp Fiction'', de Quentin Tarantino, vencedor da Palma de Ouro em Cannes.', 1994, 'cinema'),
  ('Kurt Cobain, vocalista do Nirvana, morreu aos 27 anos.', 1994, 'musica'),
  ('Fragmentos do cometa Shoemaker-Levy 9 colidiram com Júpiter.', 1994, 'ciencias'),
  ('Foi lançado o Plano Real, que criou a moeda atual do Brasil e controlou a inflação.', 1994, 'historia');

-- 2002
DELETE FROM dica WHERE anoDica = 2002;
INSERT INTO dica (textoDica, anoDica, categoria) VALUES
  ('Um Mundial de futebol foi sediado por dois países asiáticos pela primeira vez, e o Brasil bateu a Alemanha na final.', 2002, 'esportes'),
  ('Estreou ''Cidade de Deus'', de Fernando Meirelles, filme sobre a violência numa favela do Rio.', 2002, 'cinema'),
  ('Avril Lavigne lançou o álbum de estreia ''Let Go'', com o sucesso ''Complicated''.', 2002, 'musica'),
  ('Astrônomos anunciaram a descoberta de Quaoar, um grande objeto gelado além de Netuno.', 2002, 'ciencias'),
  ('Luiz Inácio Lula da Silva foi eleito presidente do Brasil.', 2002, 'historia');

-- 2014
DELETE FROM dica WHERE anoDica = 2014;
INSERT INTO dica (textoDica, anoDica, categoria) VALUES
  ('Uma seleção europeia se tornou a primeira a vencer um Mundial de futebol disputado nas Américas.', 2014, 'esportes'),
  ('Estreou ''Interestelar'', de Christopher Nolan, sobre uma viagem através de um buraco de minhoca.', 2014, 'cinema'),
  ('Taylor Swift lançou o álbum pop ''1989'', com ''Shake It Off''.', 2014, 'musica'),
  ('A sonda Philae, da missão Rosetta, pousou pela primeira vez na superfície de um cometa.', 2014, 'ciencias'),
  ('O Brasil sediou a Copa do Mundo, marcada pela goleada de 7 a 1 da Alemanha sobre a seleção brasileira na semifinal.', 2014, 'historia');

-- 2020
DELETE FROM dica WHERE anoDica = 2020;
INSERT INTO dica (textoDica, anoDica, categoria) VALUES
  ('Os Jogos Olímpicos de Tóquio foram adiados pela primeira vez em tempos de paz.', 2020, 'esportes'),
  ('O sul-coreano ''Parasita'' foi o primeiro filme de língua não inglesa a vencer o Oscar de melhor filme.', 2020, 'cinema'),
  ('The Weeknd lançou o álbum ''After Hours'', com o sucesso ''Blinding Lights''.', 2020, 'musica'),
  ('A SpaceX levou astronautas à Estação Espacial Internacional na primeira missão tripulada de uma empresa privada.', 2020, 'ciencias'),
  ('A Organização Mundial da Saúde declarou a pandemia de covid-19.', 2020, 'historia');

-- um jogo por dia: cada ano tem uma dataJogada, de 2026-11-25 voltando um dia por ano
-- (pode rodar de novo sem problema, o UPDATE sempre grava o mesmo valor)
UPDATE jogo SET dataJogada = '2026-11-25' WHERE ano = 2002;
UPDATE jogo SET dataJogada = '2026-11-24' WHERE ano = 1986;
UPDATE jogo SET dataJogada = '2026-11-23' WHERE ano = 1992;
UPDATE jogo SET dataJogada = '2026-11-22' WHERE ano = 1958;
UPDATE jogo SET dataJogada = '2026-11-21' WHERE ano = 2020;
UPDATE jogo SET dataJogada = '2026-11-20' WHERE ano = 1969;
UPDATE jogo SET dataJogada = '2026-11-19' WHERE ano = 1994;
UPDATE jogo SET dataJogada = '2026-11-18' WHERE ano = 1930;
UPDATE jogo SET dataJogada = '2026-11-17' WHERE ano = 1950;
UPDATE jogo SET dataJogada = '2026-11-16' WHERE ano = 2014;

-- usuarios: alunos + administrador (senha adm)
INSERT IGNORE INTO users (pontosTotais, nomeReal, nickname, email, cidade, anoFav, categoriaFav, senha, ehAdm)
  VALUES (0, 'Bernardo Tibaldi', 'bernardo', 'bernardo@gmail.com', 'Uberlândia', 1969, 'ciencias', '1234', 0);
INSERT IGNORE INTO users (pontosTotais, nomeReal, nickname, email, cidade, anoFav, categoriaFav, senha, ehAdm)
  VALUES (0, 'Caio Araujo', 'caio', 'caio@gmail.com', 'Belo Horizonte', 1994, 'esportes', '1234', 0);
INSERT IGNORE INTO users (pontosTotais, nomeReal, nickname, email, cidade, anoFav, categoriaFav, senha, ehAdm)
  VALUES (0, 'Gustavo Leal', 'gustavo', 'gustavo@gmail.com', 'Campinas', 1985, 'cinema', '1234', 0);
INSERT IGNORE INTO users (pontosTotais, nomeReal, nickname, email, cidade, anoFav, categoriaFav, senha, ehAdm)
  VALUES (0, 'Osvaldo Pagioli', 'osvaldo', 'osvaldo@gmail.com', 'Goiânia', 1958, 'musica', '1234', 0);
INSERT IGNORE INTO users (pontosTotais, nomeReal, nickname, email, cidade, anoFav, categoriaFav, senha, ehAdm)
  VALUES (0, 'Tiago Urquiza', 'tiago', 'tiago@gmail.com', 'Curitiba', 2002, 'literatura', '1234', 0);
INSERT IGNORE INTO users (pontosTotais, nomeReal, nickname, email, cidade, anoFav, categoriaFav, senha, ehAdm)
  VALUES (0, 'Vinicius Carvalho', 'vinicius', 'vinicius@gmail.com', 'Recife', 1977, 'historia', '1234', 0);
INSERT IGNORE INTO users (pontosTotais, nomeReal, nickname, email, cidade, anoFav, categoriaFav, senha, ehAdm)
  VALUES (0, 'Administrador', 'adm', 'adm@gmail.com', 'São Paulo', 1999, 'cinema', 'adm', 1);

-- bancos já existentes: o INSERT IGNORE acima não altera linhas que já existem
UPDATE users SET senha = 'adm', ehAdm = 1 WHERE nickname = 'adm';
UPDATE users SET senha = '1234', ehAdm = 0 WHERE nickname IN ('bernardo','caio','gustavo','osvaldo','tiago','vinicius');
UPDATE users SET nomeReal = 'Bernardo Tibaldi' WHERE nickname = 'bernardo';
UPDATE users SET nomeReal = 'Caio Araujo' WHERE nickname = 'caio';
UPDATE users SET nomeReal = 'Gustavo Leal' WHERE nickname = 'gustavo';
UPDATE users SET nomeReal = 'Osvaldo Pagioli' WHERE nickname = 'osvaldo';
UPDATE users SET nomeReal = 'Tiago Urquiza' WHERE nickname = 'tiago';
UPDATE users SET nomeReal = 'Vinicius Carvalho' WHERE nickname = 'vinicius';

-- o administrador não tem foto de perfil
DELETE FROM upload WHERE contexto = 'perfil' AND userID = (SELECT userID FROM users WHERE nickname = 'adm');

-- fotos de perfil (arquivos em web/arquivos/perfil), inseridas só se o usuário ainda não tiver uma
INSERT INTO upload (userID, contexto, nomeOriginal, nomeSalvo, tipoConteudo, tamanhoBytes)
  SELECT u.userID, 'perfil', 'bernardo.png', 'perfil_bernardo.png', 'image/png', 403144 FROM users u
  WHERE u.nickname = 'bernardo' AND NOT EXISTS (SELECT 1 FROM upload p WHERE p.userID = u.userID AND p.contexto = 'perfil');
INSERT INTO upload (userID, contexto, nomeOriginal, nomeSalvo, tipoConteudo, tamanhoBytes)
  SELECT u.userID, 'perfil', 'caio.jpeg', 'perfil_caio.jpeg', 'image/jpeg', 87933 FROM users u
  WHERE u.nickname = 'caio' AND NOT EXISTS (SELECT 1 FROM upload p WHERE p.userID = u.userID AND p.contexto = 'perfil');
INSERT INTO upload (userID, contexto, nomeOriginal, nomeSalvo, tipoConteudo, tamanhoBytes)
  SELECT u.userID, 'perfil', 'gustavo.jpeg', 'perfil_gustavo.jpeg', 'image/jpeg', 35888 FROM users u
  WHERE u.nickname = 'gustavo' AND NOT EXISTS (SELECT 1 FROM upload p WHERE p.userID = u.userID AND p.contexto = 'perfil');
INSERT INTO upload (userID, contexto, nomeOriginal, nomeSalvo, tipoConteudo, tamanhoBytes)
  SELECT u.userID, 'perfil', 'osvaldo.png', 'perfil_osvaldo.png', 'image/png', 396933 FROM users u
  WHERE u.nickname = 'osvaldo' AND NOT EXISTS (SELECT 1 FROM upload p WHERE p.userID = u.userID AND p.contexto = 'perfil');
INSERT INTO upload (userID, contexto, nomeOriginal, nomeSalvo, tipoConteudo, tamanhoBytes)
  SELECT u.userID, 'perfil', 'tiago.jpeg', 'perfil_tiago.jpeg', 'image/jpeg', 253383 FROM users u
  WHERE u.nickname = 'tiago' AND NOT EXISTS (SELECT 1 FROM upload p WHERE p.userID = u.userID AND p.contexto = 'perfil');
INSERT INTO upload (userID, contexto, nomeOriginal, nomeSalvo, tipoConteudo, tamanhoBytes)
  SELECT u.userID, 'perfil', 'vinicius.jpg', 'perfil_vinicius.jpg', 'image/jpeg', 255567 FROM users u
  WHERE u.nickname = 'vinicius' AND NOT EXISTS (SELECT 1 FROM upload p WHERE p.userID = u.userID AND p.contexto = 'perfil');

-- fotos marcantes de cada ano (Dica Zero), arquivos em web/arquivos/jogo; inseridas só se o ano ainda não tiver uma
-- 1930: https://commons.wikimedia.org/wiki/File:Uruguay_goal_v_argentina_1930.jpg (Public domain)
INSERT INTO upload (ano, contexto, nomeOriginal, nomeSalvo, tipoConteudo, tamanhoBytes)
  SELECT j.ano, 'jogo', 'jogo_1930.jpg', 'jogo_1930.jpg', 'image/jpeg', 62735 FROM jogo j
  WHERE j.ano = 1930 AND NOT EXISTS (SELECT 1 FROM upload p WHERE p.ano = j.ano AND p.contexto = 'jogo');
-- 1950: https://commons.wikimedia.org/wiki/File:Gol_ghiggia_vs_brasil.jpg (Public domain)
INSERT INTO upload (ano, contexto, nomeOriginal, nomeSalvo, tipoConteudo, tamanhoBytes)
  SELECT j.ano, 'jogo', 'jogo_1950.jpg', 'jogo_1950.jpg', 'image/jpeg', 207502 FROM jogo j
  WHERE j.ano = 1950 AND NOT EXISTS (SELECT 1 FROM upload p WHERE p.ano = j.ano AND p.contexto = 'jogo');
-- 1958: https://commons.wikimedia.org/wiki/File:Pel%C3%A9_goal_1958_WC_final.jpg (Public domain)
INSERT INTO upload (ano, contexto, nomeOriginal, nomeSalvo, tipoConteudo, tamanhoBytes)
  SELECT j.ano, 'jogo', 'jogo_1958.jpg', 'jogo_1958.jpg', 'image/jpeg', 122407 FROM jogo j
  WHERE j.ano = 1958 AND NOT EXISTS (SELECT 1 FROM upload p WHERE p.ano = j.ano AND p.contexto = 'jogo');
-- 1969: https://commons.wikimedia.org/wiki/File:Aldrin_Apollo_11.jpg (Public domain (NASA))
INSERT INTO upload (ano, contexto, nomeOriginal, nomeSalvo, tipoConteudo, tamanhoBytes)
  SELECT j.ano, 'jogo', 'jogo_1969.jpg', 'jogo_1969.jpg', 'image/jpeg', 398906 FROM jogo j
  WHERE j.ano = 1969 AND NOT EXISTS (SELECT 1 FROM upload p WHERE p.ano = j.ano AND p.contexto = 'jogo');
-- 1986: https://commons.wikimedia.org/wiki/File:Maradona_shilton_mano_dios.jpg (Public domain)
INSERT INTO upload (ano, contexto, nomeOriginal, nomeSalvo, tipoConteudo, tamanhoBytes)
  SELECT j.ano, 'jogo', 'jogo_1986.jpg', 'jogo_1986.jpg', 'image/jpeg', 423896 FROM jogo j
  WHERE j.ano = 1986 AND NOT EXISTS (SELECT 1 FROM upload p WHERE p.ano = j.ano AND p.contexto = 'jogo');
-- 1992: https://commons.wikimedia.org/wiki/File:Dream_Team_at_the_1992_Summer_Olympics.JPEG (Public domain)
INSERT INTO upload (ano, contexto, nomeOriginal, nomeSalvo, tipoConteudo, tamanhoBytes)
  SELECT j.ano, 'jogo', 'jogo_1992.jpg', 'jogo_1992.jpg', 'image/jpeg', 475412 FROM jogo j
  WHERE j.ano = 1992 AND NOT EXISTS (SELECT 1 FROM upload p WHERE p.ano = j.ano AND p.contexto = 'jogo');
-- 1994: https://commons.wikimedia.org/wiki/File:Salva_romrio.jpg (CC BY-SA 4.0)
INSERT INTO upload (ano, contexto, nomeOriginal, nomeSalvo, tipoConteudo, tamanhoBytes)
  SELECT j.ano, 'jogo', 'jogo_1994.jpg', 'jogo_1994.jpg', 'image/jpeg', 606992 FROM jogo j
  WHERE j.ano = 1994 AND NOT EXISTS (SELECT 1 FROM upload p WHERE p.ano = j.ano AND p.contexto = 'jogo');
-- 2002: https://commons.wikimedia.org/wiki/File:Seoul_Plaza_2002_FIFA_World_Cup.jpg (CC0)
INSERT INTO upload (ano, contexto, nomeOriginal, nomeSalvo, tipoConteudo, tamanhoBytes)
  SELECT j.ano, 'jogo', 'jogo_2002.jpg', 'jogo_2002.jpg', 'image/jpeg', 378259 FROM jogo j
  WHERE j.ano = 2002 AND NOT EXISTS (SELECT 1 FROM upload p WHERE p.ano = j.ano AND p.contexto = 'jogo');
-- 2014: https://commons.wikimedia.org/wiki/File:Germany_and_Argentina_face_off_in_the_final_of_the_World_Cup_2014_-2014-07-13_(5).jpg (CC BY 3.0)
INSERT INTO upload (ano, contexto, nomeOriginal, nomeSalvo, tipoConteudo, tamanhoBytes)
  SELECT j.ano, 'jogo', 'jogo_2014.jpg', 'jogo_2014.jpg', 'image/jpeg', 148507 FROM jogo j
  WHERE j.ano = 2014 AND NOT EXISTS (SELECT 1 FROM upload p WHERE p.ano = j.ano AND p.contexto = 'jogo');
-- 2020: https://commons.wikimedia.org/wiki/File:Face_masks_during_the_COVID-19_pandemic.jpg (CC BY-SA 2.0)
INSERT INTO upload (ano, contexto, nomeOriginal, nomeSalvo, tipoConteudo, tamanhoBytes)
  SELECT j.ano, 'jogo', 'jogo_2020.jpg', 'jogo_2020.jpg', 'image/jpeg', 101160 FROM jogo j
  WHERE j.ano = 2020 AND NOT EXISTS (SELECT 1 FROM upload p WHERE p.ano = j.ano AND p.contexto = 'jogo');

-- progresso de uma partida em andamento (apagado quando o jogo termina)
CREATE TABLE IF NOT EXISTS progresso (
  userID INT,
  ano INT,
  tentativas INT NOT NULL,
  dicasReveladas INT NOT NULL,
  chutes VARCHAR(100) NOT NULL DEFAULT '',
  PRIMARY KEY (userID, ano),
  FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (ano) REFERENCES jogo(ano) ON DELETE CASCADE ON UPDATE CASCADE
);
