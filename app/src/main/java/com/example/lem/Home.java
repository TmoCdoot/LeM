package com.example.lem;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.File;
import java.util.ArrayList;

public class Home extends AppCompatActivity {

    MapView map = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load/initialize the osmdroid configuration
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        // inflate and create the map
        setContentView(R.layout.activity_home);
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        // default zoom buttons and ability to zoom with 2 fingers (multi-touch)
        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);

        // default view point
        IMapController mapController = map.getController();
        mapController.setZoom((int) 17);
        GeoPoint startPoint = new GeoPoint(47.368284, 1.740004);
        mapController.setCenter(startPoint);

    }

    // add MapView
    /*public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MapView mMapView = new MapView(inflater.getContext(), map.getTileProvider(), map.getContext());
        return mMapView;

        // ping system
        //your items
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        items.add(new OverlayItem("Title", "Description", new GeoPoint(0.0d,0.0d))); // Lat/Lon decimal degrees

        //the overlay la bite que tu aime
        Context context = null;
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, context);
        mOverlay.setFocusItemsOnTap(true);

        mMapView.getOverlays().add(mOverlay);
    }*/

}