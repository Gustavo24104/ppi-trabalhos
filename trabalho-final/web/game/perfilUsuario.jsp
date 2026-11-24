<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% request.setCharacterEncoding("UTF-8"); /* antes do jsp:include, para o jsp:param com acento chegar certo */ %>
<jsp:include page="headerGame.jsp">
    <jsp:param name="tituloPagina" value="Editar perfil" />
</jsp:include>
<jsp:include page="navGame.jsp" />

<jsp:include page="footerGame.jsp" />