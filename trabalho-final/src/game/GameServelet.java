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
//    private int tentativas = 0;
    private final int MAX_TENTATIVAS = 5;

//    @Override
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
//        tentativas = 1;
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
        if (guess == jogoAtual.getAno()) {
            session.setAttribute("pontuacao", 60000 - 10000 * (Integer) session.getAttribute("dicasReveladas"));

            session.removeAttribute("tentativas");
            session.removeAttribute("jogoAtual");
            session.removeAttribute("dicasReveladas");

            resp.sendRedirect("ganhou.jsp");
            return;
        }

        if (tentativas >= MAX_TENTATIVAS) {
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
        HttpSession session = req.getSession();

        // Inicializa a partida na sessão se ainda não existir
        if (session.getAttribute("jogoAtual") == null) {
            session.setAttribute("jogoAtual", jogoAtual);
            session.setAttribute("dicasReveladas", 1);
            session.setAttribute("tentativas", 0);
        }

        req.getRequestDispatcher("game.jsp").forward(req, resp);
    }

}
