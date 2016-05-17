package br.com.mafra.webservice;

/**
 * Created by Leandro on 08/04/2016.
 */
public class Jsonajuda {


    private int id;
    private byte[] imagem;
    private String titulo;
    private double valor;

    public Jsonajuda() {}

    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

    public final byte[] getImagem() {
        return imagem;
    }

    public final void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public final String getTitulo() {
        return titulo;
    }

    public final void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public final double getValor() {
        return valor;
    }

    public final void setValor(double valor) {
        this.valor = valor;
    }
}
