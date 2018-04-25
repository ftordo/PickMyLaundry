package com.joaomadeira.android.appddm;

import java.lang.reflect.Array;

/**
 * Created by Fábio on 04/01/2018.
 */

public class Pedidos {

    //ATRIBUTOS

    String cliente;
    String lavandaria;
    String preco;
    Localizacao pontoRecolha;
    Localizacao pontoEntrega;
    String nrPecas;
    String data;
    String hora;
    //String[] estados = {"pendente","transOrigem","lavandaria","transDestino","entregue"};
    String estadoAtual;
    String tipoServico;
    String urgencia;

    //CONSTRUTORES

    public Pedidos(){

    }

    public Pedidos(String pCliente, String pLavandaria, String preco, Localizacao pontoRecolha, Localizacao pontoEntrega, String nrPecas, String tipoServico,
        String pData, String pHora, String pUrgencia) {
        this.cliente = pCliente;
        this.lavandaria = pLavandaria;
        this.preco = preco;
        this.pontoRecolha = pontoRecolha;
        this.pontoEntrega = pontoEntrega;
        this.nrPecas = nrPecas;
        this.estadoAtual = "pendente";
        this.tipoServico = tipoServico;
        this.data = pData;
        this.hora = pHora;
        this.urgencia = pUrgencia;
    }


    //ACESSORES


    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getLavandaria() {
        return lavandaria;
    }

    public void setLavandaria(String lavandaria) {
        this.lavandaria = lavandaria;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public Localizacao getPontoRecolha() {
        return pontoRecolha;
    }

    public void setPontoRecolha(Localizacao pontoRecolha) {
        this.pontoRecolha = pontoRecolha;
    }

    public Localizacao getPontoEntrega() {
        return pontoEntrega;
    }

    public void setPontoEntrega(Localizacao pontoEntrega) {
        this.pontoEntrega = pontoEntrega;
    }

    public String getNrPecas() {
        return nrPecas;
    }

    public void setNrPecas(String nrPecas) {
        this.nrPecas = nrPecas;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }


    public String getEstadoAtual() {
        return estadoAtual;
    }

    public void setEstadoAtual(String estadoAtual) {
        this.estadoAtual = estadoAtual;
    }

    public String getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(String tipoServico) {
        this.tipoServico = tipoServico;
    }

    public String getUrgencia() {
        return urgencia;
    }

    public void setUrgencia(String urgencia) {
        this.urgencia = urgencia;
    }


    //MÉTODOS


}
