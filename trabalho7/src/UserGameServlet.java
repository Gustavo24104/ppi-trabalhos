import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/usergame")
public class UserGameServlet extends HttpServlet {

    private UserGameDao dao;

    @Override
    public void init() {
        dao = new UserGameDao();
    }

    @Override
    public void destroy() {
        try {
            dao.fechaConexao();
        } catch (Exception e) {
            log("Erro ao fechar a conexao", e);
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
        boolean sucesso = false;
        String mensagem;
        Map<String, String> detalhes = new LinkedHashMap<>();

        try {
            // a chave primaria e composta: (userID, ano)
            int userID = Integer.parseInt(req.getParameter("userID"));
            int ano = Integer.parseInt(req.getParameter("ano"));

            if ("cadastrar".equals(acao)) {
                UserGame userGame = new UserGame();
                userGame.setUserID(userID);
                userGame.setAno(ano);
                userGame.setPontuacao(Integer.parseInt(req.getParameter("pontuacao")));
                dao.adiciona(userGame);
                sucesso = true;
                mensagem = "Partida cadastrada com sucesso!";
            } else if ("alterar".equals(acao)) {
                UserGame userGame = new UserGame();
                userGame.setUserID(userID);
                userGame.setAno(ano);
                userGame.setPontuacao(Integer.parseInt(req.getParameter("pontuacao")));
                if (dao.altera(userGame)) {
                    sucesso = true;
                    mensagem = "Partida alterada com sucesso!";
                } else {
                    mensagem = "Nenhuma partida encontrada para esse usuário e ano.";
                }
            } else if ("consultar".equals(acao)) {
                UserGame userGame = dao.busca(userID, ano);
                if (userGame == null) {
                    mensagem = "Nenhuma partida encontrada para esse usuário e ano.";
                } else {
                    sucesso = true;
                    mensagem = "Partida encontrada com sucesso!";
                    detalhes.put("ID do usuário", String.valueOf(userGame.getUserID()));
                    detalhes.put("Ano", String.valueOf(userGame.getAno()));
                    detalhes.put("Pontuação", String.valueOf(userGame.getPontuacao()));
                }
            } else if ("remover".equals(acao)) {
                if (dao.remove(userID, ano)) {
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
        req.setAttribute("paginaVoltar", "usergame.jsp");
        req.getRequestDispatcher("roteador.jsp").forward(req, resp);
    }
}
