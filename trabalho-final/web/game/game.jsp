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

<form method="post" action="jogar" enctype="multipart/form-data">
    <%
    UsuarioDao uDao = new UsuarioDao();
    List<Usuario> usuarios = null;
    try {
        usuarios = uDao.getLista();
    } finally {
      try { uDao.fechaConexao(); } catch (Exception ignored) {}
    }
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
                <%  }
                } %>
            </select>
            <div style="text-align:center;">
                <label for="guess">Dê seu chute:</label>
                <input type="number" id="guess" name="guess" placeholder="1900">
                <br>
                <a> Pontuacao atual: 67 </a>
            </div>
            <div style="text-align:center;">
                <button type="submit" style="border-radius: 50%"> Chutar 🥾 </button>
                <button type="submit" style="border-radius: 50%" > Dica 💡 </button>
                <button type="reset" style="border-radius: 50%" > Limpar 🪌 </button>
            </div>
            <hr>
            <div>
                <a> ano oficial do luigi </a> <br>
                <a> mais dicas... </a>
            </div>
        </p>
</form>


<jsp:include page="footerGame.jsp" />