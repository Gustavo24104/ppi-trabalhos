<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Map" %>
<% request.setCharacterEncoding("UTF-8"); %>
<%!
    private static String escapa(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<jsp:include page="header.jsp">
    <jsp:param name="tituloPagina" value="Upload de Arquivos" />
</jsp:include>
<jsp:include page="menu.jsp" />

<main>
    <jsp:useBean id="upl" scope="page" class="yeardle.UploadBeanSeguro" />
    <jsp:setProperty name="upl" property="diretorio" value="arquivos" />
    <jsp:setProperty name="upl" property="extensoesPermitidasPerfil" value="jpg,jpeg,png,gif,webp" />
    <jsp:setProperty name="upl" property="extensoesPermitidasPartida" value="jpg,jpeg,png,gif,webp" />
    <jsp:setProperty name="upl" property="size" value="5" />

    <%
        boolean enviou = upl.doFilePost(request, application);
        String userParam = request.getParameter("userID");
        int userSelecionado = upl.getUserID();
        if (userSelecionado <= 0 && userParam != null && !userParam.isBlank()) {
            try {
                userSelecionado = Integer.parseInt(userParam);
            } catch (NumberFormatException ignored) {
            }
        }

        UsuarioDao uDao = new UsuarioDao();
        UploadDao arqDao = new UploadDao();
        List<Usuario> usuarios = null;
        Map<Integer, Upload> mapaPerfil = null;
        Map<Integer, Upload> mapaPartida = null;
        try {
            usuarios = uDao.getLista();
            mapaPerfil = arqDao.mapaPorContexto("perfil");
            mapaPartida = arqDao.mapaPorContexto("partida");
        } finally {
            try { uDao.fechaConexao(); } catch (Exception ignored) {}
            try { arqDao.fechaConexao(); } catch (Exception ignored) {}
        }
    %>

    <% if (enviou) { %>
        <div class="alerta-sucesso">
            <h2>Upload Concluído</h2>
            <p><%= escapa(upl.getMensagemSucesso()) %></p>
            <ul>
                <% if (upl.getFilenamePerfil() != null) { %>
                    <li><strong>Foto de perfil:</strong> <%= escapa(upl.getFilenamePerfil()) %>
                        &mdash; <a href="usuario.jsp">Ver e baixar no contexto de Usuários</a></li>
                <% } %>
                <% if (upl.getFilenamePartida() != null) { %>
                    <li><strong>Screenshot do desempenho no jogo:</strong> <%= escapa(upl.getFilenamePartida()) %>
                        &mdash; <a href="usergame.jsp">Ver e baixar no contexto de Partidas</a></li>
                <% } %>
            </ul>
        </div>
        <br>
    <% } else if (upl.getErro() != null) { %>
        <div class="alerta-erro">
            <h2>Erro no Upload</h2>
            <p><jsp:getProperty name="upl" property="erro" /></p>
        </div>
        <br>
    <% } %>

    <form method="post" action="upload.jsp" enctype="multipart/form-data">
        <fieldset>
            <legend>Seleção de Usuário e Imagens (máx. <jsp:getProperty name="upl" property="size" /> MB por arquivo)</legend>

            <p>
                <label for="userID">Usuário:</label>
                <select id="userID" name="userID" required>
                    <option value="">-- Selecione um usuário --</option>
                    <% if (usuarios != null) {
                        for (Usuario u : usuarios) { %>
                        <option value="<%= u.getUserID() %>" <%= (u.getUserID() == userSelecionado) ? "selected" : "" %>>
                            ID <%= u.getUserID() %> &mdash; <%= escapa(u.getNickname()) %> (<%= escapa(u.getNomeReal()) %>)
                        </option>
                    <%  }
                    } %>
                </select>
            </p>

            <p>
                <label for="arquivoPerfil"><strong>Arquivo 1 &mdash; Foto de perfil (Contexto: Usuários):</strong></label><br>
                <input type="file" id="arquivoPerfil" name="arquivoPerfil" accept="image/*"><br>
                <small>Formatos aceitos: <jsp:getProperty name="upl" property="extensoesPermitidasPerfil" /></small>
            </p>

            <p>
                <label for="arquivoPartida"><strong>Arquivo 2 &mdash; Screenshot do desempenho no jogo (Contexto: Partidas):</strong></label><br>
                <input type="file" id="arquivoPartida" name="arquivoPartida" accept="image/*"><br>
                <small>Formatos aceitos: <jsp:getProperty name="upl" property="extensoesPermitidasPartida" /></small>
            </p>
        </fieldset>

        <p>Você pode enviar as duas imagens juntas ou atualizar apenas uma delas. O download fica disponível em seus respectivos contextos: <a href="usuario.jsp">Usuários</a> (foto de perfil) e <a href="usergame.jsp">Partidas</a> (screenshot do desempenho).</p>

        <button type="submit">Enviar Imagens</button>
        <button type="reset">Limpar</button>
    </form>

    <h3>Resumo Geral de Imagens por Usuário</h3>
    <div class="rolagem">
        <table class="lista">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Apelido</th>
                    <th>Foto de Perfil</th>
                    <th>Screenshot do Desempenho</th>
                </tr>
            </thead>
            <tbody>
            <% if (usuarios != null && !usuarios.isEmpty()) {
                for (Usuario u : usuarios) {
                    Upload p = mapaPerfil.get(u.getUserID());
                    Upload g = mapaPartida.get(u.getUserID());
            %>
                <tr>
                    <td><%= u.getUserID() %></td>
                    <td><%= escapa(u.getNickname()) %></td>
                    <td>
                        <% if (p != null) { %>
                            <a href="download?userID=<%= u.getUserID() %>&amp;contexto=perfil"><%= escapa(p.getNomeOriginal()) %></a>
                        <% } else { %>
                            <em>Não enviada</em>
                        <% } %>
                    </td>
                    <td>
                        <% if (g != null) { %>
                            <a href="download?userID=<%= u.getUserID() %>&amp;contexto=partida"><%= escapa(g.getNomeOriginal()) %></a>
                        <% } else { %>
                            <em>Não enviada</em>
                        <% } %>
                    </td>
                </tr>
            <%  }
            } %>
            </tbody>
        </table>
    </div>
</main>

<jsp:include page="footer.jsp" />