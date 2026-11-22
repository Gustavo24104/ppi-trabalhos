package yeardle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String acao = req.getParameter("acao");
        Campos campos = new Campos(req);
        boolean sucesso = false;
        String mensagem;
        Map<String, String> detalhes = new LinkedHashMap<>();
        List> lista = null;

        try {
            if ("listar".equals(acao)) {
                lista = new ArrayList<>();
                Map<Integer, Upload> mapaPartida = arquivoDao.mapaPorContexto("partida");
                for (UserGame userGame : dao.getLista()) {
                    lista.add(detalhesDe(userGame, mapaPartida.get(userGame.getUserID())));
                }
                sucesso = true;
                mensagem = lista.isEmpty() ? "Nenhuma partida cadastrada." : lista.size() + " partida(s) cadastrada(s).";
            } else if ("cadastrar".equals(acao)) {
                UserGame userGame = new UserGame();
                userGame.setUserID(campos.inteiro("userID"));
                userGame.setAno(campos.inteiro("ano"));
                userGame.setPontuacao(campos.inteiro("pontuacao"));
                dao.adiciona(userGame);
                sucesso = true;
                mensagem = "Partida cadastrada com sucesso!";
            } else if ("alterar".equals(acao)) {
                // a chave primaria e composta: (userID, ano); pontuacao é NOT NULL, então não pode ser apagada
                UserGame userGame = dao.busca(campos.inteiro("userID"), campos.inteiro("ano"));
                if (userGame == null) {
                    mensagem = "Nenhuma partida encontrada para esse usuário e ano.";
                } else if (!campos.preenchido("pontuacao")) {
                    mensagem = "Nenhuma alteração informada: preencha a nova pontuação.";
                } else {
                    userGame.setPontuacao(campos.inteiro("pontuacao"));
                    if (dao.altera(userGame)) {
                        sucesso = true;
                        mensagem = "Partida alterada com sucesso!";
                        Upload screenshot = arquivoDao.buscaPorUsuarioEContexto(userGame.getUserID(), "partida");
                        detalhes = detalhesDe(userGame, screenshot);
                    } else {
                        mensagem = "Nenhuma partida encontrada para esse usuário e ano.";
                    }
                }
            } else if ("consultar".equals(acao)) {
                UserGame userGame = dao.busca(campos.inteiro("userID"), campos.inteiro("ano"));
                if (userGame == null) {
                    mensagem = "Nenhuma partida encontrada para esse usuário e ano.";
                } else {
                    sucesso = true;
                    mensagem = "Partida encontrada com sucesso!";
                    Upload screenshot = arquivoDao.buscaPorUsuarioEContexto(userGame.getUserID(), "partida");
                    detalhes = detalhesDe(userGame, screenshot);
                }
            } else if ("remover".equals(acao)) {
                if (dao.remove(campos.inteiro("userID"), campos.inteiro("ano"))) {
                    sucesso = true;
                    mensagem = "Partida removida com sucesso!";
                } else {
                    mensagem = "Nenhuma partida encontrada para esse usuário e ano.";
                }
            } else {
                mensagem = "Ação inválida.";
            }
        } catch (NumberFormatException e) {
            mensagem = "Preencha os campos numéricos com valores válidos.";
        } catch (RuntimeException e) {
            // erros do banco, ex.: usuário ou jogo inexistente (chave estrangeira) ou chave duplicada
            mensagem = "Erro no banco de dados: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
        }

        req.setAttribute("status", sucesso ? "sucesso" : "erro");
        req.setAttribute("entidade", "Partida (UserGame)");
        req.setAttribute("mensagem", mensagem);
        req.setAttribute("detalhes", detalhes);
        req.setAttribute("lista", lista);
        req.setAttribute("paginaVoltar", "usergame.jsp");
        req.getRequestDispatcher("roteador.jsp").forward(req, resp);
    }

    // campos da partida para a tabela da página de sucesso
    private static Map<String, String> detalhesDe(UserGame userGame, Upload screenshot) {
        Map<String, String> d = new LinkedHashMap<>();
        d.put("ID do usuário", String.valueOf(userGame.getUserID()));
        d.put("Ano", String.valueOf(userGame.getAno()));
        d.put("Pontuação", String.valueOf(userGame.getPontuacao()));
        d.put("Screenshot do desempenho", screenshot != null ? screenshot.getNomeOriginal() : "-");
        return d;
    }
}