package com.joaomadeira.android.appddm;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainLavandaria extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    AmUtil mUtil;

    View.OnClickListener mClickHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_lavandaria);

        init();
    }//onCreate

    public void init(){
        mUtil = new AmUtil(MainLavandaria.this);

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

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.mainLayout, new Lavandaria_home(MainLavandaria.this));
        transaction.commit();

        navigationView.setCheckedItem(R.id.nav_inicio);

    }//menu

    //----------------- MENU ---------------------
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
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

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

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
            ft.replace(R.id.mainLayout, new MainMapCondutor(MainLavandaria.this));
            ft.commit();
        } else if (id == R.id.nav_recebido){
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.mainLayout, new LavandariaRoupaRecebida(this));
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_aLavar) {
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.mainLayout, new LavandariaRoupaALavar(this));
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_pronto){
            FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.mainLayout, new LavandariaRoupaPronto(this));
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_Logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent= new Intent(MainLavandaria.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }//onNavigationItemSelected

    //----------------- END MENU ---------------------
}


