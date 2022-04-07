package com.example.lem;

import org.osmdroid.util.GeoPoint;

public class EvenementLocalise {

    private GeoPoint coord;

    public EvenementLocalise(GeoPoint coord, String name) {
        this.coord = coord;
        this.descr = name;
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
