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
    private String extensoesPermitidasJogo = "jpg,jpeg,png,gif,webp";

    private int userID;
    private int ano;
    private String filenamePerfil;
    private String filenameJogo;
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

        public String getExtensoesPermitidasJogo() {
        return extensoesPermitidasJogo;
    }

    public void setExtensoesPermitidasJogo(String extensoesPermitidasJogo) {
        this.extensoesPermitidasJogo = extensoesPermitidasJogo;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

     public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getFilenamePerfil() {
        return filenamePerfil;
    }

     public String getFilenameJogo() {
        return filenameJogo;
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
        JogoDao jogoDao = null;
        UploadDao arquivoDao = null;

        try {
            List<FileItem> list = sfu.parseRequest(request);

            String userIdStr = null;
            String anoStr = null;
            boolean temArquivoUsuario = false;
            boolean temArquivoJogo = false;

            for (FileItem item : list) {
                if (item.isFormField()) {
                    if ("userID".equals(item.getFieldName())) {
                        userIdStr = item.getString("UTF-8");
                    } else if ("ano".equals(item.getFieldName())) {
                        anoStr = item.getString("UTF-8");
                    }
                } else if (item.getName() != null && !item.getName().isBlank()) {
                    String fn = item.getFieldName();
                    if ("arquivoPerfil".equals(fn)) {
                        temArquivoUsuario = true;
                    } else if ("arquivoJogo".equals(fn)) {
                        temArquivoJogo = true;
                    }
                }
            }

            if (!temArquivoUsuario && !temArquivoJogo) {
                setErro("Selecione ao menos uma imagem (Foto de perfil ou Foto marcante do ano) para enviar.");
                return false;
            }

            Usuario usuario = null;
            if (temArquivoUsuario) {
                if (userIdStr == null || userIdStr.isBlank()) {
                    setErro("Selecione um usuário válido para vincular as foto de perfil.");
                    return false;
                }
                try {
                    this.userID = Integer.parseInt(userIdStr.trim());
                } catch (NumberFormatException e) {
                    setErro("ID de usuário inválido.");
                    return false;
                }
                usuarioDao = new UsuarioDao();
                usuario = usuarioDao.busca(this.userID);
                if (usuario == null) {
                    setErro("Nenhum usuário encontrado com o ID informado.");
                    return false;
                }
            }

            Jogo jogo = null;
            if (temArquivoJogo) {
                if (anoStr == null || anoStr.isBlank()) {
                    setErro("Selecione um ano válido para vincular a foto marcante do jogo (Dica Zero).");
                    return false;
                }
                try {
                    this.ano = Integer.parseInt(anoStr.trim());
                } catch (NumberFormatException e) {
                    setErro("Ano do jogo inválido.");
                    return false;
                }
                jogoDao = new JogoDao();
                jogo = jogoDao.busca(this.ano);
                if (jogo == null) {
                    setErro("Nenhum jogo cadastrado com o ano informado.");
                    return false;
                }
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
                    } else if ("arquivoJogo".equals(fieldName)) {
                        contexto = "jogo";
                        rotuloContexto = "Foto marcante do ano (Dica Zero)";
                        extensoesContexto = extensoesPermitidasJogo;
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

                    String prefixo = "jogo".equals(contexto)
                            ? "jogo_" + this.ano
                            : "user_" + this.userID + "_" + contexto;
                    String nomeSalvo = prefixo + "_" + System.currentTimeMillis() + "_" + nomeOriginal;

                    File destino = new File(pastaContexto, nomeSalvo);
                    item.write(destino);

                    Upload registro = new Upload();
                    if ("jogo".equals(contexto)) {
                        registro.setAno(this.ano);
                    } else {
                        registro.setUserID(this.userID);
                    }
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
                        this.filenameJogo = nomeOriginal;
                    }
                    enviados++;
                }
            }

            this.mensagemSucesso = enviados + " imagem(ns) enviada(s) com sucesso!";
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
                if (jogoDao != null) jogoDao.fechaConexao();
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