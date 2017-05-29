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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.activities.MainActivity;
import com.clickaboom.letrasparavolar.adapters.CollectionsAdapter;
import com.clickaboom.letrasparavolar.models.Book;
import com.clickaboom.letrasparavolar.models.collections.by_category.Collections;
import com.clickaboom.letrasparavolar.models.collections.by_category.ResCollections;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.clickaboom.letrasparavolar.network.GsonRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Karencita on 15/05/2017.
 */

public class LegendsFragment extends Fragment {

    private static final String TAG = "com.lpv.legends";
    private Book mBook;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private CollectionsAdapter mAdapter;
    private List<Collections> mBooksList = new ArrayList<>();

    public LegendsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBook = new Book();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_legends, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.legends_recycler);

        Toolbar toolbar = (Toolbar)v.findViewById(R.id.toolbar);
        ((MainActivity) getContext()).setSupportActionBar(toolbar);

        // Set toolbar_asistant title
        ((TextView)v.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.legends_title));
        v.findViewById(R.id.left_btn).setVisibility(View.VISIBLE);
        v.findViewById(R.id.right_bnt).setVisibility(View.VISIBLE);

        // use a linear layout manager
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new CollectionsAdapter(mBooksList, getContext());
        mRecyclerView.setAdapter(mAdapter);

        if(mBooksList.isEmpty())
            loadBooks();
        return v;
    }

    private void loadBooks() {
        Map<String, String> params = new HashMap<>();
        //params.put("order", "nuevas");

        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(getActivity())
                .addToRequestQueue(new GsonRequest(ApiConfig.collecByOrder,
                        ResCollections.class,
                        Request.Method.GET,
                        null, params,
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                Log.d(TAG, response.toString());
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