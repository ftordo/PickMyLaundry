package com.joaomadeira.android.appddm;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.Fragment;
/*import android.app.Fragment;*/
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapFragment;

import android.support.annotation.NonNull;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainCliente extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static int THIS_APP_REQUEST_CODE = 123;

    AmUtil mUtil;

    FragmentManager mFragmentManager;

    View.OnClickListener mClickHandler;

    static boolean bPedido = false;
    static String idPedido;

    private static double mLongitude;
    private static double mLatitude;
    private static TextView mTvLavSelecionada;
    private static DatePicker mDateDia;
    private static TimePicker mTimeHoras;
    private static TextView mTvPontoEntrega;
    private static TextView mTvPontoRecolha;
    private static String mQual;
    private static boolean res3;
    private static String mNrPecas;
    private static String mTipoServico;
    private static int mUrgencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cliente);

        init();
    }//onCreate

    public void init(){
        mUtil = new AmUtil(MainCliente.this);

        menu();

    }//init

    public void menu(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (res3){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.mainLayout, new ReqServ3(MainCliente.this, mLatitude, mLongitude, mTvLavSelecionada, mDateDia, mTimeHoras, mTvPontoEntrega, mTvPontoRecolha, mQual,
                    mNrPecas,mTipoServico,mUrgencia));
            transaction.commit();
            res3 = false;
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.mainLayout, new MainMap(MainCliente.this, bPedido, idPedido));
            transaction.commit();

            navigationView.setCheckedItem(R.id.nav_inicio);
        }
    }//menu

    //----------------- MENU ---------------------
    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }
        else {
            getFragmentManager().popBackStack();
        }
    }//onBackPressed

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }//onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;

        if (id == R.id.nav_inicio) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainLayout, new MainMap(MainCliente.this));
            ft.commit();
        } else if (id == R.id.nav_pedido) {
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.mainLayout, new ReqServ1(this));
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_Logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent= new Intent(MainCliente.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }//onNavigationItemSelected

    //----------------- END MENU ---------------------

    public static Intent backInicio(
            Activity pChamador,
            boolean pPedido,
            String pIdPedido
    ){
        Intent ret = new Intent(
                pChamador,
                MainCliente.class
        );
        bPedido = pPedido;
        if(pIdPedido != "")
            idPedido = pIdPedido;
        return ret;
    }//backInicio

    public static Intent backMorada(
            Activity pChamador,
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
        Intent ret = new Intent(
                pChamador,
                MainCliente.class
        );
        mLatitude = pLatitude;
        mLongitude = pLongitude;
        mTvLavSelecionada = pTvLavSelecionada;
        mDateDia = pDateDia;
        mTimeHoras = pTimeHoras;
        mTvPontoEntrega = pTvPontoEntrega;
        mTvPontoRecolha = pTvPontoRecolha;
        mQual = pQual;
        res3 = true;
        mNrPecas = pNrPecas;
        mTipoServico = pTipoServico;
        mUrgencia = pUrgencia;

        return ret;
    }//backMorada


    @Override
    protected void onStop() {
        super.onStop();
    }

}

