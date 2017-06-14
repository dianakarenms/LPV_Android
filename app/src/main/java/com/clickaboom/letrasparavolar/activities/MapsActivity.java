package com.clickaboom.letrasparavolar.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.models.collections.Colecciones;
import com.clickaboom.letrasparavolar.models.collections.ResCollections;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.clickaboom.letrasparavolar.network.GsonRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import static com.clickaboom.letrasparavolar.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String EXTRA_MAP_TYPE = "com.lpv.extraMapType";
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private Context mContext;
    private String mapType;

    public static Intent newInstance(Context packageContext, String mapType) {
        Intent i = new Intent(packageContext, MapsActivity.class);
        i.putExtra(EXTRA_MAP_TYPE, mapType);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Set toolbar_asistant gone
        findViewById(R.id.toolbar_asistant).setVisibility(View.GONE);
        findViewById(R.id.toolbar).findViewById(R.id.drawer_button).setVisibility(View.GONE);

        // BackBtn
        LinearLayout backBtn = (LinearLayout) findViewById(R.id.back_btn);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mapType = getIntent().getStringExtra(EXTRA_MAP_TYPE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        mContext = this;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                startActivity(BookDetailsActivity.newIntent(mContext, ((Colecciones)marker.getTag()).id, mapType));
            }
        });

        loadMarkers();
        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    private void loadMarkers() {

        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(mContext)
                .addToRequestQueue(new GsonRequest(ApiConfig.mapaMarkers,
                        ResCollections.class,
                        Request.Method.GET,
                        null, null,
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                Log.d(TAG, response.toString());
                                List<Colecciones> res = new ArrayList<>();

                                if(mapType.equals(BookDetailsActivity.LEGENDS))
                                    res = ((ResCollections) response).leyendas;
                                else if(mapType.equals(BookDetailsActivity.COLECCIONES))
                                    res = ((ResCollections) response).colecciones;

                                for(Colecciones col: res) {
                                    createMarker(col);
                                }

                                LatLng mexicoCenter = new LatLng(20.926893,-101.9256694);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mexicoCenter, 4.0f));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }));

    }

    private Marker createMarker(Colecciones item) {
       Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(item.latitud, item.longitud))
                .anchor(0.5f, 0.5f)
                .title(item.titulo));
                //.snippet(item.descripcion));
        marker.setTag(item);
        return marker;
/*        PicassoMarker pMarker = new PicassoMarker(marker);
        Picasso.with(MapsActivity.this).load(url).into(pMarker);*/
    }
}
