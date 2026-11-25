package yeardle;

// progresso de uma partida em andamento
public class Progresso {
    private int tentativas;
    private int dicasReveladas;
    private String chutes = ""; // chutes separados por virgula

    public int getTentativas() { return tentativas; }
    public void setTentativas(int tentativas) { this.tentativas = tentativas; }
    public int getDicasReveladas() { return dicasReveladas; }
    public void setDicasReveladas(int dicasReveladas) { this.dicasReveladas = dicasReveladas; }
    public String getChutes() { return chutes; }
    public void setChutes(String chutes) { this.chutes = (chutes == null) ? "" : chutes; }
}
