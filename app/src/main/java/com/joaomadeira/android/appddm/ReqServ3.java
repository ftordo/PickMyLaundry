package com.joaomadeira.android.appddm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
/*import android.app.Fragment;*/

import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReqServ3 extends Fragment {

    public final static int THIS_APP_REQUEST_CODE = 123;

    Button mBtnPontoEntrega;
    Button mBtnPontoRecolha;
    TextView mTvPontoEntrega;
    TextView mTvPontoRecolha;
    DatePicker mDateDia;
    TimePicker mTimeHoras;

    static boolean primeiroRegisto = true;

    private AmUtil mUtil;
    View.OnClickListener mClickHandler;

    Button mbtnSegunte3;
    public TextView mTvLavSelecionada;
    public String mLavandariaSelecionada;
    String mNrPecas, mTipoServico;
    int mUrgencia;
    Activity main;


    View v;

    private static double mLongitude;
    private static double mLatitude;

    private static TextView mTvLavSelecionadaG;
    private static DatePicker mDateDiaG;
    private static TimePicker mTimeHorasG;
    private static TextView mTvPontoEntregaG;
    private static TextView mTvPontoRecolhaG;
    private static String mQual;
    private static String mNrPecasG, mTipoServicoG;
    private static int mUrgenciaG;

    public ReqServ3(){

    }

    @SuppressLint("ValidFragment")
    public ReqServ3(Activity pA, String pLavandaria, String pNrPecas, String pTipoServico, int pUrgencia) {
        this.main = pA;
        this.mLavandariaSelecionada = pLavandaria;
        this.mNrPecas = pNrPecas;
        this.mTipoServico = pTipoServico;
        this.mUrgencia = pUrgencia;
        // Required empty public constructor

    }//ReqServ3

    @SuppressLint("ValidFragment")
    public ReqServ3(
            Activity pA,
            double pLatitude,
            double pLongitude,
            TextView pTvLavSelecionada,
            DatePicker pDateDia,
            TimePicker pTimeHoras,
            TextView pTvPontoEntrega,
            TextView pTvPontoRecolha,
            String pQual,
            String pNrPecas,
            String pTipoServico,
            int pUrgencia

    ){
        this.main = pA;
        this.mLatitude = pLatitude;
        this.mLongitude = pLongitude;
        this.mTvLavSelecionadaG = pTvLavSelecionada;
        this.mDateDiaG = pDateDia;
        this.mTimeHorasG = pTimeHoras;
        this.mTvPontoEntregaG = pTvPontoEntrega;
        this.mTvPontoRecolhaG = pTvPontoRecolha;
        this.mQual = pQual;
        this.primeiroRegisto = false;
        this.mNrPecasG = pNrPecas;
        this.mTipoServicoG = pTipoServico;
        this.mUrgenciaG = pUrgencia;
    }//ReqServ3

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.requisitar_servicos3, container, false);

        init();

        return v;
    }//onCreateView

    public void init(){
        mUtil = new AmUtil(main, THIS_APP_REQUEST_CODE);

        mBtnPontoEntrega = (Button)v.findViewById(R.id.idBtnPontoEntrega);
        mBtnPontoRecolha = (Button)v.findViewById(R.id.idBtnPontoRecolha);
        mTvPontoEntrega = (TextView)v.findViewById(R.id.idTvPontoEntrega);
        mTvPontoRecolha = (TextView)v.findViewById(R.id.idTvPontoRecolha);
        mDateDia = (DatePicker) v.findViewById(R.id.idDateDia);
        mTimeHoras = (TimePicker) v.findViewById(R.id.idTimeHoras);

        mTvLavSelecionada= (TextView)v.findViewById(R.id.idTvLavandariaSelecionada);
        if(primeiroRegisto){
            mTvLavSelecionada.setText(mLavandariaSelecionada);
        }
        mbtnSegunte3= (Button)v.findViewById(R.id.idBtnSegunte3);

        if(!primeiroRegisto){
            mTvLavSelecionada.setText(mTvLavSelecionadaG.getText());

            mDateDia.updateDate(mDateDiaG.getYear(), mDateDiaG.getMonth(), mDateDiaG.getDayOfMonth());

            mTimeHoras.setCurrentHour(mTimeHorasG.getCurrentHour());
            mTimeHoras.setCurrentMinute(mTimeHorasG.getCurrentMinute());

            if(mQual == "Recolha"){
                mTvPontoRecolha.setText(mLatitude + "," + mLongitude);
            } else {
                mTvPontoRecolha.setText(mTvPontoRecolhaG.getText());
            }
            if(mQual == "Entrega"){
                mTvPontoEntrega.setText(mLatitude + "," + mLongitude);
            } else {
                mTvPontoEntrega.setText(mTvPontoEntregaG.getText());
            }
            mNrPecas = mNrPecasG;
            mTipoServico = mTipoServicoG;
            mUrgencia = mUrgenciaG;
        }
        mClickHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idQualOjectoInteragido = v.getId();
                if(idQualOjectoInteragido==R.id.idBtnSegunte3){
                    if(mTvPontoRecolha.getText().toString().length() > 0 && mTvPontoEntrega.getText().toString().length() > 0){
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                        String data = mDateDia.getYear()+"-"+(mDateDia.getMonth()+1)+"-"+mDateDia.getDayOfMonth();
                        String hora = mTimeHoras.getCurrentHour()+":"+mTimeHoras.getCurrentMinute();

                        transaction.replace(R.id.mainLayout, new ReqServ4(main, mTvLavSelecionada.getText().toString(), data, hora,
                                mTvPontoEntrega.getText().toString(), mTvPontoRecolha.getText().toString(), mNrPecasG, mTipoServicoG, mUrgenciaG));
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } else {
                        Toast.makeText(main, "Dados por preencher", Toast.LENGTH_SHORT).show();
                    }
                } else if (idQualOjectoInteragido==R.id.idBtnPontoEntrega) {
                    Intent intent = MapLocalizacao.dados1(main, mTvLavSelecionada, mDateDia, mTimeHoras, mTvPontoEntrega, mTvPontoRecolha, "Entrega",
                            mNrPecas, mTipoServico, mUrgencia);
                    startActivity(intent);
                } else if (idQualOjectoInteragido==R.id.idBtnPontoRecolha) {
                    Intent intent = MapLocalizacao.dados1(main, mTvLavSelecionada, mDateDia, mTimeHoras, mTvPontoEntrega, mTvPontoRecolha, "Recolha",
                            mNrPecas, mTipoServico, mUrgencia);
                    startActivity(intent);
                }


            }
        };

        mBtnPontoEntrega.setOnClickListener(mClickHandler);
        mBtnPontoRecolha.setOnClickListener(mClickHandler);
        mbtnSegunte3.setOnClickListener(mClickHandler);
    }//init

    public class Globalcoiso{
        public String lavandariaSelecionada = mTvLavSelecionada.getText().toString();
    }


}