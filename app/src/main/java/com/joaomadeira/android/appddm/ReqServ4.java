package com.joaomadeira.android.appddm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
/*import android.app.Fragment;*/

import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReqServ4 extends Fragment {

    public final static int THIS_APP_REQUEST_CODE = 123;

    private AmUtil mUtil;
    View.OnClickListener mClickHandler;

    Button mBtnConfirmar, mBtnCancelar;
    TextView mTvPrecoLavagem, mTvPrecoEngomar, mTvPrecoTransporte, mTvPrecoTotal;

    Activity main;

    View v;

    double precoPorPecaLava = 0.35;
    double precoPorPecaEngoma = 0.40;
    double precoPorQuilometro = 0.25;
    double precoTotalLavagem = 0;
    double precoTotalEngoma = 0;
    double precoTotalTransporte = 0;
    double precoTotal;

    double latitudeLavandaria;
    double longitudeLavandaria;

    String mLavSelecionada;
    String mDateDia;
    String mTimeHoras;
    String mPontoEntrega;
    String mPontoRecolha;
    String mNrPecas;
    String mTipoServico;
    int mUrgencia;
    int mUrgenciaPreco;

    public ReqServ4(){

    }

    @SuppressLint("ValidFragment")
    public ReqServ4(Activity pA, String pLavSelecionada, String pDateDia, String pTimeHoras,
        String pPontoEntrega, String pPontoRecolha, String pNrPecas, String pTipoServico, int pUrgencia) {
        this.main = pA;
        this.mLavSelecionada = pLavSelecionada;
        this.mDateDia = pDateDia;
        this.mTimeHoras = pTimeHoras;
        this.mPontoEntrega = pPontoEntrega;
        this.mPontoRecolha = pPontoRecolha;
        this.mNrPecas = pNrPecas;
        this.mTipoServico = pTipoServico;
        this.mUrgencia = pUrgencia;
        // Required empty public constructor
    }//ReqServ4

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.requisitar_servicos4, container, false);

        init();

        return v;
    }

    public void init(){
        mUtil = new AmUtil(main, THIS_APP_REQUEST_CODE);

        if(mUrgencia == 1){
            mUrgenciaPreco = 2;
        } else {
            mUrgenciaPreco = 1;
        }

        mTvPrecoLavagem = (TextView)v.findViewById(R.id.idTvPrecoLavagem);
        mTvPrecoEngomar = (TextView)v.findViewById(R.id.idTvPrecoEngomar);
        mTvPrecoTransporte = (TextView)v.findViewById(R.id.idTvPrecoTransporte);
        mTvPrecoTotal = (TextView)v.findViewById(R.id.idTvPrecoTotal);

        calcularPrecos();
        calcularDistancias();

        precoTotal = precoTotalLavagem + precoTotalEngoma + precoTotalTransporte;

        mTvPrecoTotal.setText(String.valueOf(precoTotal)+"€");

        mBtnConfirmar= (Button)v.findViewById(R.id.idBtnConfirmar);
        mBtnCancelar = (Button)v.findViewById(R.id.idBtnCancelar);
        mClickHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idQualOjectoInteragido = v.getId();
                if(idQualOjectoInteragido==R.id.idBtnConfirmar){
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.mainLayout, new FormPay(main, mLavSelecionada, mDateDia, mTimeHoras, mPontoEntrega,
                            mPontoRecolha, mNrPecas, mTipoServico, String.valueOf(mUrgencia), String.valueOf(precoTotal)));
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                if(idQualOjectoInteragido==R.id.idBtnCancelar){
                    Intent chamadorDeJogar = MainCliente.backInicio(main, false, "");
                    startActivity(chamadorDeJogar);
                }
            }
        };
        mBtnConfirmar.setOnClickListener(mClickHandler);
        mBtnCancelar.setOnClickListener(mClickHandler);
    }//init

    public void calcularPrecos(){
        if(mTipoServico == "lavar"){
            calcularLavar();
        } else if (mTipoServico == "engomar"){
            calcularEngoma();
        } else if (mTipoServico == "lavar/engomar"){
            calcularLavar();
            calcularEngoma();
        }
    }//calcularPrecos

    public double calcularPrecoServico(double pPrecoPor){
        double total = 0;
        total = Math.round(Integer.parseInt(mNrPecas) * pPrecoPor) * mUrgenciaPreco;
        return total;
    }//calcularPrecoServico

    public void calcularLavar(){
        precoTotalLavagem = calcularPrecoServico(precoPorPecaLava);
        mTvPrecoLavagem.setText(String.valueOf(precoTotalLavagem)+"€");
    }//calcularLavar

    public void calcularEngoma(){
        precoTotalEngoma = calcularPrecoServico(precoPorPecaEngoma);
        mTvPrecoEngomar.setText(String.valueOf(precoTotalEngoma)+"€");
    }//calcularEngoma

    public void calcularDistancias(){
        queryLavandaria();
        String[] coorRecolha = mPontoRecolha.split(",");
        double latitudeRecolha = Double.parseDouble(coorRecolha[0].toString());
        double longitudeRecolha = Double.parseDouble(coorRecolha[1].toString());
        String[] coorEntrega = mPontoEntrega.split(",");
        double latitudeEntrega = Double.parseDouble(coorEntrega[0].toString());
        double longitudeEntrega = Double.parseDouble(coorEntrega[1].toString());

        LatLng latLngRecolha = new LatLng(latitudeRecolha,longitudeRecolha);
        LatLng latLngEntrega = new LatLng(latitudeEntrega,longitudeEntrega);
        LatLng latLngLavandaria = new LatLng(latitudeLavandaria,longitudeLavandaria);
        Location locRecolha = new Location("Recolha");
        locRecolha.setLatitude(latLngRecolha.latitude);
        locRecolha.setLongitude(latLngRecolha.longitude);
        Location locEntrega = new Location("Entrega");
        locEntrega.setLatitude(latLngEntrega.latitude);
        locEntrega.setLongitude(latLngEntrega.longitude);
        Location locLavandaria = new Location("Lavandaria");
        locLavandaria.setLatitude(latLngLavandaria.latitude);
        locLavandaria.setLongitude(latLngLavandaria.longitude);

        double disRecLav = locRecolha.distanceTo(locLavandaria)/1000;
        double disLavEnt = locLavandaria.distanceTo(locEntrega)/1000;

        double disTotal = (disRecLav+disLavEnt)/1000;

        precoTotalTransporte = Math.round(disTotal * precoPorQuilometro) * mUrgenciaPreco;
        mTvPrecoTransporte.setText(String.valueOf(precoTotalTransporte)+"€");
    }//calcularDistancias

    public void queryLavandaria(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String idUser = user.getUid();
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference user1 = root.child("Utilizadores").child("Lavandaria");
        user1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String key = ds.getKey();
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        if(map.get("nome")!=null) {
                            if (map.get("nome").toString() == mLavSelecionada){
                                if (map.get("morada") != null){
                                    latitudeLavandaria = Double.parseDouble(ds.child("morada").child("latitude").getValue().toString());
                                    longitudeLavandaria = Double.parseDouble(ds.child("morada").child("longitude").getValue().toString());
                                }
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }//queryLavandaria

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}