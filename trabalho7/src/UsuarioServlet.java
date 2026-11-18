import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

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
                    sucesso = true;
                    mensagem = "Usuário cadastrado com sucesso!";
                } else if (usuario.getUserID() > 0 && dao.altera(usuario)) {
                    sucesso = true;
                    mensagem = "Usuário alterado com sucesso!";
                } else {
                    mensagem = "Nenhum usuário encontrado com esse ID.";
                }
            } else if ("consultar".equals(acao)) {
                Usuario usuario = dao.busca(Integer.parseInt(req.getParameter("userID")));
                if (usuario == null) {
                    mensagem = "Nenhum usuário encontrado com esse ID.";
                } else {
                    sucesso = true;
                    mensagem = "Usuário encontrado com sucesso!";
                    detalhes.put("ID", String.valueOf(usuario.getUserID()));
                    detalhes.put("Pontos totais", String.valueOf(usuario.getPontosTotais()));
                    detalhes.put("Nome", usuario.getNomeReal());
                    detalhes.put("Apelido", usuario.getNickname());
                    detalhes.put("E-mail", usuario.getEmail());
                    detalhes.put("Cidade", usuario.getCidade());
                    detalhes.put("Ano favorito", usuario.getAnoFav() > 0 ? String.valueOf(usuario.getAnoFav()) : "-");
                    detalhes.put("Categoria favorita", usuario.getCategoriaFav() == null ? "-" : usuario.getCategoriaFav());
                }
            } else if ("remover".equals(acao)) {
                if (dao.remove(Integer.parseInt(req.getParameter("userID")))) {
                    sucesso = true;
                    mensagem = "Usuário removido com sucesso!";
                } else {
                    mensagem = "Nenhum usuário encontrado com esse ID.";
                }
            } else {
                mensagem = "Ação inválida.";
            }
        } catch (NumberFormatException e) {
            mensagem = "Preencha os campos numéricos com valores válidos.";
        } catch (RuntimeException e) {
            // erros do banco, ex.: apelido ou e-mail duplicado
            mensagem = "Erro no banco de dados: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
        }

        req.setAttribute("status", sucesso ? "sucesso" : "erro");
        req.setAttribute("entidade", "Usuário");
        req.setAttribute("mensagem", mensagem);
        req.setAttribute("detalhes", detalhes);
        req.setAttribute("paginaVoltar", "usuario.jsp");
        req.getRequestDispatcher("roteador.jsp").forward(req, resp);
    }
}
