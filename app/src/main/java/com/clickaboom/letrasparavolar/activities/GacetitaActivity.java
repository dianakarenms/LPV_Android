package com.clickaboom.letrasparavolar.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.adapters.GacetitaAdapter;
import com.clickaboom.letrasparavolar.models.gacetita.Gacetita;
import com.clickaboom.letrasparavolar.models.gacetita.ResGacetita;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.clickaboom.letrasparavolar.network.GsonRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karencita on 15/05/2017.
 */

public class GacetitaActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "com.lpv.gacetitas";
    private RecyclerView mCollectionsRV;
    private GacetitaAdapter mColeccionesAdapter;
    private List<Gacetita> mCollectionsList = new ArrayList<>();
    private GridLayoutManager mGridLayoutManager;
    private Context mContext;

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, GacetitaActivity.class);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gace_noti);
        mContext = this;

        // Menu drawer onclicklistener
        findViewById(R.id.drawer_button).setVisibility(View.INVISIBLE);

        // Set toolbar_asistant title
        ((TextView)findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.gacetita));
        findViewById(R.id.left_btn).setVisibility(View.GONE);
        findViewById(R.id.right_btn).setVisibility(View.GONE);

        // BackBtn
        LinearLayout backBtn = (LinearLayout) findViewById(R.id.back_btn);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Collections RecyclerView
        mColeccionesAdapter = new GacetitaAdapter(mCollectionsList, mContext);
        mCollectionsRV = (RecyclerView) findViewById(R.id.collections_recycler);
        mGridLayoutManager = new GridLayoutManager(mContext, 3);
        mCollectionsRV.setLayoutManager(mGridLayoutManager);
        mCollectionsRV.setHasFixedSize(true);
        mColeccionesAdapter.mColType = BookDetailsActivity.COLECCIONES;
        mCollectionsRV.setAdapter(mColeccionesAdapter);

        // show new collections on start
        if(mCollectionsList.isEmpty()) {
            loadGacetitas();
        }
    }

    private void loadGacetitas() {
        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(mContext)
                .addToRequestQueue(new GsonRequest(ApiConfig.gacetitas,
                        ResGacetita.class,
                        Request.Method.GET,
                        null, null,
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                Log.d(TAG, response.toString());
                                List<List<Gacetita>> res = ((ResGacetita) response).data;
                                mCollectionsList.clear();
                                for(List<Gacetita> item : res) {
                                    mCollectionsList.addAll(item); // Add main book to list
                                    //db.addAllBooks(item, BookDetailsActivity.COLECCIONES);
                                }
                                mColeccionesAdapter.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
        }
    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SEARCH:
                if(resultCode == RESULT_OK) {
                    loadGacetitas(ApiConfig.searchCollections, "?q=" + data.getStringExtra(RESULT_SEARCH));
                }
                break;
        }
    }*/
}
