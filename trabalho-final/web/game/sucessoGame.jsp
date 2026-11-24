<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setCharacterEncoding("UTF-8"); /* antes do jsp:include, para o jsp:param com acento chegar certo */ %>
<%@ page import="java.util.Map, java.util.List, yeardle.*" %>
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
    // só existe na ação "listar": uma linha (coluna -> valor) por registro
    @SuppressWarnings("unchecked")
    List<Map<String, String>> lista = (List<Map<String, String>>) request.getAttribute("lista");
%>
<jsp:include page="headerGame.jsp">
    <jsp:param name="tituloPagina" value="Leaderboard" />
</jsp:include>
<jsp:include page="navGame.jsp" />

<main>
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

    <% if (lista != null && !lista.isEmpty()) { %>
        <div class="rolagem">
            <table class="lista">
                <thead>
                <tr>
                    <% for (String coluna : lista.get(0).keySet()) { %>
                        <th><%= escapa(coluna) %></th>
                    <% } %>
                </tr>
                </thead>
                <tbody>
                <% for (Map<String, String> linha : lista) { %>
                    <tr>
                        <% for (String valor : linha.values()) { %>
                            <td>
                                <%
                                    String v = (valor != null) ? valor.toLowerCase() : "";
                                    if (v.endsWith(".png") || v.endsWith(".gif") ||
                                        v.endsWith(".jpeg") || v.endsWith(".jpg") || v.endsWith(".webp")) {
                                %>
                                    <img src="download?fileName=<%= escapa(valor) %>&amp;contexto=partida&amp;modo=inline"
                                         alt="Screenshot de desempenho"
                                         style="width: 60px; height: 36px; object-fit: cover; border-radius: 4px; vertical-align: middle; margin-right: 8px;">
                                <% } else { %>
                                    <%= (valor != null) ? escapa(valor) : "" %>
                                <% } %>
                            </td>
                        <% } %>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    <% } %>

    <p><a href="<%= paginaVoltar != null ? paginaVoltar : "index.jsp" %>">Voltar ao formulário</a></p>
</main>

<jsp:include page="footer.jsp" />