package com.joaomadeira.android.appddm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class LavandariaRoupaRecebida extends Fragment {


    ListView mLvRecebido;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    Activity main;
    View v;
    public LavandariaRoupaRecebida(){

    }
    @SuppressLint("ValidFragment")
    public LavandariaRoupaRecebida(Activity pA) {
        this.main = pA;
    }//LavandariaRoupaRecebida

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.lavandaria_recebidos, container, false);

        init();

        return v;
    }//onCreateView


    public void init(){
        mLvRecebido= (ListView)v.findViewById(R.id.idLvRecebidos);
        arrayList = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arrayList);

        mLvRecebido.setAdapter(adapter);
        arrayList.add("Serviço Id:1253");
        arrayList.add("Serviço Id:1233");
        arrayList.add("Serviço Id:1785");
        arrayList.add("Serviço Id:1621");
        arrayList.add("Serviço Id:6952");
    }//init
}
