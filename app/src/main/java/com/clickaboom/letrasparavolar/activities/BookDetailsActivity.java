package com.clickaboom.letrasparavolar.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.adapters.RecommendedAdapter;
import com.clickaboom.letrasparavolar.models.collections.Colecciones;
import com.clickaboom.letrasparavolar.models.defaults.ResDefaults;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.clickaboom.letrasparavolar.network.GsonRequest;
import com.google.android.gms.common.api.Api;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.clickaboom.letrasparavolar.activities.MapsActivity.EXTRA_MAP_TYPE;

/**
 * Created by Karencita on 15/05/2017.
 */

public class BookDetailsActivity extends AppCompatActivity {

    private static final String EXTRA_ITEMID = "com.lpv.item";
    public static final String COLECCIONES = "colecciones";
    public static final String LEGENDS = "leyendas";
    private static final String EXTRA_COL_TYPE = "com.lpv.colType";

    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private RecommendedAdapter mAdapter;
    private List<Colecciones> mBooksList = new ArrayList<>();
    private String params;
    private Context mContext;
    private String TAG = "com.lpv.bookDetails";

    public static Intent newIntent(Context packageContext, int id, String colType) {
        Intent i = new Intent(packageContext, BookDetailsActivity.class);
        i.putExtra(EXTRA_ITEMID, id);
        i.putExtra(EXTRA_COL_TYPE, colType);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_book_detail);

        mContext = this;

        int itemId = getIntent().getIntExtra(EXTRA_ITEMID, 0);
        String colType = getIntent().getStringExtra(EXTRA_COL_TYPE);
        String url = "";
        if(colType.equals(LEGENDS))
            url = ApiConfig.legends;
        else
            url = ApiConfig.collections;

        // Load Book Data
        loadItem(url, itemId);

        // Set toolbar_asistant title
        findViewById(R.id.toolbar_asistant).setVisibility(View.GONE);

        // use a linear layout manager
        mGridLayoutManager = new GridLayoutManager(mContext, 3);
        mRecyclerView = (RecyclerView) findViewById(R.id.related_recycler);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new RecommendedAdapter(mBooksList, mContext);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadItem(String url, int id) {
        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(mContext)
                .addToRequestQueue(new GsonRequest(url + "?id=" + id,
                        ResDefaults.class,
                        Request.Method.GET,
                        null, null,
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                Log.d(TAG, response.toString());
                                List<Colecciones> res = ((ResDefaults) response).data;
                                Colecciones item = res.get(0);

                                // Book title info
                                ((TextView)findViewById(R.id.title_txt)).setText(item.titulo);
                                String imgUrl = ApiConfig.collectionsImg + item.imagenes.get(0).imagen;
                                ImageView image = (ImageView) findViewById(R.id.book_img);
                                Picasso.with(mContext)
                                        .load(imgUrl)
                                        .resize(300,300)
                                        .centerInside()
                                        .into(image);

                                // Subtitle Authors
                                List<String> autoresList = new ArrayList<>();
                                for(int i = 0; i<item.autores.size(); i++) {
                                    autoresList.add(item.autores.get(i).autor);
                                }

                                // Book extra info
                                ((TextView)findViewById(R.id.subtitle_txt)).setText(MainActivity.getStringFromListByCommas(autoresList));
                                ((TextView)findViewById(R.id.date_title_txt)).setText(item.fecha);
                                ((TextView)findViewById(R.id.description_txt)).setText(item.descripcion);

                                // show new collections on start
                                if(!item.librosRelacionados.isEmpty()) {
                                    mBooksList.addAll(item.librosRelacionados);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }));

    }
}
