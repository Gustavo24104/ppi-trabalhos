package yeardle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
        String nameParam = req.getParameter("fileName");
        String contexto = req.getParameter("contexto");

        if ((userIdParam == null && nameParam == null) || (!"perfil".equals(contexto) && !"partida".equals(contexto))) {
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

        String descricaoContexto = "perfil".equals(contexto)
                ? "foto de perfil"
                : "screenshot do desempenho no jogo";

        UploadDao dao = new UploadDao();
        Upload arquivo;
        try {
            if(nameParam != null) {
                arquivo = dao.buscaPorNome(nameParam, contexto);
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