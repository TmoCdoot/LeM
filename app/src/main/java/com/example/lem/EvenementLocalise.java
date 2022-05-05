package com.example.lem;

import org.osmdroid.util.GeoPoint;

public class EvenementLocalise {

    private GeoPoint coord;
    private String activity_id;
    private String activity_id_creator;
    private String activity_latitude;
    private String activity_longitude;
    private String activity_name;
    private String activity_max_number;
    private String activity_status;
    private String activity_category_id;
    private String activity_view;

    public EvenementLocalise(GeoPoint coord, String activity_id, String activity_id_creator, String activity_latitude, String activity_longitude, String activity_name, String activity_max_number, String activity_status, String activity_category_id, String activity_view) {
        this.coord = coord;
        this.activity_id = activity_id;
        this.activity_id_creator = activity_id_creator;
        this.activity_latitude = activity_latitude;
        this.activity_longitude =activity_longitude;
        this.activity_name = activity_name;
        this.activity_max_number = activity_max_number;
        this.activity_status = activity_status;
        this.activity_category_id = activity_category_id;
        this.activity_view = activity_view;
    }

    public EvenementLocalise(GeoPoint coord, String activity_id_creator, String activity_latitude, String activity_longitude, String activity_name, String activity_max_number, String activity_status, String activity_category_id, String activity_view) {
        this.coord = coord;
        this.activity_id_creator = activity_id_creator;
        this.activity_latitude = activity_latitude;
        this.activity_longitude =activity_longitude;
        this.activity_name = activity_name;
        this.activity_max_number = activity_max_number;
        this.activity_status = activity_status;
        this.activity_category_id = activity_category_id;
        this.activity_view = activity_view;
    }


    /*public GeoPoint getCoord() {
        return coord;
    }*/

    /*public void setCoord(GeoPoint coord) {
        this.coord = coord;
    }*/

    public GeoPoint getCoord() {
        return coord;
    }

    public String getActivity_id() {
        return activity_id;
    }

    public String getActivity_latitude() {
        return activity_latitude;
    }

    public String getActivity_longitude() {
        return activity_longitude;
    }

    public String getActivity_id_creator() {
        return activity_id_creator;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public String getActivity_max_number() {
        return activity_max_number;
    }

    public String getActivity_status() {
        return activity_status;
    }

    public String getActivity_category_id() {
        return activity_category_id;
    }

    public String getActivity_view() {
        return activity_view;
    }
}
