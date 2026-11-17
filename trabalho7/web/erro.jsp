<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%!
    private static String escapa(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<%
    String entidade = (String) request.getAttribute("entidade");
    String mensagem = (String) request.getAttribute("mensagem");
    String paginaVoltar = (String) request.getAttribute("paginaVoltar");
%>
<jsp:include page="header.jsp">
    <jsp:param name="tituloPagina" value="Erro na Operação" />
</jsp:include>
<jsp:include page="menu.jsp" />

<main>
    <div class="alerta-erro">
        <h2>Erro &mdash; <%= escapa(entidade) %></h2>
        <p><%= escapa(mensagem) %></p>
    </div>

    <p><a href="<%= paginaVoltar != null ? paginaVoltar : "index.jsp" %>">Voltar e tentar novamente</a></p>
</main>

<jsp:include page="footer.jsp" />