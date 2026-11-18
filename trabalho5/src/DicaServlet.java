import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/dica")
public class DicaServlet extends HttpServlet {

    private DicaDao dao;

    @Override
    public void init() {
        dao = new DicaDao();
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
            if ("cadastrar".equals(acao)) {
                Dica dica = new Dica();
                dica.setTextoDica(req.getParameter("textoDica"));
                dica.setAnoDica(Integer.parseInt(req.getParameter("anoDica")));
                String id = req.getParameter("dicaID");
                if (id != null && !id.isBlank()) {
                    dica.setDicaID(Integer.parseInt(id));
                }
                dao.adiciona(dica);
                mensagem = "Dica cadastrada com sucesso!";
            } else if ("alterar".equals(acao)) {
                Dica dica = new Dica();
                dica.setDicaID(Integer.parseInt(req.getParameter("dicaID")));
                dica.setTextoDica(req.getParameter("textoDica"));
                dica.setAnoDica(Integer.parseInt(req.getParameter("anoDica")));
                mensagem = dao.altera(dica) ? "Dica alterada com sucesso!" : "Nenhuma dica encontrada com esse ID.";
            } else if ("consultar".equals(acao)) {
                Dica dica = dao.busca(Integer.parseInt(req.getParameter("dicaID")));
                if (dica == null) {
                    mensagem = "Nenhuma dica encontrada com esse ID.";
                } else {
                    mensagem = "<b>ID:</b> " + dica.getDicaID()
                            + "<br><b>Texto:</b> " + escapa(dica.getTextoDica())
                            + "<br><b>Ano:</b> " + dica.getAnoDica();
                }
            } else if ("remover".equals(acao)) {
                mensagem = dao.remove(Integer.parseInt(req.getParameter("dicaID")))
                        ? "Dica removida com sucesso!" : "Nenhuma dica encontrada com esse ID.";
            } else {
                mensagem = "Ação inválida.";
            }
        } catch (NumberFormatException e) {
            mensagem = "Preencha os campos numéricos com valores válidos.";
        } catch (RuntimeException e) {
            // erros do banco, ex.: ano inexistente em jogo (chave estrangeira) ou ID duplicado
            mensagem = "Erro no banco de dados: " + escapa(String.valueOf(e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
        }

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html><html lang=\"pt-br\"><head><meta charset=\"UTF-8\"><title>Dica</title></head><body>");
        out.println("<h1>Dica</h1>");
        out.println("<p>" + mensagem + "</p>");
        out.println("<a href=\"dica.html\">Voltar</a>");
        out.println("</body></html>");
    }

    // evita que o texto digitado seja interpretado como HTML
    private static String escapa(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
