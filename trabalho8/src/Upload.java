import java.io.Serializable;
import java.sql.Timestamp;

public class Upload implements Serializable {
    private static final long serialVersionUID = 1L;

    private int arquivoID;
    private int userID;
    private String contexto;
    private String nomeOriginal;
    private String nomeSalvo;
    private String tipoConteudo;
    private long tamanhoBytes;
    private Timestamp dataUpload;

    public Upload() {
    }

    public int getArquivoID() {
        return arquivoID;
    }

    public void setArquivoID(int arquivoID) {
        this.arquivoID = arquivoID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getContexto() {
        return contexto;
    }

    public void setContexto(String contexto) {
        this.contexto = contexto;
    }

    public String getNomeOriginal() {
        return nomeOriginal;
    }

    public void setNomeOriginal(String nomeOriginal) {
        this.nomeOriginal = nomeOriginal;
    }

    public String getNomeSalvo() {
        return nomeSalvo;
    }

    public void setNomeSalvo(String nomeSalvo) {
        this.nomeSalvo = nomeSalvo;
    }

    public String getTipoConteudo() {
        return tipoConteudo;
    }

    public void setTipoConteudo(String tipoConteudo) {
        this.tipoConteudo = tipoConteudo;
    }

    public long getTamanhoBytes() {
        return tamanhoBytes;
    }

    public void setTamanhoBytes(long tamanhoBytes) {
        this.tamanhoBytes = tamanhoBytes;
    }

    public Timestamp getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(Timestamp dataUpload) {
        this.dataUpload = dataUpload;
    }
}