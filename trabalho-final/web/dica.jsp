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
                <label for="anoDica">Ano do jogo:</label>
                <input type="number" id="anoDica" name="anoDica" min="1901" placeholder="Ex.: 1958">
                <label class="apagar"><input type="checkbox" name="limpar" value="anoDica"> apagar valor salvo</label>
            </p>
        </fieldset>

        <p>O ID é obrigatório para alterar, consultar e remover. Para alterar, informe o ID e apenas os campos que quiser mudar
            (campo em branco mantém o valor salvo). Para deixar o ano vazio no banco, marque "apagar valor salvo";
            o texto é obrigatório e não pode ser apagado.</p>

        <button type="submit" name="acao" value="cadastrar">Cadastrar</button>
        <button type="submit" name="acao" value="alterar">Alterar</button>
        <button type="submit" name="acao" value="consultar">Consultar</button>
        <button type="submit" name="acao" value="remover">Remover</button>
        <button type="submit" name="acao" value="listar" formnovalidate>Listar todos</button>
        <button type="reset">Limpar</button>
    </form>
</main>

<jsp:include page="footer.jsp" />
