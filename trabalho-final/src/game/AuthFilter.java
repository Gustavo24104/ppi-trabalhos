package game;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// Controle de acesso: sessao "userID" (Integer) e "ehAdmin" (Boolean), gravadas no login.
// Aplica-se so a requisicoes diretas (REQUEST); forwards/includes internos passam livres.
@WebFilter("/*")
public class AuthFilter implements Filter {

    // paginas/servlets publicos
    private static final Set<String> PUBLICOS = new HashSet<>(Arrays.asList(
            "", "/", "/indexGame.jsp", "/login", "/logout", "/erroGame.jsp"));

    // so administradores
    private static final Set<String> ADMIN = new HashSet<>(Arrays.asList(
            "/index.jsp", "/usuario.jsp", "/jogo.jsp", "/dica.jsp", "/usergame.jsp", "/upload.jsp",
            "/menu.jsp", "/header.jsp", "/footer.jsp", "/roteador.jsp", "/sucesso.jsp", "/erro.jsp",
            "/usuario", "/jogo", "/dica"));

    private boolean estatico(String caminho) {
        return caminho.endsWith(".css") || caminho.endsWith(".js") || caminho.endsWith(".ico");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String caminho = req.getServletPath();
        if (PUBLICOS.contains(caminho) || estatico(caminho)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        Object userID = session == null ? null : session.getAttribute("userID");

        // evita mostrar paginas privadas pelo botao Voltar apos o logout
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        if (userID == null) {
            resp.sendRedirect(req.getContextPath() + "/indexGame.jsp");
            return;
        }

        boolean ehAdmin = Boolean.TRUE.equals(session.getAttribute("ehAdmin"));
        // fotos dos jogos tem o ano no nome do arquivo: jogadores so as veem via /download
        boolean exigeAdmin = ADMIN.contains(caminho) || caminho.startsWith("/arquivos/jogo/")
                || ("/usergame".equals(caminho) && "POST".equalsIgnoreCase(req.getMethod()));

        if (exigeAdmin && !ehAdmin) {
            resp.sendRedirect(req.getContextPath() + "/jogar");
            return;
        }

        chain.doFilter(request, response);
    }
}
