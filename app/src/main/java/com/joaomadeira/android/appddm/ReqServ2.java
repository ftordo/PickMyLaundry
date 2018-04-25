package com.joaomadeira.android.appddm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
/*import android.app.Fragment;*/

import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReqServ2 extends Fragment {

    public final static int THIS_APP_REQUEST_CODE = 123;

    private AmUtil mUtil;
    View.OnClickListener mClickHandler;

    Button mbtnSegunte2;

    ListView mLvLavandarias;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    public static String lavandariaSelecionada;
    String nrPecas, tipoServico;
    int urgencia;

    Activity main;

    View v;

    public ReqServ2(){

    }

    @SuppressLint("ValidFragment")
    public ReqServ2(Activity pA, String nrPecas, String tipoServico, int urgencia){
        this.main = pA;
        this.nrPecas = nrPecas;
        this.tipoServico = tipoServico;
        this.urgencia = urgencia;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.requisitar_servicos2, container, false);

        init();
        return v;
    }//onCreateView

    public void init(){
        mUtil = new AmUtil(main, THIS_APP_REQUEST_CODE);

        mLvLavandarias= (ListView)v.findViewById(R.id.idLvLavandarias);
        arrayList = new ArrayList<String>();


        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arrayList);

        mLvLavandarias.setAdapter(adapter);
        gerarLavandarias();

        mLvLavandarias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adpterView, View view, int position,
                                    long id) {
                for (int i = 0; i < mLvLavandarias.getChildCount(); i++) {
                    if(position == i ){
                        mLvLavandarias.getChildAt(i).setBackgroundColor(Color.GRAY);
                        lavandariaSelecionada = mLvLavandarias.getItemAtPosition(position).toString();
                        Log.d(lavandariaSelecionada,"Lavandaria");
                    }else{
                        mLvLavandarias.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            }
        });

        mbtnSegunte2= (Button)v.findViewById(R.id.idBtnSegunte2);
        mClickHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idQualOjectoInteragido = v.getId();
                if(idQualOjectoInteragido==R.id.idBtnSegunte2){
                    if(lavandariaSelecionada != null){
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                        transaction.replace(R.id.mainLayout, new ReqServ3(main, lavandariaSelecionada, nrPecas, tipoServico, urgencia));
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } else {
                        Toast.makeText(main, "Seleccione uma lavandaria", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        };
        mbtnSegunte2.setOnClickListener(mClickHandler);
    }//init

    public void gerarLavandarias(){
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
                            adapter.add(map.get("nome").toString());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }//gerarLavandarias

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
