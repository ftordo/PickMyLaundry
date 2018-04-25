package com.joaomadeira.android.appddm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
/*import android.app.Fragment;*/

import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.app.FragmentManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Pay extends Fragment {

    public final static int THIS_APP_REQUEST_CODE = 123;

    private AmUtil mUtil;
    View.OnClickListener mClickHandler;

    Button mBtnPagar;

    ImageView mIvMetodo;
    TextView mTvMetodo;
    DatePicker mDateExpiracao;
    EditText mEtNCartao, mEtCodCVV;

    ImageView backImage;
    String nMetodo;

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
    static String nomeCliente;
    String idPedido;

    Pedidos pedido;

    public Pay(){

    }

    @SuppressLint("ValidFragment")
    public Pay(Activity pA, ImageView pBackImage, String pNMetodo, String pLavSelecionada, String pDateDia, String pTimeHoras, String pPontoEntrega,
               String pPontoRecolha, String pNrPecas, String pTipoServico, String pUrgencia, String pPrecoTotal) {
        this.main = pA;
        this.backImage = pBackImage;
        this.nMetodo = pNMetodo;
        this.mLavSelecionada = pLavSelecionada;
        this.mDateDia = pDateDia;
        this.mTimeHoras = pTimeHoras;
        this.mPontoEntrega = pPontoEntrega;
        this.mPontoRecolha = pPontoRecolha;
        this.mNrPecas = pNrPecas;
        this.mTipoServico = pTipoServico;
        this.mUrgencia = pUrgencia;
        this.mPrecoTotal = pPrecoTotal;
    }//Pay

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.pay_formulario, container, false);

        init();

        return v;
    }//onCreateView

    public void init(){
        mUtil = new AmUtil(main, THIS_APP_REQUEST_CODE);

        mBtnPagar = (Button)v.findViewById(R.id.idBtnPagar);

        mIvMetodo = (ImageView) v.findViewById(R.id.idIvMetodo);
        mIvMetodo.setImageDrawable(backImage.getBackground());
        mTvMetodo = (TextView) v.findViewById(R.id.idTvMetodo);
        mTvMetodo.setText(nMetodo);
        mDateExpiracao = (DatePicker) v.findViewById(R.id.idDateExpiracao);
        mEtNCartao = (EditText) v.findViewById(R.id.idEtNCartao);
        mEtCodCVV = (EditText) v.findViewById(R.id.idEtCodCVV);

        mClickHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idQualOjectoInteragido = v.getId();
                if(idQualOjectoInteragido==R.id.idBtnPagar){
                    try {
                        if(mEtNCartao.getText().toString().length() > 0 && mEtCodCVV.getText().toString().length() >0){

                            inserirPedido();

                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                            transaction.replace(R.id.mainLayout, new BotaoPickLaundry(main, idPedido));
                            transaction.addToBackStack(null);
                            transaction.commit();
                        } else {
                            Toast.makeText(main, "Dados por preencher", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e){
                        mUtil.utilFeedback(e.toString());
                    }
                }
            }
        };
        mBtnPagar.setOnClickListener(mClickHandler);
    }//init

    public void inserirPedido(){
        String[] coorRecolha = mPontoRecolha.split(",");
        double latitudeRecolha = Double.parseDouble(coorRecolha[0].toString());
        double longitudeRecolha = Double.parseDouble(coorRecolha[1].toString());
        String[] coorEntrega = mPontoEntrega.split(",");
        double latitudeEntrega = Double.parseDouble(coorEntrega[0].toString());
        double longitudeEntrega = Double.parseDouble(coorEntrega[1].toString());

        Localizacao moradaRecolha = new Localizacao(latitudeRecolha,longitudeRecolha);
        Localizacao moradaEntrega = new Localizacao(latitudeEntrega,longitudeEntrega);

        verCliente();

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        pedido = new Pedidos(user_id, mLavSelecionada, mPrecoTotal, moradaRecolha, moradaEntrega, mNrPecas, mTipoServico,
                mDateDia, mTimeHoras, mUrgencia);

        idPedido = user_id+"_"+mDateDia+"_"+mTimeHoras;
        DatabaseReference current_pedido = FirebaseDatabase.getInstance().getReference().child("Pedidos").child(idPedido);
        current_pedido.setValue(pedido);
    }//inserirPedido

    public void verCliente(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String idUser = user.getUid();
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference user1 = root.child("Utilizadores");
        user1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                nomeCliente = snapshot.child("Cliente").child(idUser).child("nome").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }//verCliente
}