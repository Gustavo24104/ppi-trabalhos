package yeardle;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadBeanSeguro implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final int KB = 1024;
    private static final int MB = 1024 * 1024;

    private String diretorio = "arquivos";
    private int size = 5;
    private String extensoesPermitidasPerfil = "jpg,jpeg,png,gif,webp";
    private String extensoesPermitidasPartida = "jpg,jpeg,png,gif,webp";

    private int userID;
    private String filenamePerfil;
    private String filenamePartida;
    private String erro = null;
    private String mensagemSucesso = null;

    public UploadBeanSeguro() {
    }

    public String getDiretorio() {
        return diretorio;
    }

    public void setDiretorio(String diretorio) {
        this.diretorio = diretorio;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getExtensoesPermitidasPerfil() {
        return extensoesPermitidasPerfil;
    }

    public void setExtensoesPermitidasPerfil(String extensoesPermitidasPerfil) {
        this.extensoesPermitidasPerfil = extensoesPermitidasPerfil;
    }

    public String getExtensoesPermitidasPartida() {
        return extensoesPermitidasPartida;
    }

    public void setExtensoesPermitidasPartida(String extensoesPermitidasPartida) {
        this.extensoesPermitidasPartida = extensoesPermitidasPartida;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getFilenamePerfil() {
        return filenamePerfil;
    }

    public String getFilenamePartida() {
        return filenamePartida;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public String getMensagemSucesso() {
        return mensagemSucesso;
    }

    public boolean doFilePost(HttpServletRequest request, ServletContext context) {
        if (request.getContentType() == null) {
            return false;
        }
        if (!request.getContentType().toLowerCase().startsWith("multipart/form-data")) {
            return false;
        }

        DiskFileItemFactory fileUpload = new DiskFileItemFactory();
        fileUpload.setSizeThreshold(4 * KB);

        ServletFileUpload sfu = new ServletFileUpload(fileUpload);
        sfu.setHeaderEncoding("UTF-8");
        sfu.setFileSizeMax((long) getSize() * MB);
        sfu.setSizeMax((long) getSize() * 2L * MB);

        String basePath = context.getRealPath(getDiretorio());
        if (basePath == null) {
            setErro("Não foi possível resolver o diretório físico de armazenamento no servidor.");
            return false;
        }

        UsuarioDao usuarioDao = null;
        UploadDao arquivoDao = null;

        try {
            List<FileItem> list = sfu.parseRequest(request);

            String userIdStr = null;
            for (FileItem item : list) {
                if (item.isFormField() && "userID".equals(item.getFieldName())) {
                    userIdStr = item.getString("UTF-8");
                }
            }

            if (userIdStr == null || userIdStr.isBlank()) {
                setErro("Selecione um usuário válido para vincular as imagens.");
                return false;
            }

            try {
                this.userID = Integer.parseInt(userIdStr.trim());
            } catch (NumberFormatException e) {
                setErro("ID de usuário inválido.");
                return false;
            }

            usuarioDao = new UsuarioDao();
            Usuario usuario = usuarioDao.busca(this.userID);
            if (usuario == null) {
                setErro("Nenhum usuário encontrado com o ID informado.");
                return false;
            }

            arquivoDao = new UploadDao();
            int enviados = 0;

            for (FileItem item : list) {
                if (!item.isFormField()) {
                    String fieldName = item.getFieldName();
                    String rawName = item.getName();

                    if (rawName == null || rawName.isBlank()) {
                        continue;
                    }

                    String nomeOriginal = new File(rawName).getName();
                    String contexto;
                    String rotuloContexto;
                    String extensoesContexto;

                    if ("arquivoPerfil".equals(fieldName)) {
                        contexto = "perfil";
                        rotuloContexto = "Foto de perfil";
                        extensoesContexto = extensoesPermitidasPerfil;
                    } else if ("arquivoPartida".equals(fieldName)) {
                        contexto = "partida";
                        rotuloContexto = "Screenshot do desempenho no jogo";
                        extensoesContexto = extensoesPermitidasPartida;
                    } else {
                        continue;
                    }

                    String contentType = item.getContentType();
                    if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
                        setErro(rotuloContexto + ": o arquivo \"" + nomeOriginal + "\" não é uma imagem válida.");
                        return false;
                    }

                    if (!isPermission(nomeOriginal, extensoesContexto)) {
                        setErro(rotuloContexto + ": extensão não permitida para \"" + nomeOriginal
                              + "\". Formatos aceitos: " + extensoesContexto);
                        return false;
                    }

                    File pastaContexto = new File(basePath, contexto);
                    if (!pastaContexto.exists()) {
                        pastaContexto.mkdirs();
                    }

                    String nomeSalvo = "user_" + this.userID + "_" + contexto + "_"
                                     + System.currentTimeMillis() + "_" + nomeOriginal;
                    File destino = new File(pastaContexto, nomeSalvo);
                    item.write(destino);

                    Upload registro = new Upload();
                    registro.setUserID(this.userID);
                    registro.setContexto(contexto);
                    registro.setNomeOriginal(nomeOriginal);
                    registro.setNomeSalvo(nomeSalvo);
                    registro.setTipoConteudo(contentType);
                    registro.setTamanhoBytes(item.getSize());

                    String antigoSalvo = arquivoDao.salvaOuAtualiza(registro);
                    if (antigoSalvo != null && !antigoSalvo.equals(nomeSalvo)) {
                        File arquivoAntigo = new File(pastaContexto, antigoSalvo);
                        if (arquivoAntigo.exists()) {
                            arquivoAntigo.delete();
                        }
                    }

                    if ("perfil".equals(contexto)) {
                        this.filenamePerfil = nomeOriginal;
                    } else {
                        this.filenamePartida = nomeOriginal;
                    }
                    enviados++;
                }
            }

            if (enviados == 0) {
                setErro("Selecione ao menos uma imagem (Foto de perfil ou Screenshot do desempenho) para enviar.");
                return false;
            }

            this.mensagemSucesso = enviados + " imagem(ns) enviada(s) com sucesso para o usuário "
                                 + usuario.getNickname() + " (ID " + this.userID + ")!";
            return true;

        } catch (FileUploadBase.FileSizeLimitExceededException slee) {
            setErro("Tamanho excedido: cada imagem deve ter no máximo " + getSize() + " MB.");
            return false;
        } catch (FileUploadBase.SizeLimitExceededException sle) {
            setErro("Tamanho total da requisição excedido.");
            return false;
        } catch (Exception e) {
            setErro("Uma exceção ocorreu durante o upload: " + e.getMessage());
            return false;
        } finally {
            try {
                if (usuarioDao != null) usuarioDao.fechaConexao();
                if (arquivoDao != null) arquivoDao.fechaConexao();
            } catch (Exception ignored) {
            }
        }
    }

    public boolean isPermission(String fileName, String listaExtensoes) {
        if (fileName == null || listaExtensoes == null) {
            return false;
        }
        String lowerCaseName = fileName.toLowerCase();
        StringTokenizer st = new StringTokenizer(listaExtensoes, ",");
        while (st.hasMoreTokens()) {
            String ext = st.nextToken().trim().toLowerCase();
            if (!ext.isEmpty() && lowerCaseName.endsWith("." + ext)) {
                return true;
            }
        }
        return false;
    }
}