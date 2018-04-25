package com.joaomadeira.android.appddm;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.joaomadeira.android.appddm.models.PlaceInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by User on 10/2/2017.
 */

public class MapLocalizacao extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }
    }//onMapReady

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));


    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;
    private AmUtil mUtil;
    //button
    Button mBtnEscolher;

    View.OnClickListener mClickHandler;

    LatLng localSelect;

    MarkerOptions options;

    private static EditText mEtNome;
    private static EditText mEtApelido;
    private static EditText mEtEmail;
    private static EditText mEtTelefone;
    private static EditText mEtPass;
    private static EditText mEtPassConf;
    private static EditText mEtIdade;
    private static String mQual;

    private static TextView mTvLavSelecionada;
    private static DatePicker mDateDia;
    private static TimePicker mTimeHoras;
    private static TextView mTvPontoEntrega;
    private static TextView mTvPontoRecolha;
    private static String mNrPecas;
    private static String mTipoServico;
    private static int mUrgencia;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_localizacao);
        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);

        getLocationPermission();

    }//onCreate

    private void init(){
        mUtil = new AmUtil(MapLocalizacao.this);

        mBtnEscolher = (Button) findViewById(R.id.idBtnEscolher);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            //@Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });

        mClickHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idQualOjectoInteragido = v.getId();
                if(idQualOjectoInteragido==R.id.idBtnEscolher){
                    try {
                        if(mQual == "Registo"){
                            Intent chamadorDeJogar = RegistoActivity.backMorada(MapLocalizacao.this , localSelect.latitude, localSelect.longitude,
                                    mEtNome, mEtApelido, mEtEmail, mEtTelefone, mEtPass, mEtPassConf, mEtIdade);
                            startActivity(chamadorDeJogar);
                        } else if(mQual == "Recolha" || mQual == "Entrega"){
                            Intent intent = MainCliente.backMorada(MapLocalizacao.this, localSelect.latitude, localSelect.longitude, mTvLavSelecionada, mDateDia, mTimeHoras, mTvPontoEntrega, mTvPontoRecolha, mQual,
                                    mNrPecas,mTipoServico,mUrgencia);
                            startActivity(intent);
                        }

                    } catch (Exception e){
                        mUtil.utilFeedback(e.toString());
                    }
                }
            }
        };
        mBtnEscolher.setOnClickListener(mClickHandler);

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();


            }
        });
        hideSoftKeyboard();
    }//init

    private void geoLocate(){
        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapLocalizacao.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
                    address.getAddressLine(0));
        }
    }//geoLocate

    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "Minha localização");
                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }//getDeviceLocation

    private void moveCamera(LatLng latLng, float zoom, String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(latLng != localSelect){
            mMap.clear();
            options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

        localSelect = latLng;

        hideSoftKeyboard();
    }//moveCamera

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapLocalizacao.this);
    }//initMap

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }//getLocationPermission

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }//onRequestPermissionsResult

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }//hideSoftKeyboard

    // Google places API autocomplete suggestions

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();

                return;
            }
            final Place place = places.get(0);


            try  {
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                Log.d(TAG, "onResult: name: " + place.getName());
                mPlace.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: address: " + place.getAddress());
               //mPlace.setAttributions(place.getAttributions().toString());
                mPlace.setId(place.getId());
                Log.d(TAG, "onResult: id: " + place.getId());
                mPlace.setLatLng(place.getLatLng());
                Log.d(TAG, "onResult: lating: " + place.getLatLng());
                mPlace.setRating(place.getRating());
                Log.d(TAG, "onResult: rating: " + place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                Log.d(TAG, "onResult: phone number: " + place.getPhoneNumber());
                mPlace.setWebsiteUri(place.getWebsiteUri());
                Log.d(TAG, "onResult: website uri: " + place.getWebsiteUri());

                Log.d(TAG, "onResult: place: " + mPlace.toString());

            } catch (NullPointerException e) {
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace.getName());

            places.release();
        }
    };

    public static Intent dados(
            Activity pChamador,
            EditText pEtNome,
            EditText pEtApelido,
            EditText pEtEmail,
            EditText pEtTelefone,
            EditText pEtPass,
            EditText pEtPassConf,
            EditText pEtIdade,
            String pQual
    ){
        Intent ret = new Intent(
                pChamador,
                MapLocalizacao.class
        );
        mEtNome = pEtNome;
        mEtApelido = pEtApelido;
        mEtEmail = pEtEmail;
        mEtTelefone = pEtTelefone;
        mEtPass = pEtPass;
        mEtPassConf = pEtPassConf;
        mEtIdade = pEtIdade;
        mQual = pQual;
        return ret;
    }
    public static Intent dados1(
            Activity pChamador,
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
                MapLocalizacao.class
        );
        mTvLavSelecionada = pTvLavSelecionada;
        mDateDia = pDateDia;
        mTimeHoras = pTimeHoras;
        mTvPontoEntrega = pTvPontoEntrega;
        mTvPontoRecolha = pTvPontoRecolha;
        mQual = pQual;
        mNrPecas = pNrPecas;
        mTipoServico = pTipoServico;
        mUrgencia = pUrgencia;

        return ret;
    }
}











