package com.joaomadeira.android.appddm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class LavandariaRoupaPronto extends Fragment {


    ListView mLvPronto;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    Activity main;
    View v;

    public LavandariaRoupaPronto(){

    }
    @SuppressLint("ValidFragment")
    public LavandariaRoupaPronto(Activity pA) {
        this.main = pA;
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.lavandaria_pronto, container, false);

        init();

        return v;
    }//onCreateView


    public void init(){
        mLvPronto= (ListView)v.findViewById(R.id.idLvPronto);
        arrayList = new ArrayList<String>();


        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arrayList);

        mLvPronto.setAdapter(adapter);
        arrayList.add("Serviço Id:1253");
        arrayList.add("Serviço Id:1233");
        arrayList.add("Serviço Id:1785");
        arrayList.add("Serviço Id:1621");
        arrayList.add("Serviço Id:6952");
    }//init
}
