package com.example.lem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity implements Serializable, DemandeFriendsInterface {

    RequestQueue fileRequeteWS;

    //map
    MapView map = null;
    private GeoPoint center = null;
    private GeoPoint centertest = new GeoPoint(47.3592182, 1.7434925);

    //pour localisation gps
    private LocationManager locationManager;
    private LocationListener locationListener;

    //list activity
    private ArrayList<ActivityClass> mesEvenements = new ArrayList<>();

    //list friend
    private ArrayList<FriendsClass> mesFriends = new ArrayList<>();
    private int countFriend;
    private boolean readyToLoadEvenement = false;

    //user
    private UserClass user;

    //ajout amis
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText pseudoAddFriends;
    private Button buttonAddFriends;

    //demande amis en recu
    private ArrayList<FriendsClass> demandeFriends = new ArrayList<>();

    //ajout activité
    private EditText activity_name, activity_max_member;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileRequeteWS = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        user = (UserClass) intent.getSerializableExtra("user");

        //recuperation des friends de l'utilisateur
        requestGetFriendsByUser();
        requestGetDemandeFriends();

        //definir permission localisation
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, 10);
            return;
        } else {
            loadParamMapPermission();
        }
    }


    /**
     * Récupère les demande d'amis de l'utilisateur
     */
    //recuperation des demande d'amis en attente
    private void requestGetDemandeFriends() {
        demandeFriends = new ArrayList<>();
        String url = "http://10.0.2.2/~timeo.cadouot/Jumati/public/webservice/get_demande_friends?id=" + String.valueOf(user.getId_user());
        //String url = "http://10.0.2.2/Jumati/public/webservice/get_data_friend?id=" + String.valueOf(user.getId_user());
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                this::procesDemandeFriends,
                this::getError);
        fileRequeteWS.add(stringRequest);
    }

    //traite le resultat et les mets dans des tableaux
    private void procesDemandeFriends(String reponse) {
        try {
            JSONArray obj = new JSONArray(reponse);
            if (obj.getJSONObject(0).getString("demandeReturn").equals("1")) {
                for (int i = 1; i< obj.length(); i++) {
                    String id_friends = obj.getJSONObject(i).getString("exp_friend_user_id");
                    String id_friend_demande = obj.getJSONObject(i).getString("friend_id");

                    String url = "http://10.0.2.2/~timeo.cadouot/Jumati/public/webservice/get_data_user_by_friend_id?id=" + id_friends + "&iddemande=" + id_friend_demande;
                    //String url = "http://10.0.2.2/Jumati/public/webservice/get_data_user_by_friend_id?id=" + id_friends;
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.GET,
                            url,
                            this::procesDemandeFriendsIdResult,
                            this::getError);
                    fileRequeteWS.add(stringRequest);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //traite resultate recuperation info demandeur d'amis et ajout dans tableau
    private void procesDemandeFriendsIdResult(String reponse) {
        try {
            JSONArray obj = new JSONArray(reponse);
            FriendsClass friend = new FriendsClass(obj.getJSONObject(0).getString("id_demande"),
                    obj.getJSONObject(1).getString("user_id"),
                    obj.getJSONObject(1).getString("user_pseudo"),
                    obj.getJSONObject(1).getString("user_activity_id_create"),
                    obj.getJSONObject(1).getString("user_activity_id_join"));
            demandeFriends.add(friend);

            //Log.d("list demande", String.valueOf(demandeFriends));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Récupère les amis de l'utilisateur
     */
    //recuperation de friends de l'utilisateur connecter
    private void requestGetFriendsByUser() {
        mesFriends = new ArrayList<>();
        String url = "http://10.0.2.2/~timeo.cadouot/Jumati/public/webservice/get_data_friend?id=" + String.valueOf(user.getId_user());
        //String url = "http://10.0.2.2/Jumati/public/webservice/get_data_friend?id=" + String.valueOf(user.getId_user());
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                this::processFriendsResultRequest,
                this::getError);
        fileRequeteWS.add(stringRequest);
    }

    //traite le resultat et fait une requete pour recuperer les information du friends
    private void processFriendsResultRequest(String reponse) {
        try {
            JSONArray obj = new JSONArray(reponse);
            if (obj.getJSONObject(0).getString("friendsReturn").equals("1")) {
                countFriend = obj.length()-1;
                for (int i = 1; i< obj.length(); i++) {
                    String id_friends = obj.getJSONObject(i).getString("dest_friend_user_id");

                    if (user.getId_user().equals(id_friends)) {
                        id_friends = obj.getJSONObject(i).getString("exp_friend_user_id");
                    }

                    String url = "http://10.0.2.2/~timeo.cadouot/Jumati/public/webservice/get_data_user_by_friend_id?id=" + id_friends;
                    //String url = "http://10.0.2.2/Jumati/public/webservice/get_data_user_by_friend_id?id=" + id_friends;
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.GET,
                            url,
                            this::processDataByFriendsIdResult,
                            this::getError);
                    fileRequeteWS.add(stringRequest);
                }
            } else {
                //Toast.makeText(this,"Pas d'amis", Toast.LENGTH_LONG).show();
                readyToLoadEvenement = true;
                startMap();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //traitement resultat et creation des objet friends et ajout dans la liste des friend
    private void processDataByFriendsIdResult(String reponse) {
        try {
            JSONArray obj = new JSONArray(reponse);
            FriendsClass friend = new FriendsClass(obj.getJSONObject(1).getString("user_id"),
                    obj.getJSONObject(1).getString("user_pseudo"),
                    obj.getJSONObject(1).getString("user_activity_id_create"),
                    obj.getJSONObject(1).getString("user_activity_id_join"));
            mesFriends.add(friend);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        int countFriendList = mesFriends.size();
        if (countFriendList == countFriend) {
            readyToLoadEvenement = true;
            //Log.d("Listes friends", String.valueOf(mesFriends));

            //test recuperation des activités depuis ws
            requestEventsForUser();
        }
    }


    /**
     * parametre de la location de l'utilisateur
     */
    //si user accepter la location alors load map
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadParamMapPermission();
                }
                return;
        }
    }

    private void loadParamMapPermission() {
        //mesEvenements = null;

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                //Log.d("gps", location.getLatitude() + "   " + location.getLongitude());
                center = new GeoPoint(location.getLatitude(), location.getLongitude());

                //connexion user
                //Log.e("user", String.valueOf(user.getUser_email()));
            }
        };
        locationManager.requestLocationUpdates("gps", 0, 1000, locationListener);
    }


    /**
     * Récupère par web service les evennements que l'utilisateur doit voir
     * @return la liste des evennements que l'utilisateur doit voir
     */
    //recuperation activité des amis de l'utilisateur en a crée une
    private void requestEventsForUser(){
        mesEvenements = new ArrayList<>();
        String url = "http://10.0.2.2/~timeo.cadouot/Jumati/public/webservice/get_data_activity_user?id=" + String.valueOf(user.getId_user()) ;
        //String url = "http://10.0.2.2/Jumati/public/webservice/get_data_activity_user?id=" + String.valueOf(mesFriends.get(i).getFriend_user_id()) ;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                this::processEventsForUser,
                this::getError);
        fileRequeteWS.add(stringRequest);
        for (int i = 0; i < mesFriends.size(); i++) {
            String url2 = "http://10.0.2.2/~timeo.cadouot/Jumati/public/webservice/get_data_activity_user?id=" + String.valueOf(mesFriends.get(i).getFriend_user_id()) ;
            //String url = "http://10.0.2.2/Jumati/public/webservice/get_data_activity_user?id=" + String.valueOf(mesFriends.get(i).getFriend_user_id()) ;
            StringRequest stringRequest2 = new StringRequest(
                    Request.Method.GET,
                    url2,
                    this::processEventsForUser,
                    this::getError);
            fileRequeteWS.add(stringRequest2);
        }

    }

    private void processEventsForUser(String reponse){
        //code de traitement volley, parsing du json, instanciation des evennement
        try {
            JSONArray obj = new JSONArray(reponse);
            if (obj.getJSONObject(0).getString("activitesReturn").equals("1")) {
                ActivityClass activity = new ActivityClass(
                        new GeoPoint(Double.parseDouble(obj.getJSONObject(1).getString("activity_latitude")), Double.parseDouble(obj.getJSONObject(1).getString("activity_longitude"))),
                        obj.getJSONObject(1).getString("activity_id"),
                        obj.getJSONObject(1).getString("activity_id_creator"),
                        obj.getJSONObject(1).getString("activity_latitude"),
                        obj.getJSONObject(1).getString("activity_longitude"),
                        obj.getJSONObject(1).getString("activity_name"),
                        obj.getJSONObject(1).getString("activity_max_member"),
                        obj.getJSONObject(1).getString("activity_status"),
                        obj.getJSONObject(1).getString("category_id"),
                        obj.getJSONObject(1).getString("activity_view"));
                mesEvenements.add(activity);
            }
        } catch (JSONException e) {
            getError(e);
        }

        /*if (mesEvenements != null) {
            if (countFriend == mesEvenements.size()) {
                Log.d("valid", "true");
            }
        }*/


        //je demare la creation de la map
        startMap();
    }

    public void getError(Throwable t) {
        Toast.makeText(this, "probleme serveur", Toast.LENGTH_LONG).show();
        Log.e("categorie", "probleme serveur", t);
    }


    /**
     * ajout d'activité par l'utilisateur par appuie long
     */
    //ajout activité avec un appuie long
    private Overlay buildTouchOverlay(){
        Overlay touchOverlay = new Overlay(this){
            ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = null;
            @Override
            public void draw(Canvas arg0, MapView arg1, boolean arg2) {
            }

            private void dlgThread() {
            }

            //ajout de point sur long press
            @Override
            public boolean onLongPress(final MotionEvent e, final MapView mapView) {
                if(user.getUser_activity_id_create().equals("null")) {
                    Projection proj = mapView.getProjection();
                    GeoPoint loc = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());

                    //instanciation des variables
                    dialogBuilder = new AlertDialog.Builder(Home.this);
                    final View popupActivityView = getLayoutInflater().inflate(R.layout.popup_create_activity, null);
                    activity_name = popupActivityView.findViewById(R.id.activity_name);
                    activity_max_member = popupActivityView.findViewById(R.id.activity_max_member);

                    final Spinner spinner = popupActivityView.findViewById(R.id.categorie_spinner);
                    // Create an ArrayAdapter using the string array and a default spinner layout
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Home.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.categories_array));
                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    spinner.setAdapter(adapter);

                    dialogBuilder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!spinner.getSelectedItem().toString().equalsIgnoreCase("Choisir une catégorie")) {
                                ActivityClass el = new ActivityClass(
                                        new GeoPoint(Double.parseDouble(String.valueOf(loc.getLatitude())),
                                                Double.parseDouble(String.valueOf(loc.getLongitude()))),
                                        user.getId_user(),
                                        Double.toString(loc.getLatitude()),
                                        Double.toString(loc.getLongitude()),
                                        activity_name.getText().toString(),
                                        activity_max_member.getText().toString(),
                                        "OPEN",
                                        spinner.getSelectedItem().toString(),
                                        null);
                                //on ajoute l'evennement pour sauvegarde ulterieure (ou plutot on pourrait declencher directement la sauvegarde -- plus tard --)
                                mesEvenements.add(el);

                                String url = "http://10.0.2.2/~timeo.cadouot/Jumati/public/webservice/create_activity?id_creator="
                                        + user.getId_user()
                                        + "&lati=" + loc.getLatitude()
                                        + "&longi=" + loc.getLongitude()
                                        + "&max_member=" + activity_max_member.getText().toString()
                                        + "&status=OPEN"
                                        + "&name=" + activity_name.getText().toString()
                                        + "&category=" + spinner.getSelectedItem().toString()
                                        + "&view=null";
                                //String url = "http://10.0.2.2/Jumati/public/webservice/get_data_activity_user?id=" + String.valueOf(mesFriends.get(i).getFriend_user_id()) ;
                                StringRequest stringRequest = new StringRequest(
                                        Request.Method.GET,
                                        url,
                                        this::processAddActivityResult,
                                        this::getError);
                                fileRequeteWS.add(stringRequest);

                                addMarkerFromEvennementLocalise(el);
                                map.invalidate();
                                dlgThread();
                                dialog.dismiss();
                            }
                        }

                        private void processAddActivityResult(String reponse) {
                            try {
                                JSONArray obj = new JSONArray(reponse);
                                if (obj.getJSONObject(0).getString("activityAddReturn").equals("1")) {
                                    Toast.makeText(Home.this, "Activité crée", Toast.LENGTH_LONG).show();
                                    user.setUser_activity_id_create(obj.getJSONObject(1).getString("activity_id"));
                                } else {
                                    Toast.makeText(Home.this, "problème ajout activité", Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }

                        public void getError(Throwable t) {
                            Log.e("categorie", "probleme serveur", t);
                        }
                    });

                    dialogBuilder.setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    //set la view
                    dialogBuilder.setView(popupActivityView);
                    dialog = dialogBuilder.create();
                    dialog.show();
                } else {
                    Toast.makeText(Home.this, "Il existe deja une activité", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        };
        return touchOverlay;
    }


    /**
     * affiche la map avec les point correspondant au évènement des amius de l'utilisateur
     */
    //chargement de la map
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

        mapController.setCenter(centertest);

        // ajout de tous les points stockés dans mesEvenements
        for (ActivityClass e:mesEvenements){
            addMarkerFromEvennementLocalise(e);
        }

        map.getOverlays().add(buildTouchOverlay());
    }

    //ajout des marker correpondant au activité
    private void addMarkerFromEvennementLocalise(ActivityClass e){
        Marker m=new Marker(map);
        m.setPosition(e.getCoord());
        m.setTitle(e.getActivity_name());
        m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(m);
        m.setIcon(getResources().getDrawable(R.drawable.marker));
    }


    /**
     * ajout d'amis et acceptation ou déclinaison des demande
     */
    //ajout amis
    public void addFriends(View v) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View friendPopUp = getLayoutInflater().inflate(R.layout.popup_friend, null);

        RecyclerView viewRecyclerBook = (RecyclerView) friendPopUp.findViewById(R.id.recyclerViewDemandeFriend);

        DemandeFriendsAdapter adapter = new DemandeFriendsAdapter(demandeFriends);
        adapter.setListener(this);
        viewRecyclerBook.setAdapter(adapter);
        viewRecyclerBook.setLayoutManager(new LinearLayoutManager(this));

        pseudoAddFriends = (EditText) friendPopUp.findViewById(R.id.pseudoAddFriends);
        buttonAddFriends = (Button) friendPopUp.findViewById(R.id.buttonAddFriends);

        dialogBuilder.setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        buttonAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pseudoFriends = pseudoAddFriends.getText().toString();

                String url = "http://10.0.2.2/~timeo.cadouot/Jumati/public/webservice/get_data_user_by_pseudo?pseudo=" + pseudoFriends + "&exp=" + user.getId_user() ;
                //String url = "http://10.0.2.2/Jumati/public/webservice/get_data_user_with_email?email=" + userEmaiToString + "&mdp=" + userPasswordToString;
                StringRequest stringRequest = new StringRequest(
                        Request.Method.GET,
                        url,
                        this::parceResultAddFriends,
                        this::getError
                );
                fileRequeteWS.add(stringRequest);
            }

            private void parceResultAddFriends(String reponse) {
                try {
                    JSONArray obj = new JSONArray(reponse);
                    if (obj.getJSONObject(0).getString("usersReturn").equals("1")) {
                        Toast.makeText(Home.this, "Demande envoyé", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(Home.this, "Utilisateur introuvable", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            public void getError(Throwable t) {
                Log.e("CONNECT", "Problème communication serveur", t);
            }
        });

        dialogBuilder.setView(friendPopUp);
        dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    //accept demande
    public void acceptDemande(int pos, View v) {
        FriendsClass demandeFriend = demandeFriends.get(pos);
        String url = "http://10.0.2.2/~timeo.cadouot/Jumati/public/webservice/accept_demande?id=" + String.valueOf(demandeFriend.getFriend_id_demande()) ;
        //String url = "http://10.0.2.2/Jumati/public/webservice/accept_demande?id=" + String.valueOf(demandeFriend.getFriend_id_demande()) ;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                this::processResultAccept,
                this::getError);
        fileRequeteWS.add(stringRequest);
    }

    //decline demane
    public void declineDemande(int pos, View v) {
        FriendsClass demandeFriend = demandeFriends.get(pos);
        String url = "http://10.0.2.2/~timeo.cadouot/Jumati/public/webservice/decline_demande?id=" + String.valueOf(demandeFriend.getFriend_id_demande()) ;
        //String url = "http://10.0.2.2/Jumati/public/webservice/decline_demande?id=" + String.valueOf(demandeFriend.getFriend_id_demande()) ;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                this::processResultDecline,
                this::getError);
        fileRequeteWS.add(stringRequest);
    }

    //traitement resultat acceptation demande d'amis
    private void processResultAccept(String reponse) {
        try {
            JSONArray obj = new JSONArray(reponse);
            if(obj.getJSONObject(0).getString("acceptDemandeReturn").equals("1")) {
                Toast.makeText(this,"Demande accepté", Toast.LENGTH_LONG).show();
                dialog.dismiss();

                //test
                requestGetDemandeFriends();
                requestGetFriendsByUser();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //traitement resultat decline demande d'amis
    private void processResultDecline(String reponse) {
        try {
            JSONArray obj = new JSONArray(reponse);
            if(obj.getJSONObject(0).getString("declineDemandeReturn").equals("1")) {
                Toast.makeText(this,"Demande décliné", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * retourne l'evennement le plus proche du geopoint passé en param
     * @param loc le geopoint tappé
     * @return l'evt le plus proche (null si trop loin????????)
     */
    /*public EvenementLocalise nearest(GeoPoint loc){
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
    }*/
}
