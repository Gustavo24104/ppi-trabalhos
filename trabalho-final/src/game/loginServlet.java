package game;

import yeardle.Campos;
import yeardle.Usuario;
import yeardle.UsuarioDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;



@WebServlet("/login")
public class loginServlet extends HttpServlet  {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nome = req.getParameter("username");
        String senha = req.getParameter("senha");

        UsuarioDao ud = new UsuarioDao();
        Usuario u = ud.buscaPorNickname(nome);


        if (senha.equals(u.getSenha())) {
            HttpSession sessaoAntiga = req.getSession(false);
            if (sessaoAntiga != null) {
                sessaoAntiga.invalidate();
            }

            HttpSession novaSessao = req.getSession(true);
            novaSessao.setAttribute("userID", u.getUserID());


            if (u.isEhAdmin()) {
                req.getRequestDispatcher("index.jsp").forward(req, resp);
                return;
            } else {
                resp.sendRedirect("jogar");
            }
        } else {
            resp.sendRedirect("indexGame.jsp");
        }
    }
}
