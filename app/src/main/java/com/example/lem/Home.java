package com.example.lem;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.File;
import java.util.ArrayList;

public class Home extends AppCompatActivity {

    MapView map = null;
    private ViewPager viewPager;
    private ViewPageAdapter viewPageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //test swip
        viewPager = findViewById(R.id.viewpage);
        viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        viewPager.setAdapter(viewPageAdapter);

        // load/initialize the osmdroid configuration
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // inflate and create the map
        setContentView(R.layout.activity_home);
        map = (MapView) findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);

        // default zoom buttons and ability to zoom with 2 fingers (multi-touch) (use ctrl on web)
        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);

        // default view point
        IMapController mapController = map.getController();
        mapController.setZoom((int) 17);
        GeoPoint startPoint = new GeoPoint(47.368284, 1.740004);
        GeoPoint startPoint1 = new GeoPoint(47.370524, 1.738388);
        mapController.setCenter(startPoint);

        Marker startMarker = new Marker(map);
        Marker startMarker1 = new Marker(map);

        startMarker.setPosition(startPoint);
        startMarker1.setPosition(startPoint1);

        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker1.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        map.getOverlays().add(startMarker);
        map.getOverlays().add(startMarker1);

        startMarker.setIcon(getResources().getDrawable(R.drawable.marker));
        startMarker.setTitle("Start point");

        startMarker1.setIcon(getResources().getDrawable(R.drawable.marker));
        startMarker1.setTitle("Start point");

    }
}
