package yeardle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/download")
public class DownloadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userIdParam = req.getParameter("userID");
        String anoParam = req.getParameter("ano");
        String nameParam = req.getParameter("fileName");
        String contexto = req.getParameter("contexto");
        String dataParam = req.getParameter("data");

        boolean ehAdmin = Boolean.TRUE.equals(req.getSession().getAttribute("ehAdmin"));
        // jogadores nao podem usar "ano" (vazaria a resposta na URL); devem usar "data"
        if ("jogo".equals(contexto) && (anoParam != null || nameParam != null) && !ehAdmin) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado.");
            return;
        }
        if ("jogo".equals(contexto) && dataParam != null) {
            LocalDate d;
            try {
                d = LocalDate.parse(dataParam);
            } catch (Exception e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Data inválida.");
                return;
            }
            if (d.isAfter(LocalDate.now())) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Jogo não disponível.");
                return;
            }
            JogoDao jd = new JogoDao();
            Jogo jogo;
            try {
                jogo = jd.buscaPorDataJogada(new GregorianCalendar(d.getYear(), d.getMonthValue() - 1, d.getDayOfMonth()));
            } finally {
                try { jd.fechaConexao(); } catch (Exception ignored) {}
            }
            if (jogo == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Jogo não encontrado.");
                return;
            }
            anoParam = String.valueOf(jogo.getAno());
        }

        boolean contextoValido = "perfil".equals(contexto) || "jogo".equals(contexto);
        if (!contextoValido || (userIdParam == null && anoParam == null && nameParam == null)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetros de download inválidos.");
            return;
        }

        int userID = -1;
        if (userIdParam != null) {
            try {
                userID = Integer.parseInt(userIdParam);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de usuário inválido.");
                return;
            }
        }

        int ano = -1;
        if (anoParam != null) {
            try {
                ano = Integer.parseInt(anoParam);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ano inválido.");
                return;
            }
        }

        String descricaoContexto = "perfil".equals(contexto)
                ? "foto de perfil"
                : "foto marcante do ano";

        UploadDao dao = new UploadDao();
        Upload arquivo;
        try {
            if (nameParam != null) {
                arquivo = dao.buscaPorNome(nameParam, contexto);
            } else if ("jogo".equals(contexto)) {
                arquivo = dao.buscaPorJogo(ano);
            } else {
                arquivo = dao.buscaPorUsuarioEContexto(userID, contexto);
            }
        } finally {
            try {
                dao.fechaConexao();
            } catch (Exception ignored) {
            }
        }

        if (arquivo == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Nenhuma " + descricaoContexto + " encontrada para este usuário.");
            return;
        }

        String caminhoFisico = getServletContext().getRealPath("arquivos" + File.separator + contexto);
        File file = new File(caminhoFisico, arquivo.getNomeSalvo());

        if (!file.exists() || !file.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Arquivo físico de " + descricaoContexto + " não encontrado no servidor.");
            return;
        }

        String mimeType = arquivo.getTipoConteudo();
        if (mimeType == null || mimeType.isBlank()) {
            mimeType = getServletContext().getMimeType(file.getName());
        }
        if (mimeType == null || mimeType.isBlank()) {
            mimeType = "application/octet-stream";
        }

        String nomeOriginal = arquivo.getNomeOriginal();
        if ("jogo".equals(contexto) && !ehAdmin) {
            // o nome do arquivo pode conter o ano (resposta): jogadores recebem um nome neutro
            String nomeSalvo = arquivo.getNomeSalvo();
            int ponto = nomeSalvo.lastIndexOf('.');
            nomeOriginal = "foto" + (ponto >= 0 ? nomeSalvo.substring(ponto) : "");
        }
        String nomeCodificado = URLEncoder.encode(nomeOriginal, StandardCharsets.UTF_8.name()).replace("+", "%20");
        String disposicao = "inline".equals(req.getParameter("modo")) ? "inline" : "attachment";

        resp.setContentType(mimeType);
        resp.setContentLengthLong(file.length());
        resp.setHeader("Content-Disposition",
                disposicao + "; filename=\"" + nomeOriginal.replace("\"", "") + "\"; filename*=UTF-8''" + nomeCodificado);

        try (FileInputStream in = new FileInputStream(file);
             OutputStream out = resp.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesLidos;
            while ((bytesLidos = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesLidos);
            }
        }
    }
}