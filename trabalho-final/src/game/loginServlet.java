package game;

import yeardle.Usuario;
import yeardle.UsuarioDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet("/login")
public class loginServlet extends HttpServlet  {

    // encaminha para a pagina de erro
    private void erro(HttpServletRequest req, HttpServletResponse resp, String mensagem) throws ServletException, IOException {
        req.setAttribute("entidade", "Login");
        req.setAttribute("mensagem", mensagem);
        req.setAttribute("paginaVoltar", "indexGame.jsp");
        req.getRequestDispatcher("erroGame.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String nome = req.getParameter("username");
        if (nome != null) nome = nome.trim();
        String senha = req.getParameter("senha");

        if (nome == null || nome.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            erro(req, resp, "Informe o nickname e a senha.");
            return;
        }

        Usuario u = null;
        UsuarioDao ud = new UsuarioDao();
        try {
            u = ud.buscaPorNickname(nome);
        } finally {
            try { ud.fechaConexao(); } catch (Exception ignored) {}
        }

        if (u == null) {
            erro(req, resp, "Usuário '" + nome + "' não encontrado.");
            return;
        }

        if (senha.equals(u.getSenha())) {
            HttpSession sessaoAntiga = req.getSession(false);
            if (sessaoAntiga != null) {
                sessaoAntiga.invalidate();
            }

            HttpSession novaSessao = req.getSession(true);
            novaSessao.setAttribute("userID", u.getUserID());
            novaSessao.setAttribute("ehAdmin", u.isEhAdmin());

            if (u.isEhAdmin()) {
                resp.sendRedirect("index.jsp");
            } else {
                resp.sendRedirect("jogar");
            }
        } else {
            erro(req, resp, "Senha incorreta.");
        }
    }
}
