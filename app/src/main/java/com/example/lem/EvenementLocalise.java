package com.example.lem;

import org.osmdroid.util.GeoPoint;

public class EvenementLocalise {

    private GeoPoint coord;

    public EvenementLocalise(GeoPoint coord, String descr) {
        this.coord = coord;
        this.descr = descr;
    }

    public GeoPoint getCoord() {
        return coord;
    }

    public void setCoord(GeoPoint coord) {
        this.coord = coord;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    private String descr;




}
