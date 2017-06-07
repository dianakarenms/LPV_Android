package com.clickaboom.letrasparavolar.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.adapters.RecommendedAdapter;
import com.clickaboom.letrasparavolar.models.collections.Colecciones;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karencita on 15/05/2017.
 */

public class BookDetailsActivity extends AppCompatActivity {

    private static final String EXTRA_ITEM = "com.lpv.item";
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private RecommendedAdapter mAdapter;
    private List<Colecciones> mBooksList = new ArrayList<>();
    private String params;
    private Context mContext;

    public static Intent newIntent(Context packageContext, Colecciones item) {
        Intent i = new Intent(packageContext, BookDetailsActivity.class);
        i.putExtra(EXTRA_ITEM, item);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_book_detail);

        mContext = this;

        Colecciones item = (Colecciones) getIntent().getSerializableExtra(EXTRA_ITEM);

        // Set toolbar_asistant title
        findViewById(R.id.toolbar_asistant).setVisibility(View.GONE);

        // use a linear layout manager
        mGridLayoutManager = new GridLayoutManager(mContext, 3);
        mRecyclerView = (RecyclerView) findViewById(R.id.related_recycler);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new RecommendedAdapter(mBooksList, mContext);
        mRecyclerView.setAdapter(mAdapter);

        // show new collections on start
        if(!item.librosRelacionados.isEmpty()) {
            mBooksList.addAll(item.librosRelacionados);
            mAdapter.notifyDataSetChanged();
        }


        String imgUrl = ApiConfig.collectionsImg + item.imagenes.get(0).imagen;
        ImageView image = (ImageView) findViewById(R.id.book_img);
        Picasso.with(mContext)
                .load(imgUrl)
                .resize(300,300)
                .centerInside()
                .into(image);

        ((TextView)findViewById(R.id.title_txt)).setText(item.titulo);

        // Subtitle
        List<String> autoresList = new ArrayList<>();
        for(int i = 0; i<item.autores.size(); i++) {
            autoresList.add(item.autores.get(i).autor);
        }

        ((TextView)findViewById(R.id.subtitle_txt)).setText(MainActivity.getStringFromListByCommas(autoresList));
        ((TextView)findViewById(R.id.date_title_txt)).setText(item.fecha);
        ((TextView)findViewById(R.id.description_txt)).setText(item.descripcion);
    }

}
