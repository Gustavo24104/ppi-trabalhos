import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.Calendar;

@WebServlet("/jogo")
public class JogoServlet extends HttpServlet {

    private JogoDao dao;

    @Override
    public void init() {
        dao = new JogoDao();
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
            int ano = Integer.parseInt(req.getParameter("ano"));

            if ("cadastrar".equals(acao) || "alterar".equals(acao)) {
                Jogo jogo = new Jogo();
                jogo.setAno(ano);
                jogo.setCategoria(vazioParaNulo(req.getParameter("categoria")));
                String data = req.getParameter("dataJogada");
                if (data != null && !data.isBlank()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(Date.valueOf(data));
                    jogo.setDataJogada(calendar);
                }

                if ("cadastrar".equals(acao)) {
                    dao.adiciona(jogo);
                    mensagem = "Jogo cadastrado com sucesso!";
                } else {
                    mensagem = dao.altera(jogo) ? "Jogo alterado com sucesso!" : "Nenhum jogo encontrado com esse ano.";
                }
            } else if ("consultar".equals(acao)) {
                Jogo jogo = dao.busca(ano);
                if (jogo == null) {
                    mensagem = "Nenhum jogo encontrado com esse ano.";
                } else {
                    String data = jogo.getDataJogada() == null ? "-" : new Date(jogo.getDataJogada().getTimeInMillis()).toString();
                    mensagem = "<b>Ano:</b> " + jogo.getAno()
                            + "<br><b>Data jogada:</b> " + data
                            + "<br><b>Categoria:</b> " + (jogo.getCategoria() == null ? "-" : escapa(jogo.getCategoria()));
                }
            } else if ("remover".equals(acao)) {
                mensagem = dao.remove(ano) ? "Jogo removido com sucesso!" : "Nenhum jogo encontrado com esse ano.";
            } else {
                mensagem = "Ação inválida.";
            }
        } catch (IllegalArgumentException e) {
            // NumberFormatException (ano) e data invalida
            mensagem = "Preencha os campos com valores válidos.";
        } catch (RuntimeException e) {
            // erros do banco, ex.: ano duplicado ou fora da regra (ano > 1900)
            mensagem = "Erro no banco de dados: " + escapa(String.valueOf(e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
        }

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html><html lang=\"pt-br\"><head><meta charset=\"UTF-8\"><title>Jogo</title></head><body>");
        out.println("<h1>Jogo</h1>");
        out.println("<p>" + mensagem + "</p>");
        out.println("<a href=\"jogo.html\">Voltar</a>");
        out.println("</body></html>");
    }

    // evita que o texto digitado seja interpretado como HTML
    private static String escapa(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static String vazioParaNulo(String s) {
        return s == null || s.isBlank() ? null : s;
    }
}
