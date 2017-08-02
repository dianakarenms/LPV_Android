package com.clickaboom.letrasparavolar.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.adapters.NoticiaAdapter;
import com.clickaboom.letrasparavolar.models.noticias.Noticia;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karencita on 15/05/2017.
 */

public class NoticiasActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "com.lpv.noticias";
    private RecyclerView mCollectionsRV;
    private NoticiaAdapter mNoticiasAdapter;
    private List<Noticia> mNoticiasList = new ArrayList<>();
    private Context mContext;

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, NoticiasActivity.class);
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
        ((TextView)findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.noticias));
        findViewById(R.id.leyendas_prev_btn).setVisibility(View.GONE);
        findViewById(R.id.leyendas_next_btn).setVisibility(View.GONE);

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
        mNoticiasAdapter = new NoticiaAdapter(mNoticiasList, mContext);
        mCollectionsRV = (RecyclerView) findViewById(R.id.collections_recycler);
        mCollectionsRV.setLayoutManager(new LinearLayoutManager(mContext));
        mCollectionsRV.setHasFixedSize(true);
        mNoticiasAdapter.mColType = BookDetailsActivity.COLECCIONES;
        mCollectionsRV.setAdapter(mNoticiasAdapter);

        // show new collections on start
        if(mNoticiasList.isEmpty()) {
            loadNoticias();
        }
    }

    private void loadNoticias() {
        ApiSingleton.showProgressFlower(NoticiasActivity.this);

        ApiSingleton.getInstance(mContext)
                .addToRequestQueue(new JsonArrayRequest(ApiConfig.noticias + "?page=1",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        Type noticiasType = new TypeToken<List<Noticia>>(){}.getType();
                        Gson gson = new Gson();
                        mNoticiasList.clear();
                        mNoticiasList.addAll((List<Noticia>) gson.fromJson(response.toString(), noticiasType));
                        mNoticiasAdapter.notifyDataSetChanged();
                        ApiSingleton.hideProgressFlower();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ApiSingleton.hideProgressFlower();
                        Toast.makeText(mContext, "Sin conexión", Toast.LENGTH_SHORT).show();
                        Log.d("getNoticias", "error");
                    }
                }
        ));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
        }
    }
}
