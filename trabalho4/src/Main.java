import java.sql.SQLException;
import java.util.Calendar;

public class Main {
    static void main() {
        JogoDao jogoDao = new JogoDao();
        DicaDao dicaDao = new DicaDao();
        UsuarioDao usuarioDao = new UsuarioDao();
        UserGameDao userGameDao = new UserGameDao();
        System.out.println("Conectado com sucesso!");

        try {
            // insere os dados
            Jogo jogo = new Jogo();
            jogo.setAno(1958);
            jogo.setCategoria("musica");

            Dica dica = new Dica();
            dica.setTextoDica("Michael Jackson nasceu nesse ano");
            dica.setAnoDica(1958);
            dica.setDicaID(1);

            Usuario usuario = new Usuario();
            usuario.setPontosTotais(0);
            usuario.setNomeReal("Joao da Silva");
            usuario.setNickname("joaozinho");
            usuario.setEmail("joao@gmail.com");
            usuario.setCidade("Belo Horizonte");
            usuario.setAnoFav(1923);
            usuario.setCategoriaFav("historia");
            usuario.setUserID(1);

            UserGame userGame = new UserGame();
            userGame.setUserID(1);
            userGame.setAno(1958);
            userGame.setPontuacao(0);

            jogoDao.adiciona(jogo);
            dicaDao.adiciona(dica);
            usuarioDao.adiciona(usuario);
            userGameDao.adiciona(userGame);

            imprimeDados(jogoDao, dicaDao, usuarioDao, userGameDao);

            // altera os dados
            jogo.setDataJogada(Calendar.getInstance());
            dica.setTextoDica("Brasil ganhou a copa pela primeira vez");
            usuario.setCategoriaFav("musica");
            usuario.setNickname("joaozao");

            jogoDao.altera(jogo);
            dicaDao.altera(dica);
            usuarioDao.altera(usuario);
            userGameDao.altera();

            System.out.println("\n======================================================Dados Alterados!======================================================\n");

            imprimeDados(jogoDao, dicaDao, usuarioDao, userGameDao);

            // remove os dados
            userGameDao.remove();
            usuarioDao.remove();
            dicaDao.remove();
            jogoDao.remove();

            // fecha as conexoes
            userGameDao.fechaConexao();
            usuarioDao.fechaConexao();
            dicaDao.fechaConexao();
            jogoDao.fechaConexao();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    static void imprimeDados(JogoDao jogoDao, DicaDao dicaDao, UsuarioDao usuarioDao, UserGameDao userGameDao) {
        for (Jogo jogo : jogoDao.getLista()) {
            System.out.println("Ano do jogo : " + jogo.getAno() + "\nCategoria: "
                    + jogo.getCategoria());
        }

        System.out.println();
        for (Dica dica : dicaDao.getLista()) {
            System.out.println("Dica: " + dica.getTextoDica());
        }

        System.out.println();
        for (Usuario usuario : usuarioDao.getLista()) {
            System.out.println("Nome do usuario: " + usuario.getNomeReal() + "\nApelido: "
                    + usuario.getNickname() + "\nemail: " + usuario.getEmail() +
                    "\nAno favorito: " + usuario.getAnoFav() + "\nCategoria favorita: " +
                    usuario.getCategoriaFav());
        }

        System.out.println();
        userGameDao.imprimeComJuncao();
    }
}
