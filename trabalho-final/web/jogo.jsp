<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Map, java.sql.Date, yeardle.*" %>
<% request.setCharacterEncoding("UTF-8"); /* antes do jsp:include, para o jsp:param com acento chegar certo */ %>
<%!
    private static String escapa(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<jsp:include page="header.jsp">
    <jsp:param name="tituloPagina" value="Jogo" />
</jsp:include>
<jsp:include page="menu.jsp" />

<main>
    <form action="jogo" method="post">
        <fieldset>
            <legend>Dados do jogo</legend>

            <p>
                <label for="ano">Ano:</label>
                <input type="number" id="ano" name="ano" min="1901" required placeholder="Ex.: 1958">
            </p>
            <p>
                <label for="dataJogada">Data jogada:</label>
                <input type="date" id="dataJogada" name="dataJogada">
                <label class="apagar"><input type="checkbox" name="limpar" value="dataJogada"> apagar valor salvo</label>
            </p>
        </fieldset>

        <p>O ano é a chave: consultar e remover usam só ele. Para alterar, informe o ano e apenas os campos que quiser mudar
            (campo em branco mantém o valor salvo). Para deixar um campo vazio no banco, marque "apagar valor salvo".</p>

        <button type="submit" name="acao" value="cadastrar">Cadastrar</button>
        <button type="submit" name="acao" value="alterar">Alterar</button>
        <button type="submit" name="acao" value="consultar">Consultar</button>
        <button type="submit" name="acao" value="remover">Remover</button>
        <button type="submit" name="acao" value="listar" formnovalidate>Listar todos</button>
        <button type="reset">Limpar</button>
    </form>

    <%
        JogoDao jogoDao = new JogoDao();
        UploadDao arquivoDao = new UploadDao();
        List<Jogo> jogos = null;
        Map<Integer, Upload> arquivosJogo = null;
        try {
            jogos = jogoDao.getLista();
            arquivosJogo = arquivoDao.mapaPorJogo();
        } finally {
            try { jogoDao.fechaConexao(); } catch (Exception ignored) {}
            try { arquivoDao.fechaConexao(); } catch (Exception ignored) {}
        }
    %>

    <section>
        <h3>Fotos Marcantes dos Jogos (Dica Zero)</h3>
        <div class="rolagem">
            <table class="lista">
                <thead>
                <tr>
                    <th>Ano</th>
                    <th>Data Jogada</th>
                    <th>Foto Marcante (Dica Zero)</th>
                    <th>Upload</th>
                </tr>
                </thead>
                <tbody>
                <% if (jogos != null && !jogos.isEmpty()) {
                    for (Jogo j : jogos) {
                        Upload arq = arquivosJogo.get(j.getAno());
                %>
                    <tr>
                        <td><%= j.getAno() %></td>
                        <td><%= j.getDataJogada() == null ? "-" : new Date(j.getDataJogada().getTimeInMillis()).toString() %></td>
                        <td>
                            <% if (arq != null) { %>
                                <img src="download?ano=<%= j.getAno() %>&amp;contexto=jogo&amp;modo=inline"
                                     alt="Foto marcante de <%= j.getAno() %>"
                                     style="width: 60px; height: 36px; object-fit: cover; border-radius: 4px; vertical-align: middle; margin-right: 8px;">
                                <a href="download?ano=<%= j.getAno() %>&amp;contexto=jogo">
                                    Baixar foto (<%= escapa(arq.getNomeOriginal()) %>)
                                </a>
                            <% } else { %>
                                <em>Nenhuma foto enviada</em>
                            <% } %>
                        </td>
                        <td><a href="upload.jsp?ano=<%= j.getAno() %>">Enviar / Alterar</a></td>
                    </tr>
                <%  }
                } else { %>
                    <tr>
                        <td colspan="4">Nenhum jogo cadastrado.</td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </section>
</main>

<jsp:include page="footer.jsp" />
