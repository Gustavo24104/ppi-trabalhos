<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setCharacterEncoding("UTF-8"); /* antes do jsp:include, para o jsp:param com acento chegar certo */ %>
<jsp:include page="header.jsp">
    <jsp:param name="tituloPagina" value="Dica" />
</jsp:include>
<jsp:include page="menu.jsp" />

<main>
    <form action="dica" method="post">
        <fieldset>
            <legend>Dados da dica</legend>

            <p>
                <label for="dicaID">ID da dica:</label>
                <input type="number" id="dicaID" name="dicaID" placeholder="Vazio no cadastro = gerado automaticamente">
            </p>
            <p>
                <label for="textoDica">Texto da dica:</label><br>
                <textarea id="textoDica" name="textoDica" rows="3" cols="40" placeholder="Digite a dica"></textarea>
            </p>
            <p>
                <label for="anoDica">Ano do jogo (obrigatório no cadastro):</label>
                <input type="number" id="anoDica" name="anoDica" min="1901" placeholder="Ex.: 1958" required>
                <label class="apagar"><input type="checkbox" name="limpar" value="anoDica"> apagar valor salvo (só ao alterar)</label>
            </p>
            <p>
                <label for="categoria">Categoria:</label>
                <select id="categoria" name="categoria">
                    <option value="">(escolha; obrigatória no cadastro)</option>
                    <option value="historia">História</option>
                    <option value="cinema">Cinema</option>
                    <option value="literatura">Literatura</option>
                    <option value="musica">Música</option>
                    <option value="ciencias">Ciências</option>
                    <option value="esportes">Esportes</option>
                </select>
            </p>
        </fieldset>

        <p>O ID é obrigatório para alterar, consultar e remover. Para alterar, informe o ID e apenas os campos que quiser mudar
            (campo em branco mantém o valor salvo). Para deixar o ano vazio no banco ao alterar, marque "apagar valor salvo"; no cadastro o ano e o texto são obrigatórios.</p>

        <button type="submit" name="acao" value="cadastrar">Cadastrar</button>
        <button type="submit" name="acao" value="alterar" formnovalidate>Alterar</button>
        <button type="submit" name="acao" value="consultar" formnovalidate>Consultar</button>
        <button type="submit" name="acao" value="remover" formnovalidate>Remover</button>
        <button type="submit" name="acao" value="listar" formnovalidate>Listar todos</button>
        <button type="reset">Limpar</button>
    </form>
</main>

<jsp:include page="footer.jsp" />
