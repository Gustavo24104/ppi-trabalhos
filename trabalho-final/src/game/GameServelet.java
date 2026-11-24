package game;


import yeardle.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.random.RandomGenerator;

@WebServlet("/jogar")
public class GameServelet extends HttpServlet {
    UserGameDao ugd = new UserGameDao();
    UsuarioDao ud = new UsuarioDao();
    JogoDao jd = new JogoDao();
    DicaDao dd = new DicaDao();
    Jogo jogoAtual;
    List<Dica> dicasAtuas;

    @Override
    public void init() {
        jogoAtual = jd.buscaPorDataJogada(Calendar.getInstance());
        if(jogoAtual == null) {
            List<Jogo> candidatos = jd.getNaoJogados();
            Random rng = new Random();
            // se nao tiver nenhum pra hoje, ja escolhe um jogo aleatório pra jogar
            jogoAtual = candidatos.get(rng.nextInt(candidatos.size()));
            jogoAtual.setDataJogada(Calendar.getInstance());
            jd.altera(jogoAtual);
        }
        dicasAtuas = dd.buscaPorJogo(jogoAtual.getAno());
    }

    @Override
    public void destroy() {
        try {
            ugd.fechaConexao();
        } catch (Exception e) {
            log("Erro ao fechar a conexao de UserGameDao", e);
        }
        try {
            ud.fechaConexao();
        } catch (Exception e) {
            log("Erro ao fechar a conexao de UsuarioDao", e);
        }
        try {
            jd.fechaConexao();
        } catch (Exception e) {
            log("Erro ao fechar a conexao de JogoDao", e);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        super.service(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int guess = Integer.parseInt(req.getParameter("guess"));
        if(guess == jogoAtual.getAno()) {
            req.getRequestDispatcher("indexGame.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("game.jsp").forward(req, resp);
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("game.jsp").forward(req, resp);
    }

}
