package yeardle;

import java.util.Calendar;

public class Jogo {
    private int ano;
    private Calendar dataJogada;
    private String categoria;

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public Calendar getDataJogada() {
        return dataJogada;
    }

    public void setDataJogada(Calendar dataJogada) {
        this.dataJogada = dataJogada;
    }

    public boolean equals(Jogo outro) {
        return outro.ano == this.ano &&
                outro.getDataJogada().equals(this.getDataJogada()) &&
                outro.categoria.equals(this.categoria);
    }
}
