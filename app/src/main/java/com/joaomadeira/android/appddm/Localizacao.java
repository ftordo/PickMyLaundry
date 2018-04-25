package com.joaomadeira.android.appddm;

/**
 * Created by Fábio on 20/01/2018.
 */

public class Localizacao {
    double latitude;
    double longitude;

    public Localizacao(){

    }

    public Localizacao(double pLatitude,double pLongitude) {
        this.latitude = pLatitude;
        this.longitude =  pLongitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
