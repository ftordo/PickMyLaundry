package com.joaomadeira.android.appddm;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*import android.app.Fragment;*/

/**
 * A simple {@link Fragment} subclass.
 */
public class MainMapCondutor extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener,RoutingListener {

    public final static int THIS_APP_REQUEST_CODE = 123;

    AmUtil mUtil;

    Activity main;

    View v;

    View.OnClickListener mClickHandler;
    Button mbtnPedido;

    TextView mTvUser, mTvInfo;
    private String customerId = "";

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    //private boolean logout = false;

    static public final int REQUEST_LOCATION = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    public MainMapCondutor(){

    }

    @SuppressLint("ValidFragment")
    public MainMapCondutor(Activity pA) {
        this.main = pA;
        // Required empty public constructor
    }//MainMapCondutor


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_maps, container, false);

        init();
        return v;
    }//onCreateView

    public void init(){
        mUtil = new AmUtil(main, THIS_APP_REQUEST_CODE);

        mTvUser = (TextView) v.findViewById(R.id.idTvUser);

        mTvInfo = (TextView) v.findViewById(R.id.idTvInfo);

        mTvInfo.setVisibility(View.INVISIBLE);

        polylines = new ArrayList<>();
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.idGmapFragment);
        mapFragment.getMapAsync(this);


        mbtnPedido= (Button)v.findViewById(R.id.idBtnPedido);
        mbtnPedido.setVisibility(View.GONE);

        getAssignedCustomer();
    }//init


//------------------FIREBASE E MAPS-------------------//


    private void getAssignedCustomer(){
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Utilizadores").child("Condutor").child(driverId).child("customerRideId");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    customerId = dataSnapshot.getValue().toString();
                    getAssignedCustomerPickupLocation();
                }else{
                    erasePolylines();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }//getAssignedCustomer

    private void getAssignedCustomerPickupLocation(){
        DatabaseReference assignedCustomerPickupLocationRef = FirebaseDatabase.getInstance().getReference().child("Pedidos");
        assignedCustomerPickupLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        for (DataSnapshot ds1 : ds.getChildren()) {
                            if((ds.child("cliente").getValue().toString()).equalsIgnoreCase(customerId)){
                                if(ds1.getKey().equalsIgnoreCase("pontoRecolha")){
                                    Map<String, Object> map = (Map<String, Object>) ds1.getValue();
                                    double locationLat = 0;
                                    double locationLng = 0;
                                    if (map.get("latitude") != null) {
                                        locationLat = Double.parseDouble(map.get("latitude").toString());
                                    }
                                    if (map.get("longitude") != null) {
                                        locationLng = Double.parseDouble(map.get("longitude").toString());
                                    }
                                    LatLng pickupLatLng = new LatLng(locationLat, locationLng);
                                    mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Local Recolha"));
                                    getRouteToMarker(pickupLatLng);
                                }
                            }
                        }
                    }
                }
            }//onDataChange

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }//getAssignedCustomerPickupLocation

    private void getRouteToMarker(LatLng pickupLatLng) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), pickupLatLng)
                .build();
        routing.execute();
    }//getRouteToMarker

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
        if(getActivity().getApplicationContext()!=null){
            mLastLocation = location;

            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("condutorDisponivel");
            DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("condutorEmServiço");
            GeoFire geoFireAvailable = new GeoFire(refAvailable);
            GeoFire geoFireWorking = new GeoFire(refWorking);

            switch (customerId){
                case "":
                    geoFireWorking.removeLocation(userId);
                    geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    break;

                default:
                    geoFireAvailable.removeLocation(userId);
                    geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                    break;
            }
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
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String idUser = user.getUid();
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference user1 = root.child("Utilizadores");
        user1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                mTvUser.setText(snapshot.child("Condutor").child(idUser).child("email").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }//onStart

    @Override
    public void onStop() {
        super.onStop();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }//onStop

    //---------------------FIM FIREBASE E MAPS------------------//

    //As polylines são as linhas que marcam o caminho
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

//-----------------POLYLINES-----------------------//

    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            mUtil.utilFeedback("Erro: " + e.getMessage());
            //Toast.makeText(main, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            mUtil.utilFeedback("Algo correu mal, tente outra vez");
            //Toast.makeText(main, "Algo correu mal, tente outra vez", Toast.LENGTH_SHORT).show();
        }
    }//onRoutingFailure

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortesRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }//onRoutingSuccess

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            mUtil.utilFeedback("Rota "+ (i+1) +": distância - "+ route.get(i).getDistanceValue()/1000+": duração - "+ route.get(i).getDurationValue());
            //Toast.makeText(getContext(),"Rota "+ (i+1) +": distância - "+ route.get(i).getDistanceValue()/1000+": duração - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }
    }//onRoutingSuccess

    @Override
    public void onRoutingCancelled() {

    }
    private void erasePolylines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }//erasePolylines

    //-----------------FIM POLYLINES-----------------------//
}
