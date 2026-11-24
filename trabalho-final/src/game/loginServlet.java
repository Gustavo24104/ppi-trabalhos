package game;

import yeardle.Campos;

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
        int userID = Integer.parseInt(req.getParameter("userID"));

        HttpSession sessaoAntiga = req.getSession(false);
        if (sessaoAntiga != null) {
            sessaoAntiga.invalidate();
        }

        HttpSession novaSessao = req.getSession(true);
        novaSessao.setAttribute("userID", userID);

        // 4. Redireciona para o jogo
        resp.sendRedirect("jogar");
    }
}
