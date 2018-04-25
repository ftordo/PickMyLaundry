package com.joaomadeira.android.appddm;

/**
 * Created by Fábio on 04/01/2018.
 */

public class Transportador {

    //ATRIBUTOS

    int id;
    String nome;
    String email;
    int telefone;
    Localizacao localizacao;


    //CONSTRUTORES

    public Transportador(String nome, String email, int telefone, String password, Localizacao localizacao) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.localizacao = localizacao;
    }


    //ACESSORES

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTelefone() {
        return telefone;
    }

    public void setTelefone(int telefone) {
        this.telefone = telefone;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }


    //MÉTODOS

}
