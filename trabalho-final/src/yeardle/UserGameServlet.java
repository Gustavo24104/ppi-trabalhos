package yeardle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.*;

@WebServlet("/usergame")
public class UserGameServlet extends HttpServlet {

    private UserGameDao dao;
    private UploadDao arquivoDao;

    @Override
        public void init() {
            dao = new UserGameDao();
            arquivoDao = new UploadDao();
        }

    @Override
    public void destroy() {
        try {
            dao.fechaConexao();
        } catch (Exception e) {
            log("Erro ao fechar a conexao de UserGameDao", e);
        }
        try {
            arquivoDao.fechaConexao();
        } catch (Exception e) {
            log("Erro ao fechar a conexao de UploadDao", e);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        super.service(req, resp);
    }

    // mantem users.pontosTotais consistente com as partidas (delta pode ser negativo)
    private static void somaPontosUsuario(int userID, int delta) {
        if (delta == 0) return;
        UsuarioDao ud = new UsuarioDao();
        try {
            ud.somaPontos(userID, delta);
        } finally {
            try { ud.fechaConexao(); } catch (Exception ignored) {}
        }
    }

    private static void validaPontuacao(int pontuacao) {
        Campos.exige(pontuacao >= 0 && pontuacao <= 4000, "A pontuação deve estar entre 0 e 4000.");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String acao = req.getParameter("acao");
        Campos campos = new Campos(req);
        boolean sucesso = false;
        String mensagem;
        Map<String, String> detalhes = new LinkedHashMap<>();
        List<Map<String, String>> lista = null;

        try {
            if ("listar".equals(acao)) {
                lista = new ArrayList<>();
                for (UserGame userGame : dao.getLista()) {
                    lista.add(detalhesDe(userGame));
                }
                sucesso = true;
                mensagem = lista.isEmpty() ? "Nenhuma partida cadastrada." : lista.size() + " partida(s) cadastrada(s).";
            } else if ("cadastrar".equals(acao)) {
                UserGame userGame = new UserGame();
                userGame.setUserID(campos.inteiro("userID", "ID do usuário"));
                userGame.setAno(campos.inteiro("ano", "Ano"));
                userGame.setPontuacao(campos.inteiro("pontuacao", "Pontuação"));
                Campos.exige(userGame.getAno() > 1900, "O ano deve ser maior que 1900.");
                validaPontuacao(userGame.getPontuacao());
                dao.adiciona(userGame);
                somaPontosUsuario(userGame.getUserID(), userGame.getPontuacao());
                sucesso = true;
                mensagem = "Partida cadastrada com sucesso!";
            } else if ("alterar".equals(acao)) {
                // a chave primaria e composta: (userID, ano); pontuacao é NOT NULL, então não pode ser apagada
                UserGame userGame = dao.busca(campos.inteiro("userID", "ID do usuário"), campos.inteiro("ano", "Ano"));
                if (userGame == null) {
                    mensagem = "Nenhuma partida encontrada para esse usuário e ano.";
                } else if (!campos.preenchido("pontuacao")) {
                    mensagem = "Nenhuma alteração informada: preencha a nova pontuação.";
                } else {
                    int antiga = userGame.getPontuacao();
                    userGame.setPontuacao(campos.inteiro("pontuacao", "Pontuação"));
                    validaPontuacao(userGame.getPontuacao());
                    if (dao.altera(userGame)) {
                        somaPontosUsuario(userGame.getUserID(), userGame.getPontuacao() - antiga);
                        sucesso = true;
                        mensagem = "Partida alterada com sucesso!";
                        detalhes = detalhesDe(userGame);
                    } else {
                        mensagem = "Nenhuma partida encontrada para esse usuário e ano.";
                    }
                }
            } else if ("consultar".equals(acao)) {
                UserGame userGame = dao.busca(campos.inteiro("userID", "ID do usuário"), campos.inteiro("ano", "Ano"));
                if (userGame == null) {
                    mensagem = "Nenhuma partida encontrada para esse usuário e ano.";
                } else {
                    sucesso = true;
                    mensagem = "Partida encontrada com sucesso!";
                    detalhes = detalhesDe(userGame);
                }
            } else if ("remover".equals(acao)) {
                int uid = campos.inteiro("userID", "ID do usuário");
                int anoRem = campos.inteiro("ano", "Ano");
                UserGame existente = dao.busca(uid, anoRem);
                if (existente != null && dao.remove(uid, anoRem)) {
                    somaPontosUsuario(uid, -existente.getPontuacao());
                    sucesso = true;
                    mensagem = "Partida removida com sucesso!";
                } else {
                    mensagem = "Nenhuma partida encontrada para esse usuário e ano.";
                }
            } else {
                mensagem = "Ação inválida.";
            }
        } catch (Campos.CampoInvalidoException e) {
            mensagem = e.getMessage();
        } catch (NumberFormatException e) {
            mensagem = "Preencha os campos numéricos com valores válidos.";
        } catch (RuntimeException e) {
            // erros do banco, ex.: usuário ou jogo inexistente (chave estrangeira) ou chave duplicada
            log("Erro no banco de dados", e);
            mensagem = Campos.mensagemBanco(e, "usuário e ano (partida)", "O usuário ou o ano informado não existe.");
        }

        req.setAttribute("status", sucesso ? "sucesso" : "erro");
        req.setAttribute("entidade", "Partida (UserGame)");
        req.setAttribute("mensagem", mensagem);
        req.setAttribute("detalhes", detalhes);
        req.setAttribute("lista", lista);
        req.setAttribute("paginaVoltar", "usergame.jsp");
        req.getRequestDispatcher("roteador.jsp").forward(req, resp);
    }

    // Usado por usuários para ver o ranking
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Map<String, String>> lista = new ArrayList<>();
        // conexões por requisição (fechadas no finally); o ranking é sempre geral,
        // sem filtro por ano (um filtro permitiria descobrir o ano do jogo de hoje)
        UserGameDao ugd = new UserGameDao();
        UsuarioDao ud = new UsuarioDao();
        JogoDao jd = new JogoDao();
        try {
            for (UserGame userGame : ugd.getLista()) {
                lista.add(detalhesPublicos(userGame, ud, jd));
            }
        } finally {
            try { ugd.fechaConexao(); } catch (Exception ignored) {}
            try { ud.fechaConexao(); } catch (Exception ignored) {}
            try { jd.fechaConexao(); } catch (Exception ignored) {}
        }

        //                 (Map<String, String> m1, Map<String, String> m2) -> {
        //                    return Integer.valueOf(m1.get("Pontuação")).compareTo(Integer.valueOf(m2.get("Pontuação")));
        //                }
        // maior pontuação primeiro; empate: data mais antiga primeiro (data em ISO, ordena como texto)
        lista.sort(Comparator.comparing((Map<String, String> m) -> Integer.valueOf(m.get("Pontuação"))).reversed()
                .thenComparing(m -> m.get("Dia ISO")));


        for (Map<String, String> linha : lista) {
            linha.remove("Dia ISO");
        }

        String mensagem = lista.isEmpty() ? "Nenhuma partida jogada ainda." : lista.size() + " partida(s) no ranking.";

        req.setAttribute("status", "sucesso");
        req.setAttribute("entidade", "Partida (UserGame)");
        req.setAttribute("mensagem", mensagem);
        req.setAttribute("detalhes", new LinkedHashMap<>());
        req.setAttribute("lista", lista);
        req.setAttribute("paginaVoltar", "jogar");
        req.getRequestDispatcher("roteadorGame.jsp").forward(req, resp);
    }



