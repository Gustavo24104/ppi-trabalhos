<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="yeardle.*" %>
<%
    // Sem usuário logado: volta para a tela de login (antes de qualquer saída)
    Integer userID = (Integer) session.getAttribute("userID");
    if (userID == null) {
        response.sendRedirect("indexGame.jsp");
        return;
    }

    Usuario usuario = null;
    UsuarioDao uDao = new UsuarioDao();
    try {
        usuario = uDao.busca(userID);
    } finally {
        try { uDao.fechaConexao(); } catch (Exception ignored) {}
    }
    if (usuario == null) {
        response.sendRedirect("indexGame.jsp");
        return;
    }

    // Verifica se existe foto de perfil (senão o download retornaria 404)
    Upload foto = null;
    UploadDao upDao = new UploadDao();
    try {
        foto = upDao.buscaPorUsuarioEContexto(userID, "perfil");
    } finally {
        try { upDao.fechaConexao(); } catch (Exception ignored) {}
    }
%>
<jsp:include page="headerGame.jsp">
    <jsp:param name="tituloPagina" value="Meu Perfil" />
</jsp:include>
<%!
    private static String escapa(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<jsp:include page="navGame.jsp" />

<h2><%= escapa(usuario.getNickname()) %></h2>

<% if (foto != null) { %>
    <img src="download?userID=<%= usuario.getUserID() %>&amp;contexto=perfil&amp;modo=inline"
         alt="Foto de perfil" width="200">
<% } else { %>
    <p>Sem foto de perfil</p>
<% } %>

<p>Pontuação geral: <%= usuario.getPontosTotais() %></p>

<jsp:include page="footerGame.jsp" />
