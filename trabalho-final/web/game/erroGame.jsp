<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setCharacterEncoding("UTF-8"); %>
<%!
    private static String escapa(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<%
    String mensagem = (String) request.getAttribute("mensagem");
%>
<jsp:include page="headerGame.jsp">
    <jsp:param name="tituloPagina" value="Erro" />
</jsp:include>

<main>
    <div class="alerta-erro">
        <h2>Erro</h2>
        <p><%= escapa(mensagem != null ? mensagem : "Ocorreu um erro.") %></p>
    </div>

    <p><a href="indexGame.jsp">Voltar</a></p>
</main>

<jsp:include page="footerGame.jsp" />
