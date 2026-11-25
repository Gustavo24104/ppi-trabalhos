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
import java.time.*;


@WebServlet("/jogar")
public class GameServelet extends HttpServlet {
    private final int MAX_TENTATIVAS = 5;

    // copia o progresso do banco para a sessao (so como cache para o game.jsp)
    static void poeNaSessao(HttpSession session, Progresso p) {
        session.setAttribute("tentativas", p.getTentativas());
        session.setAttribute("dicasReveladas", p.getDicasReveladas());
        session.setAttribute("chutes", p.getChutes());
    }

    static void limpaJogoDaSessao(HttpSession session) {
        session.removeAttribute("tentativas");
        session.removeAttribute("jogoAtual");
        session.removeAttribute("dicasReveladas");
        session.removeAttribute("chutes");
    }

    // converte LocalDate <-> Calendar
    private Calendar paraCalendar(LocalDate d) {
        return GregorianCalendar.from(d.atStartOfDay(ZoneId.systemDefault()));
    }

    private LocalDate paraLocalDate(Calendar c) {
        return Instant.ofEpochMilli(c.getTimeInMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // busca o jogo da data; so para hoje, se nao existir, sorteia um jogo ainda sem data
    // (synchronized para dois usuarios nao sortearem jogos diferentes ao mesmo tempo)
    private synchronized Jogo buscaJogo(JogoDao jd, LocalDate data, LocalDate hoje) {
        Jogo jogo = jd.buscaPorDataJogada(paraCalendar(data));
        if (jogo == null && data.equals(hoje)) {
            List<Jogo> candidatos = jd.getNaoJogados();
            if (candidatos.isEmpty()) {
                return null;
            }
            jogo = candidatos.get(new Random().nextInt(candidatos.size()));
            jogo.setDataJogada(paraCalendar(hoje));
            jd.altera(jogo);
        }
        return jogo;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        super.service(req, resp);
    }

    private void salvaPerformance(HttpSession session, Jogo jogoAtual, boolean ganhou, int rev) {
        // derrota vale 0; vitoria sempre vale pelo menos 500 (assim pontuacao != 0 indica vitoria)
        int points = 0;
        if (ganhou) {
            points = Math.max(4000 - 1000 * rev, 500);
        }
        session.setAttribute("pontuacao", points);

        int userID = (Integer) session.getAttribute("userID");

        UserGameDao ugd = new UserGameDao();
        UsuarioDao ud = new UsuarioDao();
        try {
            // evita salvar/somar duas vezes a mesma partida
            if (ugd.busca(userID, jogoAtual.getAno()) != null) {
                return;
            }

            UserGame ug = new UserGame();
            ug.setAno(jogoAtual.getAno());
            ug.setPontuacao(points);
            ug.setUserID(userID);

            try {
                ugd.adiciona(ug);
            } catch (RuntimeException e) {
                // chave duplicada (envio duplo): ja foi salvo, nao soma de novo
                if (ugd.busca(userID, jogoAtual.getAno()) != null) {
                    return;
                }
                throw e;
            }

            // soma na pontuacao geral do usuario
            ud.somaPontos(userID, points);
        } finally {
            try { ugd.fechaConexao(); } catch (Exception ignored) {}
            try { ud.fechaConexao(); } catch (Exception ignored) {}
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        // login obrigatorio
        if (session.getAttribute("userID") == null) {
            resp.sendRedirect("indexGame.jsp");
            return;
        }

        Jogo jogoAtual = (Jogo) session.getAttribute("jogoAtual");
        String dataSessao = (String) session.getAttribute("dataSelecionada");

        if (jogoAtual == null || dataSessao == null) {
            resp.sendRedirect("jogar");
            return;
        }

        // confere se o chute e do jogo atual da sessao (outra aba pode ter trocado o dia)
        String dataForm = req.getParameter("data");
        if (dataForm == null || !dataForm.trim().equals(dataSessao)) {
            resp.sendRedirect("jogar");
            return;
        }

        int userID = (Integer) session.getAttribute("userID");
        int ano = jogoAtual.getAno();

        // valida o chute (sem gastar tentativa se for invalido)
        int guess = 0;
        boolean valido = true;
        try {
            guess = Integer.parseInt(req.getParameter("guess").trim());
        } catch (Exception e) {
            valido = false;
        }
        if (valido && (guess < 1901 || guess > LocalDate.now().getYear())) {
            valido = false;
        }
        if (!valido) {
            session.setAttribute("mensagemChute", "Chute inválido: digite um ano inteiro entre 1901 e " + LocalDate.now().getYear() + ".");
            resp.sendRedirect("jogar");
            return;
        }

        ProgressoDao pd = new ProgressoDao();
        try {
            Progresso prog = pd.busca(userID, ano);
            if (prog == null) {
                prog = new Progresso();
            }

            prog.setTentativas(prog.getTentativas() + 1);
            prog.setChutes(prog.getChutes().isEmpty() ? String.valueOf(guess) : prog.getChutes() + "," + guess);

            // limpa resultado antigo
            session.removeAttribute("pontuacao");
            session.removeAttribute("anoCorreto");

            if (guess == ano) { // ganhou
                salvaPerformance(session, jogoAtual, true, prog.getDicasReveladas());
                session.setAttribute("anoCorreto", ano);
                limpaJogoDaSessao(session);
                pd.remove(userID, ano);
                resp.sendRedirect("ganhou.jsp");
                return;
            }

            if (prog.getTentativas() >= MAX_TENTATIVAS) {
                salvaPerformance(session, jogoAtual, false, prog.getDicasReveladas());
                session.setAttribute("anoCorreto", ano);
                limpaJogoDaSessao(session);
                pd.remove(userID, ano);
                resp.sendRedirect("perdeu.jsp");
                return;
            }

            // errou: revela mais uma dica
            prog.setDicasReveladas(prog.getDicasReveladas() + 1);
            pd.salva(userID, ano, prog);
            poeNaSessao(session, prog);
            session.setAttribute("mensagemChute", "Você chutou " + guess + " — errou!");
            resp.sendRedirect("jogar");
        } finally {
            try { pd.fechaConexao(); } catch (Exception ignored) {}
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        // login obrigatorio
        if (session.getAttribute("userID") == null) {
            resp.sendRedirect("indexGame.jsp");
            return;
        }

        JogoDao jd = new JogoDao();
        UserGameDao ugd = new UserGameDao();
        try {
            tratarGet(req, resp, session, jd, ugd);
        } finally {
            try { jd.fechaConexao(); } catch (Exception ignored) {}
            try { ugd.fechaConexao(); } catch (Exception ignored) {}
        }
    }

    private void tratarGet(HttpServletRequest req, HttpServletResponse resp, HttpSession session,
                           JogoDao jd, UserGameDao ugd) throws ServletException, IOException {
        LocalDate hoje = LocalDate.now();
        String mensagem = null;
        LocalDate data = null;
        Jogo jogo = null;

        // "Jogar outro dia": vai para o dia mais recente ainda nao jogado
        if ("1".equals(req.getParameter("proximo"))) {
            int uid = (Integer) session.getAttribute("userID");
            // garante que o jogo de hoje exista (sorteia) antes de procurar
            buscaJogo(jd, hoje, hoje);
            LocalDate prox = jd.proximaDataNaoJogada(uid, hoje);
            if (prox != null) {
                resp.sendRedirect("jogar?data=" + prox);
            } else {
                session.setAttribute("mensagemTodosJogados", "Você já jogou todos os dias disponíveis!");
                resp.sendRedirect("jogar?data=" + hoje);
            }
            return;
        }

        // data pedida: parametro "data" ou a que ja estava na sessao
        String param = req.getParameter("data");
        String pedida = (param != null && !param.isBlank()) ? param.trim() : (String) session.getAttribute("dataSelecionada");
        if (pedida != null) {
            try {
                data = LocalDate.parse(pedida);
            } catch (Exception e) {
                if (param != null) mensagem = "Data inválida. Mostrando o jogo de hoje.";
            }
        }
        if (data != null && data.isAfter(hoje)) {
            mensagem = "Você não pode jogar o jogo de um dia futuro. Mostrando o jogo de hoje.";
            data = null;
        }
        if (data != null) {
            jogo = buscaJogo(jd, data, hoje);
            if (jogo == null) {
                if (param != null) mensagem = "Não existe jogo na data " + data.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ". Mostrando o jogo de hoje.";
                data = null;
            }
        }
        if (data == null) {
            data = hoje;
            jogo = buscaJogo(jd, hoje, hoje);
        }

        Calendar menor = jd.menorDataJogada();
        req.setAttribute("dataMin", paraLocalDate(menor != null ? menor : paraCalendar(hoje)).toString());
        req.setAttribute("dataMax", hoje.toString());
        req.setAttribute("dataSelecionada", data.toString());
        req.setAttribute("dataBr", data.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        if (mensagem != null) {
            req.setAttribute("mensagemData", mensagem);
        }
        Object todos = session.getAttribute("mensagemTodosJogados");
        if (todos != null) {
            session.removeAttribute("mensagemTodosJogados");
            req.setAttribute("mensagemData", todos);
        }

        if (jogo == null) {
            // nao ha jogo nem para hoje
            session.removeAttribute("jogoAtual");
            req.setAttribute("mensagemData", "Não há jogo disponível para hoje.");
            req.getRequestDispatcher("game.jsp").forward(req, resp);
            return;
        }

        session.setAttribute("dataSelecionada", data.toString());

        // se ja jogou (ent ta no leaderboard)
        int userID = (Integer) session.getAttribute("userID");
        UserGame encontrado = ugd.busca(userID, jogo.getAno());
        if (encontrado != null) {
            // entao nao joga denovo
            session.setAttribute("pontuacao", encontrado.getPontuacao());
            session.setAttribute("anoCorreto", jogo.getAno());
            limpaJogoDaSessao(session);

            // mostra o game.jsp em modo "ja jogou" (com o calendario para escolher outro dia)
            req.setAttribute("jogoJogado", jogo);
            req.setAttribute("pontuacaoJogada", encontrado.getPontuacao());
            req.getRequestDispatcher("game.jsp").forward(req, resp);
            return;
        }

        // jogo jogavel: limpa resultado antigo e carrega o progresso do banco
        session.removeAttribute("pontuacao");
        session.removeAttribute("anoCorreto");
        ProgressoDao pd = new ProgressoDao();
        try {
            Progresso prog = pd.busca(userID, jogo.getAno());
            poeNaSessao(session, prog != null ? prog : new Progresso());
        } finally {
            try { pd.fechaConexao(); } catch (Exception ignored) {}
        }
        session.setAttribute("jogoAtual", jogo);

        req.getRequestDispatcher("game.jsp").forward(req, resp);
    }

}
