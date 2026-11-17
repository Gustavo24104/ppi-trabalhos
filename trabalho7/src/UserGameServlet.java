import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String acao = req.getParameter("acao");
        String mensagem;

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
                mensagem = "Partida cadastrada com sucesso!";
            } else if ("alterar".equals(acao)) {
                UserGame userGame = new UserGame();
                userGame.setUserID(userID);
                userGame.setAno(ano);
                userGame.setPontuacao(Integer.parseInt(req.getParameter("pontuacao")));
                mensagem = dao.altera(userGame) ? "Partida alterada com sucesso!" : "Nenhuma partida encontrada para esse usuário e ano.";
            } else if ("consultar".equals(acao)) {
                UserGame userGame = dao.busca(userID, ano);
                if (userGame == null) {
                    mensagem = "Nenhuma partida encontrada para esse usuário e ano.";
                } else {
                    mensagem = "<b>ID do usuário:</b> " + userGame.getUserID()
                            + "<br><b>Ano:</b> " + userGame.getAno()
                            + "<br><b>Pontuação:</b> " + userGame.getPontuacao();
                }
            } else if ("remover".equals(acao)) {
                mensagem = dao.remove(userID, ano)
                        ? "Partida removida com sucesso!" : "Nenhuma partida encontrada para esse usuário e ano.";
            } else {
                mensagem = "Ação inválida.";
            }
        } catch (NumberFormatException e) {
            mensagem = "Preencha os campos numéricos com valores válidos.";
        } catch (RuntimeException e) {
            // erros do banco, ex.: usuário ou jogo inexistente (chave estrangeira) ou chave duplicada
            mensagem = "Erro no banco de dados: " + escapa(String.valueOf(e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
        }

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html><html lang=\"pt-br\"><head><meta charset=\"UTF-8\"><title>UserGame</title></head><body>");
        out.println("<h1>UserGame</h1>");
        out.println("<p>" + mensagem + "</p>");
        out.println("<a href=\"usergame.html\">Voltar</a>");
        out.println("</body></html>");
    }

    // evita que o texto do erro seja interpretado como HTML
    private static String escapa(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
