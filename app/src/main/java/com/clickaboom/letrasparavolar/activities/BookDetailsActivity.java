package com.clickaboom.letrasparavolar.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.clickaboom.letrasparavolar.activities.MainActivity.db;
import static com.clickaboom.letrasparavolar.activities.MainActivity.getStringFromListByCommas;

/**
 * Created by Karencita on 15/05/2017.
 */

public class BookDetailsActivity extends AppCompatActivity {

    private static final String EXTRA_ITEMID = "com.lpv.item";
    public static final String COLECCIONES = "colecciones";
    public static final String LEGENDS = "leyendas";
    private static final String EXTRA_COL_TYPE = "com.lpv.mColType";
    private static final String STATE_BOOKS_LIST = "com.lpv.mBooksList";

    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private RecommendedAdapter mAdapter;
    private List<Colecciones> mBooksList = new ArrayList<>();
    private String params;
    private Context mContext;
    private String TAG = "com.lpv.bookDetails";
    private String mColType;
    private int mItemId;
    private ImageView starBtn;

    public static Intent newIntent(Context packageContext, int id, String colType) {
        Intent i = new Intent(packageContext, BookDetailsActivity.class);
        i.putExtra(EXTRA_ITEMID, id);
        i.putExtra(EXTRA_COL_TYPE, colType);
        return i;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Avoid setting a null mBookList in mAdapter
        mAdapter.setList(mBooksList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        mContext = this;

        mItemId = getIntent().getIntExtra(EXTRA_ITEMID, 0);
        mColType = getIntent().getStringExtra(EXTRA_COL_TYPE);
        String url = "";
        if(mColType.equals(LEGENDS))
            url = ApiConfig.legends;
        else
            url = ApiConfig.collections;

        // Load Book Data
        loadItem(url, mItemId);

        // Set toolbar_asistant title
        findViewById(R.id.toolbar_asistant).setVisibility(View.GONE);

        // use a linear layout manager
        mGridLayoutManager = new GridLayoutManager(mContext, 3);
        mRecyclerView = (RecyclerView) findViewById(R.id.related_recycler);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new RecommendedAdapter(mBooksList, mContext);
        mAdapter.mColType = mColType;
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

                                updateUI(item);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                        ArrayList<Colecciones> book = db.getBookById(String.valueOf(mItemId), mColType);
                        if(book.isEmpty())
                            Toast.makeText(mContext, "Error de conexión", Toast.LENGTH_SHORT).show();
                        else {
                            updateUI(book.get(0));
                        }
                    }
                }));

    }

    private void updateUI(final Colecciones item) {
        // Item type for storing it in db
        item.type = mColType;
        item.favorito = false;
        final ArrayList<Colecciones> localItem = db.getBookByePub(item.epub);

        starBtn = (ImageView) findViewById(R.id.favorite_star);

        if(!localItem.isEmpty()) {
            ((TextView) findViewById(R.id.downloadBtn)).setText(getResources().getString(R.string.open));
            if(localItem.get(0).favorito) {
                starBtn.setBackgroundColor(getResources().getColor(R.color.legends_nav_pressed));
            }
        } else
            ((TextView)findViewById(R.id.downloadBtn)).setText(getResources().getString(R.string.download));

        // Book title info
        ((TextView)findViewById(R.id.title_txt)).setText(item.titulo);
        String imgUrl = ApiConfig.collectionsImg + item.imagenes.get(0).imagen;
        item.imagen = item.imagenes.get(0).imagen;
        ImageView image = (ImageView) findViewById(R.id.book_img);
        Picasso.with(mContext)
                .load(imgUrl)
                .resize(300,300)
                .centerInside()
                .into(image);
        ((TextView)findViewById(R.id.date_title_txt)).setText(item.fecha);

        // Subtitle Authors
        List<String> autoresList = new ArrayList<>();
        for(int i = 0; i<item.autores.size(); i++) {
            autoresList.add(item.autores.get(i).autor);
        }
        ((TextView)findViewById(R.id.subtitle_txt)).setText(getStringFromListByCommas(autoresList));
        ((TextView)findViewById(R.id.date_title_txt)).setText(item.fecha);

        // Category Image
        if(!item.categorias.isEmpty()) {
            imgUrl = ApiConfig.catImgPath + item.categorias.get(0).icono;
            image = (ImageView) findViewById(R.id.category_img);
            Picasso.with(mContext)
                    .load(imgUrl)
                    .resize(300, 300)
                    .centerInside()
                    .into(image);
            ((TextView)findViewById(R.id.category_name)).setText(item.categorias.get(0).categoria);
            ((TextView)findViewById(R.id.category_txt)).setText(item.categorias.get(0).categoria);
        }

        // Book extra info
        ((TextView)findViewById(R.id.description_txt)).setText(item.descripcion);
        ((TextView)findViewById(R.id.idiom_txt)).setText("Español");
        ((TextView)findViewById(R.id.publisher_txt)).setText(item.editorial);
        ((TextView)findViewById(R.id.date_txt)).setText(item.fecha);
        ((TextView)findViewById(R.id.size_txt)).setText(item.length + " MB");
        ((TextView)findViewById(R.id.pages_txt)).setText(item.length);

        // Tags
        List<String> tagsList = new ArrayList<>();
        for(int i = 0; i<item.etiquetas.size(); i++) {
            autoresList.add(item.etiquetas.get(i).etiqueta);
        }
        ((TextView)findViewById(R.id.tags_txt)).setText(getStringFromListByCommas(tagsList));

        // show new collections on start
        if(!item.librosRelacionados.isEmpty()) {
            mBooksList.addAll(item.librosRelacionados);
            mAdapter.notifyDataSetChanged();
        }

        // add book to favorites
        findViewById(R.id.add_to_favorites_llay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!localItem.isEmpty()) {
                    if(db.updateFavBook(item.epub, localItem.get(0).favorito ? 0 : 1))
                        starBtn.setBackgroundColor(getResources().getColor(R.color.legends_nav_pressed));
                    else
                        starBtn.setBackgroundColor(Color.TRANSPARENT);
                } else
                    Toast.makeText(mContext, "Descárgalo para poder marcarlo como favorito", Toast.LENGTH_SHORT).show();
            }
        });

        // Download Button
        findViewById(R.id.downloadBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(EpubBookContentActivity2.newIntent(mContext, item));
                                        /*// read epub
                                        try {
                                            EpubReader epubReader = new EpubReader();
                                            AssetManager am = mContext.getAssets();
                                            InputStream is = am.open("epubs/el_callejon.epub");
                                            //nl.siegmann.epublib.domain.Book book = epubReader.readEpub(new FileInputStream("/assets/"));
                                            Book book = epubReader.readEpub(is);
                                            //nl.siegmann.epublib.domain.Book book = epubReader.readEpub(is);
                                            book.getMetadata().setTitles(new ArrayList<String>() {{
                                                add("an awesome book");
                                            }});


                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }*/
            }
        });
    }
}
