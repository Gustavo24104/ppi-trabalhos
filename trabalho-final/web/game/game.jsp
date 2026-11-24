<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Collections, yeardle.*, game.*" %>
<% request.setCharacterEncoding("UTF-8"); /* antes do jsp:include, para o jsp:param com acento chegar certo */ %>
<jsp:include page="headerGame.jsp">
    <jsp:param name="tituloPagina" value="Jogar!" />
</jsp:include>
<%!
    private static String escapa(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<jsp:include page="navGame.jsp" />

<main>
    <form method="post" action="jogar">
        <%
            UsuarioDao uDao = new UsuarioDao();
            DicaDao dd = new DicaDao();
            UploadDao uploadDao = new UploadDao();

            List<Usuario> usuarios = null;
            List<Dica> dicas = Collections.emptyList();
            Upload fotoDicaZero = null;

            Jogo jogoAtual = (Jogo) session.getAttribute("jogoAtual");
            int tentativas = (session.getAttribute("tentativas") != null)
                    ? (Integer) session.getAttribute("tentativas")
                    : 0;
            Integer rev = (Integer) session.getAttribute("dicasReveladas");
            int dicasReveladas = (rev != null) ? rev : 0;

            try {
                usuarios = uDao.getLista();
                if (jogoAtual != null) {
                    dicas = dd.buscaPorJogo(jogoAtual.getAno());
                    fotoDicaZero = uploadDao.buscaPorJogo(jogoAtual.getAno());
                }
            } finally {
                try { uDao.fechaConexao(); } catch (Exception ignored) {}
                try { dd.fechaConexao(); } catch (Exception ignored) {}
                try { uploadDao.fechaConexao(); } catch (Exception ignored) {}
            }

            boolean temMaisDicas = dicasReveladas < dicas.size();
        %>

        <p>
            <label for="userID">Usuário:</label>
            <select id="userID" name="userID" required>
                <option value="">-- Selecione um usuário --</option>
                <%
                    if (usuarios != null) {
                        for (Usuario u : usuarios) { %>
                        <option value="<%= u.getUserID() %>" <%= (u.getUserID() == 1) ? "selected" : "" %>>
                            ID <%= u.getUserID() %> &mdash; <%= escapa(u.getNickname()) %> (<%= escapa(u.getNomeReal()) %>)
                        </option>
                <%      }
                    } %>
            </select>
        </p>

        <div style="text-align:center;">
            <label for="guess">Dê seu chute:</label>
            <input type="number" id="guess" name="guess" min="1901" placeholder="1900" required>
            <br>
            <span>Tentativas restantes: <%= 5 - tentativas %></span>
        </div>

        <div style="text-align:center; margin-top: 10px;">
            <button type="submit" name="chutar">Chutar</button>
            <button type="submit" formaction="gameDica" <%= temMaisDicas ? "" : "disabled" %>
                    name="dica" formnovalidate>Dica</button>
            <button type="reset">Limpar</button>
        </div>

        <hr>

        <% if (jogoAtual != null && fotoDicaZero != null) { %>
            <div id="dica-zero" style="text-align: center; margin: 16px 0;">
                <p><strong>Dica 0 &mdash; Imagem marcante do ano:</strong></p>
                <img src="download?ano=<%= jogoAtual.getAno() %>&amp;contexto=jogo&amp;modo=inline"
                     alt="Imagem marcante do ano (Dica 0)"
                     style="max-width: 100%; max-height: 320px; border-radius: 8px; border: 1px solid #ccc; box-shadow: 0 2px 6px rgba(0,0,0,0.12);">
            </div>
        <% } %>

        <div id="lista-dicas">
            <% for (int i = 0; i < Math.min(dicasReveladas, dicas.size()); i++) { %>
                <p><strong>Dica <%= i + 1 %>:</strong> <%= escapa(dicas.get(i).toString()) %></p>
            <% } %>
        </div>
    </form>
</main>

<jsp:include page="footerGame.jsp" />
