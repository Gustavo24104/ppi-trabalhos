package game;

import yeardle.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.random.RandomGenerator;


@WebServlet("/gameDica")
public class GameDicaServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userID") != null && session.getAttribute("jogoAtual") != null) {
            Jogo jogoAtual = (Jogo) session.getAttribute("jogoAtual");
            String dataSessao = (String) session.getAttribute("dataSelecionada");

            // confere se o pedido e do jogo atual da sessao (outra aba pode ter trocado o dia)
            String dataForm = req.getParameter("data");
            if (dataSessao == null || dataForm == null || !dataForm.trim().equals(dataSessao)) {
                resp.sendRedirect("jogar");
                return;
            }

            int userID = (Integer) session.getAttribute("userID");
            int ano = jogoAtual.getAno();

            List<Dica> dicas;
            DicaDao dd = new DicaDao();
            try {
                dicas = dd.buscaPorJogo(ano);
            } finally {
                try { dd.fechaConexao(); } catch (SQLException ignored) {}
            }

            ProgressoDao pd = new ProgressoDao();
            try {
                Progresso prog = pd.busca(userID, ano);
                if (prog == null) {
                    prog = new Progresso();
                }

                // so gasta tentativa se ainda ha dica e sobra tentativa para chutar depois
                if (prog.getDicasReveladas() < dicas.size() && prog.getTentativas() + 1 < 5) {
                    prog.setDicasReveladas(prog.getDicasReveladas() + 1);
                    prog.setTentativas(prog.getTentativas() + 1);
                    pd.salva(userID, ano, prog);
                }
                GameServelet.poeNaSessao(session, prog);
            } finally {
                try { pd.fechaConexao(); } catch (SQLException ignored) {}
            }
        }
        resp.sendRedirect("jogar");
    }


//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        HttpSession session = req.getSession(false);
//        if (session == null || session.getAttribute("jogoAtual") == null) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
//
//        Jogo jogoAtual = (Jogo) session.getAttribute("jogoAtual");
//        List<String> dicas = jogoAtual.getDicas(); // lista de dicas do jogo atual
//
//        Integer reveladas = (Integer) session.getAttribute("dicasReveladas");
//        int atual = (reveladas != null) ? reveladas : 0;
//
//        // Se não há mais dicas para revelar
//        if (atual >= dicas.size()) {
//            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
//            return;
//        }
//
//        String proximaDica = dicas.get(atual);
//        int novoTotal = atual + 1;
//        session.setAttribute("dicasReveladas", novoTotal);
//
//        // Avisa o front-end via cabeçalho se ainda existem dicas restantes
//        resp.setHeader("X-Tem-Mais", String.valueOf(novoTotal < dicas.size()));
//        resp.setContentType("text/plain; charset=UTF-8");
//        resp.getWriter().write(proximaDica);

//    }
}
