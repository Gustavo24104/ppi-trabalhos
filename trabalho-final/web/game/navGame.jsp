<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<nav>
    <a href="logout">Sair</a>
    <a href="jogar">Jogar</a>
    <a href="usergame">Leaderboard</a>
    <a href="perfilUsuario.jsp">Meu Perfil</a>
    <% if (Boolean.TRUE.equals(session.getAttribute("ehAdmin"))) { %>
    <a href="index.jsp">Painel admin</a>
    <% } %>
</nav>