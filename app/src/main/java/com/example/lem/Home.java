package com.example.lem;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements Serializable {

    MapView map = null;
    // Resources res = getResources();
    private ViewPager viewPager;






    /**
     * la collection de points d'intérêt (evenement)
     */
    private ArrayList<EvenementLocalise> mesEvenements;
    /**
     * le centre de la  map
     */
    private GeoPoint center;

    private EnumerationEtatsInteraction etat;

    /**
     * Récupère par web service les evennements que l'utilisateur doit voir
     * @param userId le nom de l'utilisateur
     * @return la liste des evennements que l'utilisateur doit voir
     */
    private void requestEventsForUser(String userId){
        ArrayList<EvenementLocalise> l = new ArrayList<>();

        String url = "http://10.0.2.2/~maxime.dumontet/jumati/public/get_data_activity_user/" + userId;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                this::processEventsForUser,
                this::getError);
    }

    /**
     * traite la reponse du serveur contenant les evenements localises
     * @param reponse la reponse du serveur
     */
    private void processEventsForUser(String reponse){
        ArrayList<EvenementLocalise> l =new ArrayList<>();
        //code de traitement volley, parsing du json, instanciation des evennement
        try {
            JSONArray obj = new JSONArray(reponse);
            for (int i = 0; i < obj.length(); i++) {
                JSONObject jo = obj.getJSONObject(i);
                EvenementLocalise el = new EvenementLocalise((GeoPoint) jo.get("activity_coordinate"), jo.getString("activity_name"));
                l.add(el);
            }

        } catch (JSONException e) {
            getError(e);
        }

        //je mets a jour la liste d'evennements de l'activity
        mesEvenements=l;

        //je demare la creation de la map
        startMap();
    }

    // en cas d'erreur de la requete, fonction appelée
    public void getError(Throwable t) {
        Toast.makeText(this, "probleme serveur", Toast.LENGTH_LONG).show();
        Log.e("categorie", "probleme serveur", t);
    }


    /**
     * simple ou double tap
     */
    private Overlay buildTouchOverlay(){
        Overlay touchOverlay = new Overlay(this){
            ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = null;
            @Override
            public void draw(Canvas arg0, MapView arg1, boolean arg2) {

            }

            private void dlgThread() {
            }


            //ajout de point au click
            @Override
            public boolean onSingleTapConfirmed(final MotionEvent e, final MapView mapView) {
                Projection proj = mapView.getProjection();
                GeoPoint loc = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());
                switch(etat){
                    case VISUALISATION:
                        //code de reaction au tap dans le mode visualisation
                        EvenementLocalise elo=nearest(loc);
                        if(null!=elo){
                            Toast.makeText(getApplicationContext(),elo.getDescr(),Toast.LENGTH_LONG).show();
                        }
                        break;
                    case EDITION:
                        //code de reaction au tap dans le mode edition
                        break;
                    case SUPPRESSION:
                        //code de reaction au tap dans le mode supression
                        break;
                    case CREATION:
                        //code de reaction au tap dans le mode creation
                        EvenementLocalise el = new EvenementLocalise(loc,"nouvel evenement");
                        //on ajoute l'evennement pour sauvegarde ulterieure (ou plutot on pourrait declencher directement la sauvegarde -- plus tard --)
                        mesEvenements.add(el);
                        addMarkerFromEvennementLocalise(el);

                        break;
                }

                map.invalidate();
                dlgThread();
                return true;
            }
        };
        return touchOverlay;
    }



    /**
     * affiche une map avec les points interessants stockés dans mesEvenements
     */
    private void startMap(){
        // load/initialize the osmdroid configuration
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // inflate and create the map
        setContentView(R.layout.activity_home);
        map = (MapView) findViewById(R.id.mapview);

        // default zoom buttons and ability to zoom with 2 fingers (multi-touch) (use ctrl on web)
        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);

        // default view point
        IMapController mapController = map.getController();
        mapController.setZoom((int) 17);

        //positionnement du centre de la map
        mapController.setCenter(center);

        // ajout de tous les points stockés dans mesEvenements
        for (EvenementLocalise e:mesEvenements){
            addMarkerFromEvennementLocalise(e);
        }

        map.getOverlays().add(buildTouchOverlay());
    }

    private void addMarkerFromEvennementLocalise(EvenementLocalise e){
        Marker m=new Marker(map);
        m.setPosition(e.getCoord());
        Log.e("coordonnées", String.valueOf(e.getCoord()));
        m.setTitle(e.getDescr());
        m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(m);
        m.setIcon(getResources().getDrawable(R.drawable.marker));
    }






    /**
     * Simule la recuperation des evenements par ws
     * @param usr
     */
    private void fakeRequestEventsForUser(String usr){
        ArrayList<EvenementLocalise> elist = new ArrayList<>();
        EvenementLocalise el = new EvenementLocalise(new GeoPoint(47.368284, 1.740004),"tennis avec Louise");
        elist.add(el);
        el = new EvenementLocalise(new GeoPoint(47.370524, 1.738388),"Foot avec Bob");
        elist.add(el);
        mesEvenements=elist;
        startMap();
    }









    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mesEvenements = null;

        //on demandera la recuperation des evennements pour l'utilisateur connecté
        //la methode process associée au chargement executera l'affichage de la map
        /*requestEventsForUser("bob");*/


        //je configure le centre de la map
        center = new GeoPoint(new GeoPoint(47.368284, 1.740004));


        //je place mon ui en mode visualisation
        modeCreation(null);

        //connexion user
        Intent intent = getIntent();

        UserClass user = (UserClass) intent.getSerializableExtra("user");

        Log.e("user", String.valueOf(user.getUser_email()));

        /*//pour patienter avant qu'on ait développé tout ce qu'il faut pour les ws, on fait appel à une fausse méthode
        fakeRequestEventsForUser("bob");*/

        //test recuperation des activités depuis ws
        requestEventsForUser(String.valueOf(user.getId_user()));

    }





    /**
     * test different mode
     * */

    public void modeVisualisation(View v){
        etat=EnumerationEtatsInteraction.VISUALISATION;
    }
    public void modeEdition(View v){
        etat=EnumerationEtatsInteraction.EDITION;
    }
    public void modeSupression(View v){
        etat=EnumerationEtatsInteraction.VISUALISATION;
    }
    public void modeCreation(View v){
        etat=EnumerationEtatsInteraction.CREATION;
    }









    /**
     * retourne l'evennement le plus proche du geopoint passé en param
     * @param loc le geopoint tappé
     * @return l'evt le plus proche (null si trop loin????????)
     */
    public EvenementLocalise nearest(GeoPoint loc){
        double distance=Double.MAX_VALUE;
        EvenementLocalise selected=null;
        for(EvenementLocalise el:mesEvenements){
            GeoPoint coord = el.getCoord();
            double candidateDist=distance(loc.getLongitude(),loc.getLatitude(),coord.getLongitude(),coord.getLatitude(),0,0);
            if (candidateDist<distance){
                distance=candidateDist;
                selected = el;
            }
        }
        return selected;
    }







    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    private double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }


}
