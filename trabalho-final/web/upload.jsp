<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Map, yeardle.*" %>
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
    <jsp:setProperty name="upl" property="extensoesPermitidasJogo" value="jpg,jpeg,png,gif,webp" />
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

        String anoParam = request.getParameter("ano");
        int anoSelecionado = upl.getAno();
        if (anoSelecionado <= 0 && anoParam != null && !anoParam.isBlank()) {
            try {
                anoSelecionado = Integer.parseInt(anoParam);
            } catch (NumberFormatException ignored) {
            }
        }

        UsuarioDao uDao = new UsuarioDao();
        JogoDao jDao = new JogoDao();
        UploadDao arqDao = new UploadDao();
        List<Usuario> usuarios = null;
        List<Jogo> jogos = null;
        Map<Integer, Upload> mapaPerfil = null;
        Map<Integer, Upload> mapaPartida = null;
        Map<Integer, Upload> mapaJogo = null;
        try {
            usuarios = uDao.getLista();
            jogos = jDao.getLista();
            mapaPerfil = arqDao.mapaPorContexto("perfil");
            mapaPartida = arqDao.mapaPorContexto("partida");
            mapaJogo = arqDao.mapaPorJogo();
        } finally {
            try { uDao.fechaConexao(); } catch (Exception ignored) {}
            try { jDao.fechaConexao(); } catch (Exception ignored) {}
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
                <% if (upl.getFilenameJogo() != null) { %>
                    <li><strong>Foto marcante do ano (Dica Zero):</strong> <%= escapa(upl.getFilenameJogo()) %>
                        &mdash; <a href="jogo.jsp">Ver e baixar no contexto de Jogos</a></li>
                <% } %>
            </ul>
        </div>
        <br>
    <% } else if (upl.getErro() != null) { %>
        <div class="alerta-erro">
            <h2>Erro no Upload</h2>
            <p><%= escapa(upl.getErro()) %></p>
        </div>
        <br>
    <% } %>

    <form method="post" action="upload.jsp" enctype="multipart/form-data">
        <fieldset>
            <legend>Seleção de Usuário e Imagens (máx. <jsp:getProperty name="upl" property="size" /> MB por arquivo)</legend>

            <p>
                <label for="userID">Usuário:</label>
                <select id="userID" name="userID">
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

        <fieldset>
            <legend>Seleção de Jogo e Foto Marcante &mdash; Dica Zero (máx. <jsp:getProperty name="upl" property="size" /> MB)</legend>

            <p>
                <label for="ano">Ano do jogo:</label>
                <select id="ano" name="ano">
                    <option value="">-- Selecione um ano --</option>
                    <% if (jogos != null) {
                        for (Jogo j : jogos) { %>
                        <option value="<%= j.getAno() %>" <%= (j.getAno() == anoSelecionado) ? "selected" : "" %>>
                            Ano <%= j.getAno() %>
                        </option>
                    <%  }
                    } %>
                </select>
            </p>

            <p>
                <label for="arquivoJogo"><strong>Arquivo 3 &mdash; Foto marcante do ano (Contexto: Jogos / Dica Zero):</strong></label><br>
                <input type="file" id="arquivoJogo" name="arquivoJogo" accept="image/*"><br>
                <small>Formatos aceitos: <jsp:getProperty name="upl" property="extensoesPermitidasJogo" /></small>
            </p>
        </fieldset>

        <p>Você pode enviar imagens de usuário, de jogo ou ambas ao mesmo tempo. O download fica disponível em seus respectivos contextos: <a href="usuario.jsp">Usuários</a> (foto de perfil), <a href="usergame.jsp">Partidas</a> (screenshot do desempenho) e <a href="jogo.jsp">Jogos</a> (foto marcante / Dica Zero).</p>

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

    <h3>Resumo Geral de Fotos Marcantes por Jogo (Dica Zero)</h3>
    <div class="rolagem">
        <table class="lista">
            <thead>
                <tr>
                    <th>Ano</th>
                    <th>Data Jogada</th>
                    <th>Foto Marcante (Dica Zero)</th>
                </tr>
            </thead>
            <tbody>
            <% if (jogos != null && !jogos.isEmpty()) {
                for (Jogo j : jogos) {
                    Upload fotoJogo = mapaJogo.get(j.getAno());
            %>
                <tr>
                    <td><%= j.getAno() %></td>
                    <td><%= j.getDataJogada() == null ? "-" : new java.sql.Date(j.getDataJogada().getTimeInMillis()).toString() %></td>
                    <td>
                        <% if (fotoJogo != null) { %>
                            <a href="download?ano=<%= j.getAno() %>&amp;contexto=jogo"><%= escapa(fotoJogo.getNomeOriginal()) %></a>
                        <% } else { %>
                            <em>Não enviada</em>
                        <% } %>
                    </td>
                </tr>
            <%  }
            } else { %>
                <tr>
                    <td colspan="3">Nenhum jogo cadastrado.</td>
                </tr>
            <% } %>
            </tbody>
        </table>
    </div>
</main>

<jsp:include page="footer.jsp" />
