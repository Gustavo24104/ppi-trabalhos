<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, java.util.Map, yeardle.*" %>
<% request.setCharacterEncoding("UTF-8"); /* antes do jsp:include, para o jsp:param com acento chegar certo */ %>
<%!
    private static String escapa(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<jsp:include page="header.jsp">
    <jsp:param name="tituloPagina" value="Usuário" />
</jsp:include>
<jsp:include page="menu.jsp" />

<main>
    <form action="usuario" method="post">
        <fieldset>
            <legend>Identificação</legend>

            <p>
                <label for="userID">ID do usuário:</label>
                <input type="number" id="userID" name="userID" placeholder="Vazio no cadastro = gerado automaticamente">
            </p>
            <p>
                <label for="nomeReal">Nome real:</label>
                <input type="text" id="nomeReal" name="nomeReal" maxlength="100" placeholder="Ex.: João da Silva">
            </p>
            <p>
                <label for="nickname">Apelido:</label>
                <input type="text" id="nickname" name="nickname" maxlength="15" placeholder="Ex.: joaozinho">
            </p>
            <p>
                <label for="email">E-mail:</label>
                <input type="email" id="email" name="email" maxlength="50" placeholder="Ex.: joao@gmail.com">
            </p>
            <p>
                <label for="senha">Senha:</label>
                <input type="password" id="senha" name="senha" maxlength="150" placeholder="Em branco na alteração = mantém a senha salva">
            </p>
            <p>
                <label for="ehAdmin">Administrador:</label>
                <input type="checkbox" id="ehAdmin" name="ehAdmin" value="1">
            </p>
            <p>
                <label for="cidade">Cidade:</label>
                <input type="text" id="cidade" name="cidade" maxlength="100" placeholder="Ex.: Uberlândia">
            </p>
        </fieldset>

        <fieldset>
            <legend>Jogo</legend>

            <p>
                <label for="pontosTotais">Pontos totais:</label>
                <input type="number" id="pontosTotais" name="pontosTotais" placeholder="Ex.: 0">
            </p>
            <p>
                <label for="anoFav">Ano favorito:</label>
                <input type="number" id="anoFav" name="anoFav" min="1901" placeholder="Ex.: 1958">
                <label class="apagar"><input type="checkbox" name="limpar" value="anoFav"> apagar valor salvo</label>
            </p>
            <p>
                <label for="categoriaFav">Categoria favorita:</label>
                <select id="categoriaFav" name="categoriaFav">
                    <option value="">(em branco)</option>
                    <option value="historia">História</option>
                    <option value="cinema">Cinema</option>
                    <option value="literatura">Literatura</option>
                    <option value="musica">Música</option>
                    <option value="ciencias">Ciências</option>
                    <option value="esportes">Esportes</option>
                </select>
                <label class="apagar"><input type="checkbox" name="limpar" value="categoriaFav"> apagar valor salvo</label>
            </p>
        </fieldset>

        <p>Cadastrar usa todos os campos, menos ano e categoria favoritos, que são opcionais. Consultar e remover usam só o ID.
            Para alterar, informe o ID e apenas os campos que quiser mudar (campo em branco mantém o valor salvo).
            Só o ano e a categoria favoritos podem ficar vazios no banco: para isso, marque "apagar valor salvo".</p>

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
        Map<Integer, Upload> arquivosPerfil = null;
        try {
            usuarios = usuarioDao.getLista();
            arquivosPerfil = arquivoDao.mapaPorContexto("perfil");
        } finally {
            try { usuarioDao.fechaConexao(); } catch (Exception ignored) {}
            try { arquivoDao.fechaConexao(); } catch (Exception ignored) {}
        }
    %>

    <section>
        <h3>Fotos de Perfil dos Usuários</h3>
        <div class="rolagem">
            <table class="lista">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Apelido</th>
                    <th>Nome</th>
                    <th>Foto de Perfil</th>
                    <th>Upload</th>
                </tr>
                </thead>
                <tbody>
                <% if (usuarios != null && !usuarios.isEmpty()) {
                    for (Usuario u : usuarios) {
                        Upload arq = arquivosPerfil.get(u.getUserID());
                %>
                    <tr>
                        <td><%= u.getUserID() %></td>
                        <td><%= escapa(u.getNickname()) %></td>
                        <td><%= escapa(u.getNomeReal()) %></td>
                        <td>
                            <% if (arq != null) { %>
                                <img src="download?userID=<%= u.getUserID() %>&amp;contexto=perfil&amp;modo=inline"
                                     alt="Foto de perfil de <%= escapa(u.getNickname()) %>"
                                     style="width: 36px; height: 36px; object-fit: cover; border-radius: 50%; vertical-align: middle; margin-right: 8px;">
                                <a href="download?userID=<%= u.getUserID() %>&amp;contexto=perfil">
                                    Baixar foto (<%= escapa(arq.getNomeOriginal()) %>)
                                </a>
                            <% } else { %>
                                <em>Nenhuma foto enviada</em>
                            <% } %>
                        </td>
                        <td><a href="upload.jsp?userID=<%= u.getUserID() %>">Enviar / Alterar</a></td>
                    </tr>
                <%  }
                } else { %>
                    <tr>
                        <td colspan="5">Nenhum usuário cadastrado.</td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </section>
</main>

<jsp:include page="footer.jsp" />