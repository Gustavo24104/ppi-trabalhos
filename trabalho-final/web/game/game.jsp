<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Map, yeardle.*, game.*" %>
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

<form method="post" action="jogar">
    <%
    UsuarioDao uDao = new UsuarioDao();
    List<Usuario> usuarios = null;
    try {
        usuarios = uDao.getLista();
    } finally {
      try { uDao.fechaConexao(); } catch (Exception ignored) {}
    }

    int tentativas = ((Integer) session.getAttribute("tentativas")) != null ? (Integer) session.getAttribute("tentativas") : 0;

    Jogo jogoAtual = (Jogo) session.getAttribute("jogoAtual");
    DicaDao dd = new DicaDao();
    List<Dica> dicas = (jogoAtual != null) ? dd.buscaPorJogo(jogoAtual.getAno()) : java.util.Collections.emptyList();
    Integer rev = (Integer) session.getAttribute("dicasReveladas");
    int dicasReveladas = (rev != null) ? rev : 0;
    boolean temMaisDicas = dicasReveladas < dicas.size();
    dd.fechaConexao();

    %>
        <p>


            </select>
            <div style="text-align:center;">
                <label for="guess">Dê seu chute:</label>
                <input type="number" id="guess" name="guess" placeholder="1900" required>
                <br>
                <a> Tentativas restantes: <%= 5 - tentativas %> </a>
            </div>
            <div style="text-align:center;">
                <button type="submit" name="chutar"> Chutar </button>
                <button type="submit" formaction="gameDica" <%= temMaisDicas ? "" : "disabled" %>
                            name="dica" formnovalidate> Dica </button>
                <button type="reset"> Limpar</button>
            </div>
            <hr>
            <div id="lista-dicas">
                <% for (int i = 0; i < Math.min(dicasReveladas, dicas.size()); i++) { %>
                    <p><%= escapa(dicas.get(i).toString()) %></p>
                <% } %>
            </div>
        </p>
</form>


<jsp:include page="footerGame.jsp" />