package com.joaomadeira.android.appddm;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MeuMapa implements
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback{
    android.app.Activity mActivity;
    GoogleMap mGoogleMap;
    AmUtil mUtil;
    int mAppRequestCode;

//--------------------------------------------------------------------------------------------------------------

    public MeuMapa(
            android.app.Activity pA,
            int pAppRequestCode
    ) {
        mAppRequestCode = pAppRequestCode;
        mActivity = pA;
        mUtil = new AmUtil(mActivity, pAppRequestCode);
    }//MeuMapa

    //----------------------------------------------------------------------------------------------------
    /*
    O marcador é um Objeto Marker (com.google.android.gms.maps.model.Marker)
    a construção do Marker faz-se à custa de objetos MarkerOptions
    (com.google.android.gms.maps.model.MarkerOptions)
    O padrão é pois:
    - fazer um novo MarkerOptions
    - fazer um novo Marker com as MarkerOptions anteriores
    - acrescentar ao GoogleMap por addMarker o novo marcador
    */
    Marker criaMarcadorNoMapa(
            LatLng sitio,
            String pDescritivo
    ) {
        //https://developers.google.com/android/reference/com/google/android/gms/maps/model/MarkerOptions
        MarkerOptions dadosDoMarcador = new MarkerOptions();

        //https://developers.google.com/android/reference/com/google/android/gms/maps/model/MarkerOptions.html#position(com.google.android.gms.maps.model.LatLng)
        //Sets the location for the marker.
        dadosDoMarcador.position(sitio);

        //https://developers.google.com/android/reference/com/google/android/gms/maps/model/MarkerOptions.html#title(java.lang.String)
        //Sets the title for the marker.
        String strTitle = "@" + sitio.latitude + ", " + sitio.longitude;
        dadosDoMarcador.title(strTitle);

        //https://developers.google.com/android/reference/com/google/android/gms/maps/model/MarkerOptions.html#snippet(java.lang.String)
        //Sets the snippet for the marker.
        dadosDoMarcador.snippet(pDescritivo);

        //https://developers.google.com/android/reference/com/google/android/gms/maps/model/MarkerOptions.html#alpha(float)
        //Sets the alpha (opacity) of the marker.
        dadosDoMarcador.alpha(0.9f);

        //https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.html#addMarker(com.google.android.gms.maps.model.MarkerOptions)
        /*
        Adds a marker to this map.
        The marker's icon is rendered on the map at the location Marker.position.
        Clicking the marker centers the camera on the marker.
        If Marker.title is defined, the map shows an info box with the marker's title
        and snippet.
        If the marker is draggable, long-clicking and then dragging the marker moves
        it.
        */
        Marker marcador = null;
        if (mGoogleMap != null) {
            marcador = mGoogleMap.addMarker(dadosDoMarcador);
        }
        return marcador;
    }//criaMarcadorNoMapa

    //--------------------------------------------------------------------------------------------------------------
    public boolean mudarTipoDeMapa(
            int pNovoTipoDeMapa
    ) {
        if (mGoogleMap != null) {
            switch (pNovoTipoDeMapa) {
                case GoogleMap.MAP_TYPE_HYBRID:
                case GoogleMap.MAP_TYPE_NONE:
                case GoogleMap.MAP_TYPE_NORMAL:
                case GoogleMap.MAP_TYPE_SATELLITE:
                case GoogleMap.MAP_TYPE_TERRAIN:
                    mGoogleMap.setMapType(pNovoTipoDeMapa);
                    return true;
                default:
                    //tipo de mapa desconhecido
                    return false;
            }//switch
        }//if
        return false;
    }//mudarTipoDeMapa
//--------------------------------------------------------------------------------------------------------------

    public void centrarEmGeoPosicao(
            double pLatitude,
            double pLongitude,
            int pZoom
    ) {
        //CameraUpdate camUpdate = new CameraUpdate (...); //CameraUpdate is not public
        //https://developers.google.com/android/reference/com/google/android/gms/maps/CameraUpdateFactory.html
        //https://developers.google.com/android/reference/com/google/android/gms/maps/CameraUpdateFactory.html#newLatLngZoom(com.google.android.gms.maps.model.LatLng, % 20f loat)
        CameraUpdate geoLocalizacao = CameraUpdateFactory.newLatLngZoom(
                new LatLng(pLatitude, pLongitude),
                pZoom
        );
        /*
        public final void moveCamera (CameraUpdate update)
        Repositions the camera according to the instructions defined in the update.
        The move is instantaneous, and a subsequent getCameraPosition() will reflect the new
        position. See CameraUpdateFactory for a set of updates.
        Parameters : update The change that should be applied to the camera.
        */
        //https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.html#moveCamera(com.google.android.gms.maps.CameraUpdate)
        mGoogleMap.moveCamera(geoLocalizacao);
    }//vaiParaLatLng
//--------------------------------------------------------------------------------------------------------------

    @Override
    public void onMapReady(
            GoogleMap pGoogleMap
    ) {
        /*
        https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap
        int MAP_TYPE_HYBRID Satellite maps with a transparent layer of major
        streets.
        int MAP_TYPE_NONE No base map tiles.
        int MAP_TYPE_NORMAL Basic maps.
        int MAP_TYPE_SATELLITE Satellite maps with no labels.
        int MAP_TYPE_TERRAIN Terrain maps.
        */

        pGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        boolean bRequestResult =
                mUtil.utilModernRequestPermission(
                        //android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        mAppRequestCode,
                        true
                );
        try {
            //https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.html#setMyLocationEnabled(boolean)
            //Enables or disables the my-location layer
            pGoogleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {

        }

        //accessibility related

        //https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.html#setContentDescription(java.lang.String)
        pGoogleMap.setContentDescription("isto é um mapa!"); //This is used to provide a spoken description of the map in accessibility mode.The default value is "Google Map"

        //layers related

        //https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.html#setBuildingsEnabled(boolean)
        //Turns the 3D buildings layer on or off.
        pGoogleMap.setBuildingsEnabled(true);

        //https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.html#isIndoorEnabled()
        /*
        Sets whether indoor maps should be enabled.
        Currently, indoor maps can only be shown on one map at a time and by default,
        this is the first map added to your application.
        To enable indoor maps on another map, you must first disable indoor maps on
        the original map.
        If you try to enable indoor maps when it is enabled on another map, nothing
        will happen and this will return false.
        When Indoor is not enabled for a map, all methods related to indoor will
        return null, or false.
        https://support.google.com/maps/answer/2803784?co=GENIE.Platform%3DDesktop&hl=en
        */
        pGoogleMap.setIndoorEnabled(false);

        //https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap.html#setTrafficEnabled(boolean)
        //Turns the traffic layer on or off.
        pGoogleMap.setTrafficEnabled(false);

        //https://developers.google.com/android/reference/com/google/android/gms/maps/UiSettings.html#setCompassEnabled(boolean)
        /*
        If enabled, it is only shown when the camera is tilted or rotated away from
        its default orientation (tilt of 0 and a bearing of 0)
        */
        pGoogleMap.getUiSettings().setCompassEnabled(true); //Enables or disables thecompass. =>aparece no canto superior esquerdo

        //https://developers.google.com/android/reference/com/google/android/gms/maps/UiSettings.html#setMyLocationButtonEnabled(boolean)
        /*
        The my-location button causes the camera to move such that the user's location
        is in the center of the map.
        If the button is enabled, it is only shown when the my-location layer is
        enabled.
        */
        pGoogleMap.getUiSettings().setMyLocationButtonEnabled(true); //Enables ordisables the my - location button =>aparece no canto superior direito

        //https://developers.google.com/android/reference/com/google/android/gms/maps/UiSettings.html#setZoomControlsEnabled(boolean)
        /*
        If enabled, the zoom controls are a pair of buttons (one for zooming in, one
        for zooming out) that appear on the screen.
        */
        pGoogleMap.getUiSettings().setZoomControlsEnabled(false); //aparece no canto inferior direito

        //https://developers.google.com/android/reference/com/google/android/gms/maps/UiSettings.html#setMapToolbarEnabled(boolean)
        /*
        If enabled, and the Map Toolbar can be shown in the current context, users
        will see a bar with various context-dependent actions,
        including 'open this map in the Google Maps app' and 'find directions to the
        highlighted marker in the Google Maps app'.
        */
        //pGoogleMap.getUiSettings().setMapToolbarEnabled(true); //não consigo visualizar no emulador
        mGoogleMap = pGoogleMap;

        mudarTipoDeMapa(GoogleMap.MAP_TYPE_NORMAL);
        criaMarcadorNoMapa(new LatLng(11.1, 11.1), "Bla!");
        centrarEmGeoPosicao(11.1, 11.1, 10);
        //criaMarcadorNoMapa(new LatLng(0, 0), "Bla!");
        //centrarEmGeoPosicao(0, 0, 10);
            //criaMarcadorNoMapa(new LatLng(mGoogleMap.getMyLocation().getLatitude(), mGoogleMap.getMyLocation().getLongitude()), "Estou aqui!");
            //centrarEmGeoPosicao(mGoogleMap.getMyLocation().getLatitude(), mGoogleMap.getMyLocation().getLongitude(), 10);
        //criaMarcadorNoMapa(new LatLng(11.1, 11.1), "Bla!");
        //centrarEmGeoPosicao(11.1, 11.1, 10);
        //}

    }//onMapReady

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        //Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
        criaMarcadorNoMapa(new LatLng(location.getLatitude(), location.getLongitude()), "Bla!");
        centrarEmGeoPosicao(location.getLatitude(), location.getLongitude(), 10);
    }

}//MeuMapa