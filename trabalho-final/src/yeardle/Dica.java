package yeardle;

public class Dica {
    private int dicaID;
    private String textoDica;
    private int anoDica;
    private String categoria;


    public int getDicaID() {
        return dicaID;
    }

    public void setDicaID(int dicaID) {
        this.dicaID = dicaID;
    }

    public String getTextoDica() {
        return textoDica;
    }

    public void setTextoDica(String textoDica) {
        this.textoDica = textoDica;
    }

    public int getAnoDica() {
        return anoDica;
    }

    public void setAnoDica(int anoDica) {
        this.anoDica = anoDica;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String toString() {
        return textoDica;
    }
}
