package game;


import yeardle.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
    private final int MAX_TENTATIVAS = 5;

//    @Override
    public void init() {
        getJogoAtual();
//        tentativas = 1;
    }

    public void getJogoAtual() {
        Calendar hoje = Calendar.getInstance();
        boolean mesmoDia = jogoAtual != null && hoje.get(Calendar.DAY_OF_YEAR) == jogoAtual.getDataJogada().get(Calendar.DAY_OF_YEAR) &&
                hoje.get(Calendar.YEAR) == jogoAtual.getDataJogada().get(Calendar.YEAR);

        if (mesmoDia && dicasAtuas != null) {
            return;
        }

        jogoAtual = jd.buscaPorDataJogada(Calendar.getInstance());
        if(jogoAtual == null || !mesmoDia) {
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

    private void salvaPerformance(HttpSession session) {
        int points = 5000 - 1000 * (Integer) session.getAttribute("dicasReveladas");
        session.setAttribute("pontuacao", points);

        int userID = (Integer) session.getAttribute("userID");
        UserGame ug = new UserGame();
        ug.setAno(jogoAtual.getAno());
        ug.setPontuacao(points);
        ug.setUserID(userID);

        ugd.adiciona(ug);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        Jogo jogoAtual = (Jogo) session.getAttribute("jogoAtual");
        Integer tentativas = (Integer) session.getAttribute("tentativas");

        if (jogoAtual == null || tentativas == null) {
            resp.sendRedirect("jogar");
            return;
        }

        int guess = Integer.parseInt(req.getParameter("guess"));
        tentativas++;
        session.setAttribute("tentativas", tentativas);


        // TODO: melhorar logica (adicionar usuario à sessao eu acho e evitar o cara de jogar dnv)
        if (guess == jogoAtual.getAno()) { // ganhou

            // salva no BD
            salvaPerformance(session);

            session.removeAttribute("tentativas");
            session.removeAttribute("jogoAtual");
            session.removeAttribute("dicasReveladas");

            resp.sendRedirect("ganhou.jsp");
            return;
        }

        if (tentativas >= MAX_TENTATIVAS) {
            salvaPerformance(session);

            session.setAttribute("anoCorreto", jogoAtual.getAno());
            session.removeAttribute("tentativas");
            session.removeAttribute("jogoAtual");
            session.removeAttribute("dicasReveladas");

            resp.sendRedirect("perdeu.jsp");
            return;
        }

        int atual = ((Integer) session.getAttribute("dicasReveladas") != null) ?
                (Integer) session.getAttribute("dicasReveladas") : 0;

        session.setAttribute("dicasReveladas", atual + 1);
        resp.sendRedirect("jogar");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getJogoAtual();
        HttpSession session = req.getSession();
        // Inicializa a partida na sessão se ainda não existir
        if (session.getAttribute("jogoAtual") == null) {
            session.setAttribute("jogoAtual", jogoAtual);
            session.setAttribute("dicasReveladas", 1);
            session.setAttribute("tentativas", 0);
        }

        // se ja jogou (ent ta no leaderboard)
        UserGame encontrado = ugd.busca(  (Integer) session.getAttribute("userID"), jogoAtual.getAno());
        if(encontrado != null) {
            // entao nao joga denovo
            session.setAttribute("pontuacao", encontrado.getPontuacao());
            session.removeAttribute("tentativas");
            session.removeAttribute("jogoAtual");
            session.removeAttribute("dicasReveladas");

            if(encontrado.getPontuacao() != 0) {
                resp.sendRedirect("ganhou.jsp");
            } else {
                resp.sendRedirect("perdeu.jsp");
            }
            return;
        }

        req.getRequestDispatcher("game.jsp").forward(req, resp);
    }

}
