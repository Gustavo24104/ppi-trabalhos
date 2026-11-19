<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String tituloPagina = request.getParameter("tituloPagina");
    if (tituloPagina == null || tituloPagina.isBlank()) {
        tituloPagina = "Yeardle";
    }
%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title><%= tituloPagina %> - Yeardle</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 820px; margin: 24px auto; padding: 0 16px; line-height: 1.5; color: #222; }
        header { border-bottom: 2px solid #333; padding-bottom: 10px; margin-bottom: 12px; }
        header h1 { margin: 0; font-size: 1.6rem; }
        nav { background: #f4f4f4; padding: 10px 14px; border-radius: 6px; margin-bottom: 20px; }
        nav a { margin-right: 16px; text-decoration: none; color: #0b57d0; font-weight: bold; }
        nav a:hover { text-decoration: underline; }
        fieldset { margin-bottom: 16px; border: 1px solid #ccc; border-radius: 6px; padding: 12px 16px; }
        legend { font-weight: bold; padding: 0 6px; }
        button { padding: 7px 14px; margin-right: 6px; cursor: pointer; }
        .alerta-sucesso { background: #e8f5e9; border: 1px solid #4caf50; color: #1b5e20; padding: 14px; border-radius: 6px; }
        .alerta-erro { background: #ffebee; border: 1px solid #e53935; color: #b71c1c; padding: 14px; border-radius: 6px; }
        table { border-collapse: collapse; margin-top: 14px; width: 100%; }
        th, td { border: 1px solid #ccc; padding: 8px 10px; text-align: left; }
        th { background: #f4f4f4; width: 32%; }
        .rolagem { overflow-x: auto; }
        table.lista th { width: auto; white-space: nowrap; }
        label.apagar { margin-left: 10px; font-size: 0.9rem; color: #555; }
        footer { border-top: 1px solid #ccc; margin-top: 28px; padding-top: 12px; font-size: 0.9rem; color: #666; }
    </style>
</head>
<body>
<header>
    <h1>Yeardle &mdash; <%= tituloPagina %></h1>
</header>