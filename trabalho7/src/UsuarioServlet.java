import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/usuario")
public class UsuarioServlet extends HttpServlet {

    private UsuarioDao dao;

    @Override
    public void init() {
        dao = new UsuarioDao();
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
            if ("cadastrar".equals(acao) || "alterar".equals(acao)) {
                Usuario usuario = new Usuario();
                usuario.setPontosTotais(Integer.parseInt(req.getParameter("pontosTotais")));
                usuario.setNomeReal(req.getParameter("nomeReal"));
                usuario.setNickname(req.getParameter("nickname"));
                usuario.setEmail(req.getParameter("email"));
                usuario.setCidade(req.getParameter("cidade"));
                String anoFav = req.getParameter("anoFav");
                if (anoFav != null && !anoFav.isBlank()) {
                    usuario.setAnoFav(Integer.parseInt(anoFav));
                }
                String categoria = req.getParameter("categoriaFav");
                usuario.setCategoriaFav(categoria == null || categoria.isBlank() ? null : categoria);
                String id = req.getParameter("userID");
                if (id != null && !id.isBlank()) {
                    usuario.setUserID(Integer.parseInt(id));
                }

                if ("cadastrar".equals(acao)) {
                    dao.adiciona(usuario);
                    mensagem = "Usuário cadastrado com sucesso!";
                } else {
                    mensagem = usuario.getUserID() > 0 && dao.altera(usuario)
                            ? "Usuário alterado com sucesso!" : "Nenhum usuário encontrado com esse ID.";
                }
            } else if ("consultar".equals(acao)) {
                Usuario usuario = dao.busca(Integer.parseInt(req.getParameter("userID")));
                if (usuario == null) {
                    mensagem = "Nenhum usuário encontrado com esse ID.";
                } else {
                    mensagem = "<b>ID:</b> " + usuario.getUserID()
                            + "<br><b>Pontos totais:</b> " + usuario.getPontosTotais()
                            + "<br><b>Nome:</b> " + escapa(usuario.getNomeReal())
                            + "<br><b>Apelido:</b> " + escapa(usuario.getNickname())
                            + "<br><b>E-mail:</b> " + escapa(usuario.getEmail())
                            + "<br><b>Cidade:</b> " + escapa(usuario.getCidade())
                            + "<br><b>Ano favorito:</b> " + (usuario.getAnoFav() > 0 ? usuario.getAnoFav() : "-")
                            + "<br><b>Categoria favorita:</b> " + (usuario.getCategoriaFav() == null ? "-" : escapa(usuario.getCategoriaFav()));
                }
            } else if ("remover".equals(acao)) {
                mensagem = dao.remove(Integer.parseInt(req.getParameter("userID")))
                        ? "Usuário removido com sucesso!" : "Nenhum usuário encontrado com esse ID.";
            } else {
                mensagem = "Ação inválida.";
            }
        } catch (NumberFormatException e) {
            mensagem = "Preencha os campos numéricos com valores válidos.";
        } catch (RuntimeException e) {
            // erros do banco, ex.: apelido ou e-mail duplicado
            mensagem = "Erro no banco de dados: " + escapa(String.valueOf(e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
        }

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<!DOCTYPE html><html lang=\"pt-br\"><head><meta charset=\"UTF-8\"><title>Usuário</title></head><body>");
        out.println("<h1>Usuário</h1>");
        out.println("<p>" + mensagem + "</p>");
        out.println("<a href=\"usuario.html\">Voltar</a>");
        out.println("</body></html>");
    }

    // evita que o texto digitado seja interpretado como HTML
    private static String escapa(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
