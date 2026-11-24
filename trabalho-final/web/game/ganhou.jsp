<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setCharacterEncoding("UTF-8"); /* antes do jsp:include, para o jsp:param com acento chegar certo */ %>
<%@ page import="java.util.List, java.util.Map, yeardle.*" %>
<%!
    private static String escapa(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<jsp:include page="headerGame.jsp">
    <jsp:param name="tituloPagina" value="Você ganhou!" />
</jsp:include>
<jsp:include page="navGame.jsp" />


<main>
    <div class="alerta-sucesso">
        <h2>Você ganhou!! :) </h2>
        <h1> Pontuacao: <%= session.getAttribute("pontuacao") %> </h1>
    </div>
</main>

<jsp:include page="footer.jsp" />