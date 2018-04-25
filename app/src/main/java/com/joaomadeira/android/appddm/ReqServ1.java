package com.joaomadeira.android.appddm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
/*import android.app.Fragment;*/

import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import android.app.FragmentManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReqServ1 extends Fragment {

    public final static int THIS_APP_REQUEST_CODE = 123;

    private AmUtil mUtil;
    View.OnClickListener mClickHandler;

    Button mbtnSegunte1;
    EditText mEtNrPecas;
    CheckBox mCbUrgencia;
    Spinner mSpnTipoServico;

    //variaveis
    String nrPecas;
    String tipoServicos;
    int urgencia;
    private ArrayAdapter<String> adapterTipoServico;
    Activity main;

    View v;

    public ReqServ1(){

    }

    @SuppressLint("ValidFragment")
    public ReqServ1(Activity pA) {
        this.main = pA;
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.requisitar_servicos1, container, false);

        init();

        return v;
    }//onCreateView

    public void init(){
        mUtil = new AmUtil(main, THIS_APP_REQUEST_CODE);

        mSpnTipoServico = (Spinner) v.findViewById(R.id.idSpnTipoServico);
        mEtNrPecas= (EditText)v.findViewById(R.id.idEtNrPecas);
        mCbUrgencia=(CheckBox)v.findViewById(R.id.idCbUrgencia);
        String[] arrayTipoServico = new String[] {
                "lavar", "lavar/engomar", "engomar"
        };
        adapterTipoServico = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, arrayTipoServico);
        mSpnTipoServico.setAdapter(adapterTipoServico);
        mbtnSegunte1= (Button)v.findViewById(R.id.idBtnSegunte1);
        mClickHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idQualOjectoInteragido = v.getId();
                if(idQualOjectoInteragido==R.id.idBtnSegunte1){
                    receberDados();
                    if(Integer.parseInt(nrPecas) > 0){
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.mainLayout, new ReqServ2(main,nrPecas,tipoServicos,urgencia));
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } else {
                        Toast.makeText(main, "Dados por preencher", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        };
        mbtnSegunte1.setOnClickListener(mClickHandler);
    }//init

    public void receberDados(){
        nrPecas = mEtNrPecas.getText().toString();
        //String x = "";
        if(nrPecas.length() == 0){
            nrPecas = "0";
        }
        tipoServicos = mSpnTipoServico.getSelectedItem().toString();

        if(mCbUrgencia.isChecked()){
            urgencia = 1;
        }else{
            urgencia = 0;
        }

    }//receberDados

}


































/*import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ReqServ1 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private AmUtil mUtil;
    View.OnClickListener mClickHandler;

    Button mbtnSegunte1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requisitar_servicos1);

        init();
    }

    void init (){
        mUtil = new AmUtil(ReqServ1.this);
        //x = (EditText) findViewById(R.id.idEtNome);
        //y = (EditText)findViewById(R.id.idEtPass);

        mbtnSegunte1= (Button)findViewById(R.id.idBtnSegunte1);
        mClickHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idQualOjectoInteragido = v.getId();
                if(idQualOjectoInteragido==R.id.idBtnSegunte1){
                    String x1, y1;
                    try {
                        Intent chamadorDeJogar = ReqServ2.servico1(
                                ReqServ1.this
                        );
                        startActivity(chamadorDeJogar);
                    } catch (Exception e){
                        mUtil.utilFeedback(e.toString());
                    }
                }
            }
        };
        mbtnSegunte1.setOnClickListener(mClickHandler);

        menu();
    }

    public void menu(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //----------------- MENU ---------------------
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //----------------- END MENU ---------------------

    public static Intent novoPedido(
            Activity pChamador
    ){
        Intent ret = new Intent(
                pChamador,
                ReqServ1.class
        );

        return ret;
    }//chamaMeEnviandoMeuUmMinEUmMax

}*/
