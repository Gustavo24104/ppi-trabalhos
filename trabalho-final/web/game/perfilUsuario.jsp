<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Map, yeardle.*, game.*" %>
<% request.setCharacterEncoding("UTF-8"); /* antes do jsp:include, para o jsp:param com acento chegar certo */ %>
<jsp:include page="headerGame.jsp">
    <jsp:param name="tituloPagina" value="Trocar de usuário" />
</jsp:include>
<%!
    private static String escapa(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<jsp:include page="navGame.jsp" />

<form method="post" action="login">
<label for="userID">Usuário:</label>
<select id="userID" name="userID" required>
    <option value="">-- Selecione um usuário --</option>
    <%
        UsuarioDao uDao = new UsuarioDao();
        List<Usuario> usuarios = null;
        try {
            usuarios = uDao.getLista();
        } finally {
          try { uDao.fechaConexao(); } catch (Exception ignored) {}
        }

        if (usuarios != null) {
        for (Usuario u : usuarios) { %>
        <option value="<%= u.getUserID() %>" <%= (u.getUserID() == 1) ? "selected" : "" %>>
            ID <%= u.getUserID() %> &mdash; <%= escapa(u.getNickname()) %> (<%= escapa(u.getNomeReal()) %>)
        </option>
    <%  }
    } %> </select> <br>
    <button type="submit" name="login"> Entrar como usuário </button>
</form>

<jsp:include page="footerGame.jsp" />