package com.clickaboom.letrasparavolar.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.GravityCompat;
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
import static com.clickaboom.letrasparavolar.activities.MainActivity.mIntentBook;
import static com.clickaboom.letrasparavolar.network.DownloadFile.isStoragePermissionGranted;

/**
 * Created by Karencita on 15/05/2017.
 */

public class BookDetailsActivity extends AppCompatActivity {

    private static final String EXTRA_ITEMID = "com.lpv.mItem";
    public static final String COLECCIONES = "colecciones";
    public static final String LEGENDS = "leyendas";
    private static final String EXTRA_COL_TYPE = "com.lpv.mColType";
    private static final String STATE_BOOKS_LIST = "com.lpv.mBooksList";
    public static final int REQUEST_DOWNLOAD = 20;

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
    private Colecciones mItem;

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

        // Menu drawer onclicklistener
        findViewById(R.id.drawer_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open right drawer
                MainActivity.drawer.openDrawer(GravityCompat.END);
            }
        });

        // Set toolbar_asistant gone
        findViewById(R.id.toolbar_asistant).setVisibility(View.GONE);
        findViewById(R.id.toolbar).findViewById(R.id.drawer_button).setVisibility(View.GONE);

        // BackBtn
        LinearLayout backBtn = (LinearLayout) findViewById(R.id.back_btn);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

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
                                mItem = res.get(0);
                                if(mItem.epub != null) {
                                    updateUI();
                                } else {
                                    Toast.makeText(mContext, "Este libro no tiene ePub", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                        ArrayList<Colecciones> book = db.getBookById(String.valueOf(mItemId), mColType);
                        if(book.isEmpty())
                            Toast.makeText(mContext, "Error de conexión", Toast.LENGTH_SHORT).show();
                        else {
                            mItem = book.get(0);
                            if(mItem.epub != null) {
                            updateUI();} else {
                                Toast.makeText(mContext, "Este libro no tiene ePub", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }
                }));

    }

    private void updateUI() {
        try {
            // Item type for storing it in db
            mItem.mBookType = mColType;
//        mItem.favorito = false;
//        mItem.descargado = false;

            starBtn = (ImageView) findViewById(R.id.favorite_star);

            // If item is stored on db...
            ArrayList<Colecciones> localItem = new ArrayList<>();
            if(mItem.epub != null) {
                localItem = db.getBookByePub(mItem.epub);
                if (!localItem.isEmpty()) {
                    if (localItem.get(0).descargado) {
                        ((TextView) findViewById(R.id.downloadBtn)).setText(getResources().getString(R.string.open));
                    }
                    if (localItem.get(0).favorito) {
                        starBtn.setImageResource(R.drawable.favorite_selected);
                    }
                } else
                    ((TextView) findViewById(R.id.downloadBtn)).setText(getResources().getString(R.string.download));

            }

            // Book title info
            if(mItem.titulo != null)
                ((TextView) findViewById(R.id.title_txt)).setText(mItem.titulo);

            // Book cover image
            if (!mItem.imagenes.isEmpty())
                mItem.imagen = mItem.imagenes.get(0).imagen;

            String imgUrl = ApiConfig.collectionsImg + mItem.imagen;
            ImageView image = (ImageView) findViewById(R.id.book_img);
            Picasso.with(mContext)
                    .load(imgUrl)
                    .resize(300, 300)
                    .centerInside()
                    .into(image);
            ((TextView) findViewById(R.id.date_title_txt)).setText(mItem.fecha);

            // Subtitle Authors
            if(mItem.autores != null) {
                List<String> autoresList = new ArrayList<>();
                for (int i = 0; i < mItem.autores.size(); i++) {
                    autoresList.add(mItem.autores.get(i).autor);
                    ((TextView) findViewById(R.id.subtitle_txt)).setText(getStringFromListByCommas(autoresList));
                }
            }

            // Date label
            if(mItem.fecha != null) {
                ((TextView) findViewById(R.id.date_title_txt)).setText(mItem.fecha);
                // Extra info date label
                ((TextView) findViewById(R.id.date_txt)).setText(mItem.fecha);
            }

            // Category Image
            if (!mItem.categorias.isEmpty()) {
                imgUrl = ApiConfig.catImgPath + mItem.categorias.get(0).icono;
                image = (ImageView) findViewById(R.id.category_img);
                Picasso.with(mContext)
                        .load(imgUrl)
                        .resize(300, 300)
                        .centerInside()
                        .into(image);
                ((TextView) findViewById(R.id.category_name)).setText(mItem.categorias.get(0).categoria);
                ((TextView) findViewById(R.id.category_txt)).setText(mItem.categorias.get(0).categoria);
            }


            // Book extra info
            if(mItem.descripcion != null)
                ((TextView) findViewById(R.id.description_txt)).setText(mItem.descripcion);
            ((TextView) findViewById(R.id.idiom_txt)).setText("Español");
            if(mItem.editorial != null)
                ((TextView) findViewById(R.id.publisher_txt)).setText(mItem.editorial);
            if(mItem.length != null)
                ((TextView) findViewById(R.id.size_txt)).setText(mItem.length + " MB");
            if(mItem.length != null)
                ((TextView) findViewById(R.id.pages_txt)).setText(mItem.length);

            // Tags
            List<String> tagsList = new ArrayList<>();
            for (int i = 0; i < mItem.etiquetas.size(); i++) {
                tagsList.add(mItem.etiquetas.get(i).etiqueta);
            }
            ((TextView) findViewById(R.id.tags_txt)).setText(getStringFromListByCommas(tagsList));

            // show new collections on start
            if (!mItem.librosRelacionados.isEmpty()) {
                mBooksList.addAll(mItem.librosRelacionados);
                mAdapter.notifyDataSetChanged();
            }

            // add book to favorites
            final ArrayList<Colecciones> finalLocalItem = localItem;
            findViewById(R.id.add_to_favorites_llay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!finalLocalItem.isEmpty()) {
                        if (finalLocalItem.get(0).favorito) {
                            db.updateFavBook(mItem.epub, 0);
                            finalLocalItem.get(0).favorito = false;
                            starBtn.setImageResource(R.drawable.favorite_unselected);
                        } else {
                            db.updateFavBook(mItem.epub, 1);
                            finalLocalItem.get(0).favorito = true;
                            starBtn.setImageResource(R.drawable.favorite_selected);
                        }

                    /*if(db.updateFavBook(mItem.epub, localItem.get(0).favorito ? 0 : 1))
                        starBtn.setImageResource(R.drawable.favorite_selected);
                    else {
                        db.updateFavBook(mItem.epub, localItem.get(0).favorito ? 0 : 1);
                        starBtn.setImageResource(R.drawable.favorite_unselected);
                    }*/
                    } else
                        Toast.makeText(mContext, "Descárgalo para poder marcarlo como favorito", Toast.LENGTH_SHORT).show();
                }
            });

            // Download Button
            findViewById(R.id.downloadBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isStoragePermissionGranted(BookDetailsActivity.this)) {
                        startActivityForResult(EpubBookContentActivity.newIntent(mContext, mItem), REQUEST_DOWNLOAD);
                    }

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

            if (mIntentBook != null && mIntentBook.id == mItemId) {
                mIntentBook = null;
                findViewById(R.id.downloadBtn).performClick();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
//        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_DOWNLOAD:
                if(resultCode == RESULT_OK) {
                    updateUI();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);

            Toast.makeText(mContext, "Habilitando permisos...", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Restart the app since in order to change the ID the process
                    // has to be restarted. Next time you open the app,
                    // the new groupID is set and the permission is granted.
                    PackageManager packageManager = getPackageManager();
                    Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
                    ComponentName componentName = intent.getComponent();
                    Intent mainIntent = IntentCompat.makeRestartActivityTask(componentName);
                    mainIntent.putExtra(MainActivity.EXTRA_BOOK_ITEM, mItem);
                    startActivity(mainIntent);
                    System.exit(0);
                }
            }, 2000);

        } else {
            Toast.makeText(mContext,
                    "Habilite permiso de \"almacenamiento local\" para visualizar los epubs",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
