package com.joaomadeira.android.appddm;

/**
 * Created by Fábio on 04/01/2018.
 */

public class Cliente {

    //ATRIBUTOS
    String nome;
    String apelido;
    int idade;
    Localizacao morada;
    int telefone;
    String email;


    //CONSTRUTORES

    public Cliente(){

    }

    public Cliente(String nome, String apelido, String email, int idade, Localizacao morada, int telefone) {

        this.nome = nome;
        this.apelido = apelido;
        this.email = email;
        this.idade = idade;
        this.morada = morada;
        this.telefone = telefone;
    }

    //ACESSORES

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public Localizacao getMorada() {
        return morada;
    }

    public void setMorada(Localizacao morada) {
        this.morada = morada;
    }

    public int getTelefone() {
        return telefone;
    }

    public void setTelefone(int telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //MÉTODOS

}
