<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setCharacterEncoding("UTF-8"); /* antes do jsp:include, para o jsp:param com acento chegar certo */ %>
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
            <p>
                <label for="categoria">Categoria:</label>
                <select id="categoria" name="categoria">
                    <option value="">(em branco)</option>
                    <option value="historia">História</option>
                    <option value="cinema">Cinema</option>
                    <option value="literatura">Literatura</option>
                    <option value="musica">Música</option>
                    <option value="ciencias">Ciências</option>
                    <option value="esportes">Esportes</option>
                </select>
                <label class="apagar"><input type="checkbox" name="limpar" value="categoria"> apagar valor salvo</label>
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
</main>

<jsp:include page="footer.jsp" />
