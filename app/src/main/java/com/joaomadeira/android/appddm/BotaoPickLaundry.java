package com.joaomadeira.android.appddm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
/**
 * A simple {@link Fragment} subclass.
 */
public class BotaoPickLaundry extends Fragment {

    public final static int THIS_APP_REQUEST_CODE = 123;

    private AmUtil mUtil;
    View.OnClickListener mClickHandler;

    Button mBtnPick;

    String idPedido;

    Activity main;

    View v;

    public BotaoPickLaundry(){

    }

    @SuppressLint("ValidFragment")
    public BotaoPickLaundry(Activity pA, String pIdPedido) {
        this.main = pA;
        this.idPedido = pIdPedido;
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.circular_button, container, false);

        init();

        return v;
    }

    public void init(){
        mUtil = new AmUtil(main, THIS_APP_REQUEST_CODE);

        mBtnPick = (Button)v.findViewById(R.id.idBtnPick);

        mClickHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idQualOjectoInteragido = v.getId();
                if(idQualOjectoInteragido== R.id.idBtnPick){
                    try {
                        Intent chamadorDeJogar = MainCliente.backInicio(main, true, idPedido);
                        startActivity(chamadorDeJogar);
                    } catch (Exception e){
                        mUtil.utilFeedback(e.toString());
                    }
                }
            }
        };
        mBtnPick.setOnClickListener(mClickHandler);
    }
}