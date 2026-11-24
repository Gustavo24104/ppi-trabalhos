<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Map, yeardle.*" %>
<% request.setCharacterEncoding("UTF-8"); /* antes do jsp:include, para o jsp:param com acento chegar certo */ %>
<%!
    private static String escapa(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<jsp:include page="header.jsp">
    <jsp:param name="tituloPagina" value="UserGame" />
</jsp:include>
<jsp:include page="menu.jsp" />

<main>
    <form action="usergame" method="post">
        <fieldset>
            <legend>Chave da partida</legend>

            <p>
                <label for="userID">ID do usuário:</label>
                <input type="number" id="userID" name="userID" required placeholder="Ex.: 1">
            </p>
            <p>
                <label for="ano">Ano do jogo:</label>
                <input type="number" id="ano" name="ano" min="1901" required placeholder="Ex.: 1958">
            </p>
        </fieldset>

        <fieldset>
            <legend>Resultado</legend>

            <p>
                <label for="pontuacao">Pontuação:</label>
                <input type="number" id="pontuacao" name="pontuacao" placeholder="Ex.: 10">
            </p>
        </fieldset>

        <p>A pontuação é obrigatória para cadastrar e alterar; ela não pode ficar vazia no banco.</p>

        <button type="submit" name="acao" value="cadastrar">Cadastrar</button>
        <button type="submit" name="acao" value="alterar">Alterar</button>
        <button type="submit" name="acao" value="consultar">Consultar</button>
        <button type="submit" name="acao" value="remover">Remover</button>
        <button type="submit" name="acao" value="listar" formnovalidate>Listar todos</button>
        <button type="reset">Limpar</button>
    </form>

    <%
        UsuarioDao usuarioDao = new UsuarioDao();
        UploadDao arquivoDao = new UploadDao();
        List<Usuario> usuarios = null;
        Map<Integer, Upload> arquivosPartida = null;
        try {
            usuarios = usuarioDao.getLista();
            arquivosPartida = arquivoDao.mapaPorContexto("partida");
        } finally {
            try { usuarioDao.fechaConexao(); } catch (Exception ignored) {}
            try { arquivoDao.fechaConexao(); } catch (Exception ignored) {}
        }
    %>

</main>

<jsp:include page="footer.jsp" />