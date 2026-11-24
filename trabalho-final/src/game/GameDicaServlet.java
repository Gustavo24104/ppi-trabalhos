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
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("jogoAtual") != null) {
            Jogo jogoAtual = (Jogo) session.getAttribute("jogoAtual");
            Integer rev = (Integer) session.getAttribute("dicasReveladas");
            Integer tentativas = (Integer) session.getAttribute("tentativas");
            tentativas++;
            session.setAttribute("tentativas", tentativas);

            DicaDao dd = new DicaDao();
            List<Dica> dicas = dd.buscaPorJogo(jogoAtual.getAno());

            try {
                dd.fechaConexao();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            int atual = (rev != null) ? rev : 0;

            if (atual < dicas.size()) {
                session.setAttribute("dicasReveladas", atual + 1);
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
