package com.joaomadeira.android.appddm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
/*import android.app.Fragment;*/

import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import android.app.FragmentManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FormPay extends Fragment {

    public final static int THIS_APP_REQUEST_CODE = 123;

    private AmUtil mUtil;
    View.OnClickListener mClickHandler;

    LinearLayout mllVisa, mllMaster, mllpaypal, mllBitcoin;

    ImageView mIbVisa, mIbMaster, mIbPaypal, mIbBitcoin;
    TextView mTvVisa, mTvMaster, mTvPaypal, mTvBit;

    Activity main;

    View v;

    String mLavSelecionada;
    String mDateDia;
    String mTimeHoras;
    String mPontoEntrega;
    String mPontoRecolha;
    String mNrPecas;
    String mTipoServico;
    String mUrgencia;
    String mPrecoTotal;

    public FormPay(){

    }

    @SuppressLint("ValidFragment")
    public FormPay(Activity pA, String pLavSelecionada, String pDateDia, String pTimeHoras, String pPontoEntrega,
                   String pPontoRecolha, String pNrPecas, String pTipoServico, String pUrgencia, String pPrecoTotal) {
        this.main = pA;
        this.mLavSelecionada = pLavSelecionada;
        this.mDateDia = pDateDia;
        this.mTimeHoras = pTimeHoras;
        this.mPontoEntrega = pPontoEntrega;
        this.mPontoRecolha = pPontoRecolha;
        this.mNrPecas = pNrPecas;
        this.mTipoServico = pTipoServico;
        this.mUrgencia = pUrgencia;
        this.mPrecoTotal = pPrecoTotal;
        // Required empty public constructor
    }//FormPay

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.pay_escolher, container, false);

        init();

        return v;
    }//onCreateView

    public void init(){
        mUtil = new AmUtil(main, THIS_APP_REQUEST_CODE);

        mllVisa = (LinearLayout)v.findViewById(R.id.idllVisa);
        mllMaster = (LinearLayout)v.findViewById(R.id.idllMaster);
        mllpaypal = (LinearLayout)v.findViewById(R.id.idllpaypal);
        mllBitcoin = (LinearLayout)v.findViewById(R.id.idllBitcoin);

        mIbVisa = (ImageView) v.findViewById(R.id.idIvVisa);
        mIbMaster = (ImageView)v.findViewById(R.id.idIvMaster);
        mIbPaypal = (ImageView)v.findViewById(R.id.idIvPaypal);
        mIbBitcoin = (ImageView)v.findViewById(R.id.idIvBit);

        mTvVisa = (TextView) v.findViewById(R.id.idTvVisa);
        mTvMaster = (TextView) v.findViewById(R.id.idTvMaster);
        mTvPaypal = (TextView) v.findViewById(R.id.idTvPaypal);
        mTvBit = (TextView) v.findViewById(R.id.idTvBit);

        mClickHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idQualOjectoInteragido = v.getId();
                if(idQualOjectoInteragido==R.id.idllVisa){
                    pagar(mIbVisa,mTvVisa.getText().toString());
                } else if (idQualOjectoInteragido==R.id.idllMaster){
                    pagar(mIbMaster,mTvMaster.getText().toString());
                } else if (idQualOjectoInteragido==R.id.idllpaypal){
                    pagar(mIbPaypal,mTvPaypal.getText().toString());
                } else if (idQualOjectoInteragido==R.id.idllBitcoin){
                    pagar(mIbBitcoin,mTvBit.getText().toString());
                }
            }
        };

        mllVisa.setOnClickListener(mClickHandler);
        mllMaster.setOnClickListener(mClickHandler);
        mllpaypal.setOnClickListener(mClickHandler);
        mllBitcoin.setOnClickListener(mClickHandler);
    }//init

    public void pagar(ImageView pImage, String pMetodo){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.mainLayout, new Pay(main, pImage, pMetodo, mLavSelecionada, mDateDia, mTimeHoras, mPontoEntrega,
                mPontoRecolha, mNrPecas, mTipoServico, mUrgencia, mPrecoTotal));
        transaction.addToBackStack(null);
        transaction.commit();
    }//pagar
}
