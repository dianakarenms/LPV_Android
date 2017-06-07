package com.clickaboom.letrasparavolar.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.models.collections.Colecciones;
import com.clickaboom.letrasparavolar.models.collections.ResCollections;
import com.clickaboom.letrasparavolar.models.defaults.ResDefaults;
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

import java.util.List;

import static com.clickaboom.letrasparavolar.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String EXTRA_MAP_TYPE = "com.lpv.extraMapType";
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private Context mContext;

    public static Intent newInstance(Context packageContext, String mapType) {
        Intent i = new Intent(packageContext, MapsActivity.class);
        i.putExtra(EXTRA_MAP_TYPE, mapType);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
//                Toast.makeText(mContext, "Clicked title " + ((Colecciones)marker.getTag()).titulo, Toast.LENGTH_SHORT);
//                Intent intent = new Intent(MapsActivity.this, SearchActivity.class);
//                startActivity(intent);
                loadItem(ApiConfig.legends, "?id=" + ((Colecciones)marker.getTag()).id);
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
                                List<Colecciones> res = ((ResCollections) response).leyendas;
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

    private void loadItem(String url, String params) {
        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(mContext)
                .addToRequestQueue(new GsonRequest(url + params,
                        ResDefaults.class,
                        Request.Method.GET,
                        null, null,
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                Log.d(TAG, response.toString());
                                List<Colecciones> res = ((ResDefaults) response).data;
                                startActivity(BookDetailsActivity.newIntent(mContext, res.get(0)));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }));

    }
}
