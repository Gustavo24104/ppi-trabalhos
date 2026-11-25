<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setCharacterEncoding("UTF-8"); /* antes do jsp:include, para o jsp:param com acento chegar certo */ %>
<jsp:include page="headerGame.jsp">
    <jsp:param name="tituloPagina" value="Como jogar yeardle" />
</jsp:include>
<jsp:include page="navGame.jsp" />


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



<jsp:include page="footerGame.jsp" />