package com.clickaboom.letrasparavolar.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.activities.MainActivity;
import com.clickaboom.letrasparavolar.adapters.CollectionsAdapter;
import com.clickaboom.letrasparavolar.models.Book;
import com.clickaboom.letrasparavolar.models.collections.Collections;
import com.clickaboom.letrasparavolar.models.collections.ResCollections;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.clickaboom.letrasparavolar.network.GsonRequest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Karencita on 15/05/2017.
 */

public class BookDetailsFragment extends Fragment {

    private Book mBook;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private CollectionsAdapter mAdapter;
    private List<Collections> mBooksList = new ArrayList<>();
    private String params;
    private ImageLoader imageLoader;

    public static BookDetailsFragment newInstance(Collections item) {
        BookDetailsFragment myFragment = new BookDetailsFragment();

        Bundle args = new Bundle();
        args.putSerializable("item", item);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBook = new Book();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book_detail, container, false);
        Collections item = (Collections) getArguments().getSerializable("item");

        Toolbar toolbar = (Toolbar)v.findViewById(R.id.toolbar);
        ((MainActivity) getContext()).setSupportActionBar(toolbar);

        // use a linear layout manager
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.related_recycler);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new CollectionsAdapter(mBooksList, getContext());
        mRecyclerView.setAdapter(mAdapter);

        // show new collecByOrder on start
        if(mBooksList.isEmpty()) {
            params = "?order=nuevas";
            loadBooks();
        }


        String imgUrl = ApiConfig.collectionsImg + item.imagen;
        /*NetworkImageView image = (NetworkImageView) v.findViewById(R.id.book_img);
        image.setDefaultImageResId(R.drawable.book_placeholder);
        imageLoader = ApiSingleton.getInstance(getContext()).getImageLoader();
        imageLoader.get(imgUrl, ImageLoader.getImageListener(image, R.drawable.book_placeholder, android.R.drawable.ic_dialog_alert));
        image.setImageUrl(imgUrl, imageLoader);*/
        ImageView image = (ImageView) v.findViewById(R.id.book_img);
        Picasso.with(getContext())
                .load(imgUrl)
                .resize(300,300)
                .centerInside()
                .into(image);

        ((TextView)v.findViewById(R.id.title_txt)).setText(item.titulo);

        // Subtitle
        List<String> autoresList = new ArrayList<>();
        for(int i = 0; i<item.autores.size(); i++) {
            autoresList.add(item.autores.get(i).autor);
        }

        ((TextView)v.findViewById(R.id.subtitle_txt)).setText(MainActivity.getStringFromListByCommas(autoresList));
        ((TextView)v.findViewById(R.id.date_title_txt)).setText(item.fecha);
        ((TextView)v.findViewById(R.id.description_txt)).setText(item.descripcion);

        return v;
    }

    private void loadBooks() {
        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(getActivity())
                .addToRequestQueue(new GsonRequest(ApiConfig.collecByOrder + params,
                        ResCollections.class,
                        Request.Method.GET,
                        null, null,
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                Log.d(TAG, response.toString());
                                mBooksList.clear();
                                List<List<Collections>> res = ((ResCollections) response).data;
                                //for(int i = 0; i<res.size(); i ++) {
                                    mBooksList.addAll(res.get(0));
                                //}

                                mAdapter.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }));
    }


}
