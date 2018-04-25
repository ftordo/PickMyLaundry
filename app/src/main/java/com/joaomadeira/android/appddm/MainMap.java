package com.joaomadeira.android.appddm;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
/*import android.app.Fragment;*/
import android.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import android.widget.Button;
import android.widget.TextView;


import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainMap extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    //private String user;

    public final static int THIS_APP_REQUEST_CODE = 123;

    AmUtil mUtil;
    Activity main;

    View v;

    View.OnClickListener mClickHandler;
    Button mbtnPedido;

    TextView mTvUser;

    boolean bPedidoSim = false;
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private LatLng pickupLocation;

    String mLatPedido;
    String mLogPedido;
    String idPedido;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    static public final int REQUEST_LOCATION = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    TextView mTvInfo;

    public MainMap(){

    }

    @SuppressLint("ValidFragment")
    public MainMap(Activity pA){
        this.main = pA;
    }

    @SuppressLint("ValidFragment")
    public MainMap(Activity pA, Boolean bPedido, String pIdPedido){
        this.main = pA;
        this.bPedidoSim = bPedido;
        this.idPedido = pIdPedido;
    }//MainMap

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_maps, container, false);

        init();
        return v;

    }//onCreateView

    public void init(){
        mUtil = new AmUtil(main, THIS_APP_REQUEST_CODE);

        mAuth = FirebaseAuth.getInstance();

        mTvUser = (TextView) v.findViewById(R.id.idTvUser);
        mTvInfo = (TextView) v.findViewById(R.id.idTvInfo);

        mTvInfo.setVisibility(View.INVISIBLE);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.idGmapFragment);
        mapFragment.getMapAsync(this);


        mbtnPedido= (Button)v.findViewById(R.id.idBtnPedido);

        mClickHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idQualOjectoInteragido = v.getId();

                if(idQualOjectoInteragido==R.id.idBtnPedido){
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                    transaction.replace(R.id.mainLayout, new ReqServ1(main));
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        };

        mbtnPedido.setOnClickListener(mClickHandler);
    }//init

    public void pedido(){
        mTvInfo.setVisibility(View.VISIBLE);

        pickupLocation = new LatLng(Double.parseDouble(mLatPedido), Double.parseDouble(mLogPedido));
        mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Recolha aqui"));

        mTvInfo.setText("A procurar um condutor....");

        getClosestDriver();
        bPedidoSim = false;
    }//pedido

    private int radius = 1;
    private Boolean driverFound = false;
    private String driverFoundID;

    private void getClosestDriver(){
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("condutorDisponivel");

        GeoFire geoFire = new GeoFire(driverLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound){
                    driverFound = true;
                    driverFoundID = key;

                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Utilizadores").child("Condutor").child(driverFoundID);
                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    HashMap map = new HashMap();
                    map.put("customerRideId", customerId);
                    driverRef.updateChildren(map);

                    getDriverLocation();
                    mTvInfo.setText("A procurar localização do condutor....");

                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound)
                {
                    radius++;
                    getClosestDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }//getClosestDriver

    private Marker mDriverMarker;
    private void getDriverLocation(){

        DatabaseReference driverLocationRef = FirebaseDatabase.getInstance().getReference().child("condutorEmServiço").child(driverFoundID).child("l");
        driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if(map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat,locationLng);
                    if(mDriverMarker != null){
                        mDriverMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance = loc1.distanceTo(loc2)/1000;

                    if (distance<100){
                        mTvInfo.setText("Condutor chegou!");
                    }else{
                        mTvInfo.setText("Condutor encontrado: " + String.valueOf(distance)+"km");
                    }



                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("O seu condutor"));
                }

            }//onDataChange

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }//getDriverLocation

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());
        mMap = googleMap;

        ActivityCompat.requestPermissions(main,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION);

        ActivityCompat.requestPermissions(main,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_REQUEST_COARSE_LOCATION);

        if (ActivityCompat.checkSelfPermission(main, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(main, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);

    }//onMapReady

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(main)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }//buildGoogleApiClient



    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        if(bPedidoSim){
            pedido();
        }
    }//onLocationChanged


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        ActivityCompat.requestPermissions(main,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION);

        ActivityCompat.requestPermissions(main,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSION_REQUEST_COARSE_LOCATION);

        if (ActivityCompat.checkSelfPermission(main, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(main, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }//onConnected

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

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

                mTvUser.setText(snapshot.child("Cliente").child(idUser).child("email").getValue(String.class));
            }//onDataChange

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(bPedidoSim) {
            DatabaseReference root1 = FirebaseDatabase.getInstance().getReference();
            DatabaseReference user2 = root1.child("Pedidos").child(idPedido);

            user2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String key = ds.getKey();
                            if(key.equalsIgnoreCase("pontoRecolha")){
                                Map<String, Object> map = (Map<String, Object>) ds.getValue();
                                if (map.get("latitude") != null) {
                                    mLatPedido = map.get("latitude").toString();
                                }
                                if (map.get("longitude") != null) {
                                    mLogPedido = map.get("longitude").toString();
                                }
                            }

                        }
                    }
                }//onDataChange

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }
    }//onStart


}