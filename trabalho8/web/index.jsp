<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setCharacterEncoding("UTF-8"); /* antes do jsp:include, para o jsp:param com acento chegar certo */ %>
<jsp:include page="header.jsp">
    <jsp:param name="tituloPagina" value="Painel Principal" />
</jsp:include>
<jsp:include page="menu.jsp" />

<main>
    <p>Selecione um dos módulos no menu acima para cadastrar, alterar, consultar ou remover registros do banco <strong>yeardle</strong>:</p>
    <ul>
        <li><strong><a href="usuario.jsp">Usuários</a>:</strong> gestão de jogadores, pontos totais e preferências.</li>
        <li><strong><a href="jogo.jsp">Jogos</a>:</strong> cadastro de anos-alvo, datas jogadas e categorias.</li>
        <li><strong><a href="dica.jsp">Dicas</a>:</strong> pistas vinculadas a cada ano cadastrado em jogo.</li>
        <li><strong><a href="usergame.jsp">Partidas (UserGame)</a>:</strong> registro de pontuação por usuário e ano.</li>
        <li><strong><a href="upload.jsp">Upload de Arquivos</a>:</strong> envio da foto de perfil e do screenshot de desempenho no jogo por usuário.</li>
    </ul>
</main>

<jsp:include page="footer.jsp" />