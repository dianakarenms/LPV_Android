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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.activities.MainActivity;
import com.clickaboom.letrasparavolar.adapters.CollectionsAdapter;
import com.clickaboom.letrasparavolar.models.Book;
import com.clickaboom.letrasparavolar.models.Collections;
import com.clickaboom.letrasparavolar.models.ResCollections;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.clickaboom.letrasparavolar.network.GsonRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public BookDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBook = new Book();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book_detail, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.related_recycler);

        Toolbar toolbar = (Toolbar)v.findViewById(R.id.toolbar);
        ((MainActivity) getContext()).setSupportActionBar(toolbar);

        // use a linear layout manager
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new CollectionsAdapter(mBooksList, getContext());
        mRecyclerView.setAdapter(mAdapter);

        loadBooks();
        return v;
    }

    private void loadBooks() {
        Map<String, String> params = new HashMap<>();
        params.put("order", "nuevas");

        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(getActivity())
                .addToRequestQueue(new GsonRequest(ApiConfig.collections,
                        ResCollections.class,
                        Request.Method.GET,
                        null, params,
                        new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Log.d(TAG, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        }));

    }
}
