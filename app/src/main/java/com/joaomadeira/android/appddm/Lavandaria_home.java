package com.joaomadeira.android.appddm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Lavandaria_home extends Fragment {
    View v;
    Activity main;

    AmUtil mUtil;

    private FirebaseAuth mAuth;

    View.OnClickListener mClickHandler;
    Button mBtnRecebido;
    Button mBtnALavar;
    Button mBtnPronto;
    TextView mTvUser;

    public Lavandaria_home() {

    }

    @SuppressLint("ValidFragment")
    public Lavandaria_home(Activity pA) {
        this.main = pA;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.main_lavandaria, container, false);

        init();
        return v;

    }//onCreateView

    public void init(){
        mUtil = new AmUtil(main);

        mTvUser = (TextView) v.findViewById(R.id.idTvUser);
        mBtnRecebido = (Button)v.findViewById(R.id.idBtnRecebidos);
        mBtnALavar = (Button)v.findViewById(R.id.idBtnALavar);
        mBtnPronto = (Button)v.findViewById(R.id.idBtnPronto);

        mClickHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idQualOjectoInteragido = v.getId();

                if(idQualOjectoInteragido==R.id.idBtnRecebidos){
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                    transaction.replace(R.id.mainLayout, new LavandariaRoupaRecebida(main));
                    transaction.addToBackStack(null);
                    transaction.commit();
                }else if(idQualOjectoInteragido==R.id.idBtnALavar){
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                    transaction.replace(R.id.mainLayout, new LavandariaRoupaALavar(main));
                    transaction.addToBackStack(null);
                    transaction.commit();
                }else{
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                    transaction.replace(R.id.mainLayout, new LavandariaRoupaPronto(main));
                    transaction.addToBackStack(null);
                    transaction.commit();
                }

            }
        };

        mBtnRecebido.setOnClickListener(mClickHandler);
        mBtnALavar.setOnClickListener(mClickHandler);
        mBtnPronto.setOnClickListener(mClickHandler);


    }//init

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String idUser = user.getUid();
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference user1 = root.child("Utilizadores");
        user1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                mTvUser.setText(snapshot.child("Lavandaria").child(idUser).child("email").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }//onStart
}
