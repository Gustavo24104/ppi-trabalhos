<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String status = (String) request.getAttribute("status");
    if ("sucesso".equals(status)) {
%>
    <jsp:forward page="sucesso.jsp" />
<%
    } else {
%>
    <jsp:forward page="erro.jsp" />
<%
    }
%>