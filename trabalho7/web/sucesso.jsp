<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%!
    private static String escapa(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<%
    String entidade = (String) request.getAttribute("entidade");
    String mensagem = (String) request.getAttribute("mensagem");
    String paginaVoltar = (String) request.getAttribute("paginaVoltar");
    @SuppressWarnings("unchecked")
    Map<String, String> detalhes = (Map<String, String>) request.getAttribute("detalhes");
%>
<jsp:include page="header.jsp">
    <jsp:param name="tituloPagina" value="Operação Concluída" />
</jsp:include>
<jsp:include page="menu.jsp" />

<main>
    <div class="alerta-sucesso">
        <h2>Sucesso &mdash; <%= escapa(entidade) %></h2>
        <p><%= escapa(mensagem) %></p>
    </div>

    <% if (detalhes != null && !detalhes.isEmpty()) { %>
        <table>
            <tbody>
            <% for (Map.Entry<String, String> item : detalhes.entrySet()) { %>
                <tr>
                    <th><%= escapa(item.getKey()) %></th>
                    <td><%= escapa(item.getValue()) %></td>
                </tr>
            <% } %>
            </tbody>
        </table>
    <% } %>

    <p><a href="<%= paginaVoltar != null ? paginaVoltar : "index.jsp" %>">Voltar ao formulário</a></p>
</main>

<jsp:include page="footer.jsp" />