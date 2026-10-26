public class Usuario {
    private int userID;
    private int pontosTotais;
    private String nomeReal;
    private String nickname;
    private String email;
    private String cidade;
    private int anoFav;
    private String categoriaFav;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getPontosTotais() {
        return pontosTotais;
    }

    public void setPontosTotais(int pontosTotais) {
        this.pontosTotais = pontosTotais;
    }

    public String getNomeReal() {
        return nomeReal;
    }

    public void setNomeReal(String nomeReal) {
        this.nomeReal = nomeReal;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public int getAnoFav() {
        return anoFav;
    }

    public void setAnoFav(int anoFav) {
        this.anoFav = anoFav;
    }

    public String getCategoriaFav() {
        return categoriaFav;
    }

    public void setCategoriaFav(String categoriaFav) {
        this.categoriaFav = categoriaFav;
    }
}
