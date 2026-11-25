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

<%
    UsuarioDao uDao = new UsuarioDao();
    List<Usuario> usuarios = null;
    try {
        usuarios = uDao.getLista();
    } finally {
      try { uDao.fechaConexao(); } catch (Exception ignored) {}
    }

    Integer tent = (Integer) session.getAttribute("tentativas");
    int tentativas = tent != null ? tent : 0;

    Jogo jogoAtual = (Jogo) session.getAttribute("jogoAtual");
    List<Dica> dicas = new java.util.ArrayList<Dica>();
    if (jogoAtual != null) {
        DicaDao dd = new DicaDao();
        try {
            dicas = dd.buscaPorJogo(jogoAtual.getAno());
        } finally {
            try { dd.fechaConexao(); } catch (Exception ignored) {}
        }
    }
    Integer rev = (Integer) session.getAttribute("dicasReveladas");
    int dicasReveladas = (rev != null) ? rev : 0;
    boolean temMaisDicas = dicasReveladas < dicas.size();

%>

<%
    String mensagemData = (String) request.getAttribute("mensagemData");
    String dataMin = (String) request.getAttribute("dataMin");
    String dataMax = (String) request.getAttribute("dataMax");
    String dataSel = (String) request.getAttribute("dataSelecionada");
    String dataBr = (String) request.getAttribute("dataBr");
%>
<div style="text-align:center;">
    <form method="get" action="jogar">
        <label for="data">Escolha o dia do jogo:</label>
        <input type="date" id="data" name="data" min="<%= escapa(dataMin) %>" max="<%= escapa(dataMax) %>" value="<%= escapa(dataSel) %>" required>
        <button type="submit">Jogar este dia</button>
    </form>
    <% if (dataBr != null) { %>
        <p>Jogo do dia <strong><%= escapa(dataBr) %></strong></p>
    <% } %>
    <% if (mensagemData != null) { %>
        <p class="alerta-erro"><%= escapa(mensagemData) %></p>
    <% } %>
</div>

<%
    String mensagemChute = (String) session.getAttribute("mensagemChute");
    session.removeAttribute("mensagemChute");
    if (mensagemChute != null) {
%>
<div style="text-align:center;"><p class="alerta-erro"><%= escapa(mensagemChute) %></p></div>
<% } %>

<%
    Jogo jogoJogado = (Jogo) request.getAttribute("jogoJogado");
    Integer pontJogada = (Integer) request.getAttribute("pontuacaoJogada");
    if (jogoJogado != null) {
        boolean ganhouJogado = pontJogada != null && pontJogada != 0;
%>
<div style="text-align:center;">
    <p class="<%= ganhouJogado ? "alerta-sucesso" : "alerta-erro" %>">
        Você já jogou o jogo deste dia:
        <%= ganhouJogado ? "ganhou" : "perdeu" %>,
        <%= pontJogada != null ? pontJogada : 0 %> pontos,
        o ano era <strong><%= jogoJogado.getAno() %></strong>.
    </p>
    <img src="download?data=<%= escapa(dataSel) %>&amp;contexto=jogo&amp;modo=inline"
     alt="Foto marcante"
     style="width: 800px; height: 600px; object-fit: contain; border-radius: 4px; vertical-align: middle; margin-right: 8px;">
</div>
<jsp:include page="footerGame.jsp" />
<% return; } %>

<% if (jogoAtual == null) { %>
    <jsp:include page="footerGame.jsp" />
<% return; } %>

<div style="text-align:center;">
    <h3>Imagem marcante do ano:</h3>
    <img src="download?data=<%= escapa(dataSel) %>&amp;contexto=jogo&amp;modo=inline"
     alt="Foto marcante"
     style="width: 800px; height: 600px; object-fit: contain; border-radius: 4px; vertical-align: middle; margin-right: 8px;">
</div>
<form method="post" action="jogar">
        <input type="hidden" name="data" value="<%= escapa(dataSel) %>">
        <p>
            <div style="text-align:center;">
                <label for="guess">Dê seu chute:</label>
                <input type="number" id="guess" name="guess" placeholder="1900" min="1901" max="<%= java.time.LocalDate.now().getYear() %>" required>
                <br>
                <span>Tentativas restantes: <%= 5 - tentativas %></span>
            </div>
            <div style="text-align:center;">
                <button type="submit" name="chutar"> Chutar </button>
                <button type="submit" formaction="gameDica" <%= temMaisDicas ? "" : "disabled" %>
                            name="dica" formnovalidate> Dica </button>
                <button type="reset"> Limpar</button>
            </div>
            <% String chutes = (String) session.getAttribute("chutes");
               if (chutes != null && !chutes.isEmpty()) { %>
                <p style="text-align:center;">Seus chutes: <%= escapa(chutes.replace(",", ", ")) %></p>
            <% } %>
            <hr>
            <div id="lista-dicas">
                <% for (int i = 0; i < Math.min(dicasReveladas, dicas.size()); i++) { %>
                    <p><%= escapa(dicas.get(i).getTextoDica()) %></p>
                <% } %>
            </div>
        </p>
</form>


<jsp:include page="footerGame.jsp" />