    // detalhes da partida que usuarios podem ver
    private static Map<String, String> detalhesPublicos(UserGame userGame, UsuarioDao ud, JogoDao jd) {
        Map<String, String> d = new LinkedHashMap<>();
        Usuario user = ud.busca(userGame.getUserID());
        Jogo j = jd.busca(userGame.getAno());

        d.put("Usuário", String.valueOf(user.getNickname()));
        String iso = j.getDataJogada() == null ? "" : new Date(j.getDataJogada().getTimeInMillis()).toString();
        d.put("Dia jogado", iso.isEmpty() ? "-" : new java.text.SimpleDateFormat("dd/MM/yyyy").format(j.getDataJogada().getTime()));
        d.put("Pontuação", String.valueOf(userGame.getPontuacao()));
        d.put("Dia ISO", iso); // só para ordenar; removido antes de exibir
        return d;
    }

    // campos da partida para a tabela da página de sucesso
    private static Map<String, String> detalhesDe(UserGame userGame) {
        Map<String, String> d = new LinkedHashMap<>();
        d.put("ID do usuário", String.valueOf(userGame.getUserID()));
        d.put("Ano", String.valueOf(userGame.getAno()));
        d.put("Pontuação", String.valueOf(userGame.getPontuacao()));
        return d;
    }
}