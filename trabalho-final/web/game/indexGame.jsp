<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setCharacterEncoding("UTF-8"); /* antes do jsp:include, para o jsp:param com acento chegar certo */ %>
<jsp:include page="headerGame.jsp">
    <jsp:param name="tituloPagina" value="Yeardle" />
</jsp:include>
<%-- landing page: sem menu (navGame.jsp) no topo --%>

<h2>Login</h2>
<form action="login" method="post">
    <p>
        <label for="username">Nome de usuário:</label> <br>
        <input type="text" id="username" name="username" placeholder="Seu nome de usuário" required>
    </p>
    <p>
        <label for="senha">Senha:</label><br>
        <input type="password" id="senha" name="senha" required>
    </p>
    <button type="submit" name="enviar"> Login </button>
</form>

<h2>Como jogar</h2>
<ol>
    <li>Todo dia há um novo ano misterioso. Seu objetivo é descobrir qual é esse ano.</li>
    <li>Digite o ano que você acha que é e clique para chutar. Você tem no máximo <strong>5 tentativas</strong>.</li>
    <li>A cada chute errado, uma nova <strong>dica</strong> sobre o ano é revelada. Você também pode clicar no botão
        "Dica" para revelar uma dica a mais, sem gastar tentativa (enquanto ainda houver dicas).</li>
    <li>Quanto menos dicas reveladas ao acertar, mais pontos você ganha:
        <strong>4000 pontos menos 1000 por dica revelada</strong>.
        Ou seja: nenhuma dica = 4000, 1 dica = 3000, 2 dicas = 2000, 3 dicas = 1000, 4 dicas = 0.</li>
    <li>Se acabarem as 5 tentativas sem acertar, a partida termina e o ano correto é mostrado.</li>
    <li>Só é possível jogar uma vez por dia. Veja sua posição no Leaderboard.</li>
</ol>

<h2>Vídeo tutorial</h2>
<iframe width="640" height="480" src="https://www.youtube.com/embed/7ZEgNDfEWYM?autoplay=1">
    O vídeo não pode ser tocado.
</iframe>

<jsp:include page="footerGame.jsp" />
