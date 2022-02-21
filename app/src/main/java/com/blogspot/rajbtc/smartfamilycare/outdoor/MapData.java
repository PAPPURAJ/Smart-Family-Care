package com.blogspot.rajbtc.smartfamilycare.outdoor;

public class MapData{
    private double Lat,Lon;

    public MapData() {
    }

    public MapData(double lat, double lon) {
        Lat = lat;
        Lon = lon;
    }

    public double getLat() {
        return Lat;
    }

    public double getLon() {
        return Lon;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public void setLon(double lon) {
        Lon = lon;
    }
}
