<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    if (session.getAttribute("userID") == null || session.getAttribute("anoCorreto") == null) {
        response.sendRedirect("jogar");
        return;
    }
%>
<% request.setCharacterEncoding("UTF-8"); /* antes do jsp:include, para o jsp:param com acento chegar certo */ %>
<%@ page import="java.util.List, java.util.Map, yeardle.*" %>
<%!
    private static String escapa(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<jsp:include page="headerGame.jsp">
    <jsp:param name="tituloPagina" value="Você perdeu!" />
</jsp:include>
<jsp:include page="navGame.jsp" />


<main>
    <div class="alerta-erro">
        <h2>Você perdeu! :(</h2>
        <h1> Pontuação: <%= session.getAttribute("pontuacao") != null ? session.getAttribute("pontuacao") : 0 %> </h1>
        <p> O ano era: <%= session.getAttribute("anoCorreto") %></p>
        <p><a href="jogar?proximo=1">Jogar outro dia</a></p>
    </div>
</main>

<jsp:include page="footerGame.jsp" />