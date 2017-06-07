package com.clickaboom.letrasparavolar.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.clickaboom.letrasparavolar.adapters.CategoriesAdapter;
import com.clickaboom.letrasparavolar.adapters.CollectionsAdapter;
import com.clickaboom.letrasparavolar.models.Book;
import com.clickaboom.letrasparavolar.models.collections.Colecciones;
import com.clickaboom.letrasparavolar.models.collections.ResCollections;
import com.clickaboom.letrasparavolar.models.collections.categories.Categoria;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.clickaboom.letrasparavolar.network.GsonRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Karencita on 15/05/2017.
 */

public class LibraryFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "com.lpv.collections";
    public static final int REQUEST_SEARCH = 0;
    public static final String RESULT_SEARCH = "searchText";
    private Book mBook;
    private RecyclerView mCategoriesRV, mCollectionsRV;
    private CollectionsAdapter mCollectionsAdapter;
    private List<Colecciones> mCollectionsList;
    private List<Categoria> mCategoriesList;
    private View v;
    private String url = "", params = "", mImgPath;
    private CategoriesAdapter mCategoriesAdapter;

    public LibraryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        mBook = new Book();
        mCollectionsList = new ArrayList<>();
        mCategoriesList = new ArrayList<>();
        mImgPath = "";
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("categories", (Serializable) mCategoriesList);
        outState.putSerializable("collections", (Serializable) mCollectionsList);
        outState.putString("imgPath", mImgPath);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            mImgPath = savedInstanceState.getString("imgPath");
            mCategoriesList = (List<Categoria>) savedInstanceState.getSerializable("categories");
            mCollectionsList = (List<Colecciones>) savedInstanceState.getSerializable("collections");
            mCategoriesAdapter.notifyDataSetChanged();
            mCollectionsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_library, container, false);

        // Set its bottomNavButton clicked
        ((MainActivity)getActivity()).restoreBottonNavColors();
        ((MainActivity)getActivity()).libraryBtn.
                setBackgroundColor(getResources().getColor(R.color.bottom_nav_pressed));

        // Set toolbar_asistant title
        ((TextView)v.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.library_title));
        v.findViewById(R.id.left_btn).setVisibility(View.GONE);
        v.findViewById(R.id.right_btn).setVisibility(View.GONE);

        // Order collections
        v.findViewById(R.id.information_txt).setOnClickListener(this);
        v.findViewById(R.id.international_txt).setOnClickListener(this);

        // Categories Recycler View
        mCategoriesRV = (RecyclerView) v.findViewById(R.id.readings_recycler);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mCategoriesRV.setLayoutManager(mGridLayoutManager);
        /*mCategoriesAdapter = new CategoriesAdapter(mCategoriesList, mImgPath, getContext(), new CategoriesAdapter.RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(String categoryId) {
                url = ApiConfig.collectionByCategory;
                params = "?categoria=" + categoryId;
                loadCollections(url, params);
                restoreOrderColors();
            }
        });
        mCategoriesRV.setAdapter(mCategoriesAdapter);*/


        // Collections RecyclerView
        mCollectionsRV = (RecyclerView) v.findViewById(R.id.books_recycler);
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mCollectionsRV.setLayoutManager(mGridLayoutManager);
        mCollectionsAdapter = new CollectionsAdapter(mCollectionsList, getContext());
        mCollectionsRV.setAdapter(mCollectionsAdapter);
        mCategoriesRV.setAdapter(mCollectionsAdapter);

        // show new collections on start
        if(mCollectionsList.isEmpty()) {
            url = ApiConfig.searchCollections;
            params = "?categoria=" + "13";
            loadCollections(url, params);
        }

        return v;
    }

    private void loadCollections(String url, String params) {

        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(getActivity())
                .addToRequestQueue(new GsonRequest(url + params,
                        ResCollections.class,
                        Request.Method.GET,
                        null, null,
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                Log.d(TAG, response.toString());
                                List<List<Colecciones>> res = ((ResCollections) response).data;

                                mCollectionsList.clear();
                                for(List<Colecciones> item : res) {
                                    mCollectionsList.addAll(item); // Add main book to list
                                }
                                mCollectionsAdapter.notifyDataSetChanged();
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
        restoreOrderColors();
        url = ApiConfig.searchCollections;
        switch (v.getId()) {
            case R.id.information_txt:
                params = "?categoria=" + "13";
                break;
            case R.id.international_txt:
                params = "?categoria=" + "15";
                break;

        }
        v.setBackgroundColor(getResources().getColor(R.color.order_back_pressed));
        loadCollections(url, params);
        //mCategoriesAdapter.clearActive();
        //mCategoriesAdapter.notifyDataSetChanged();
    }

    private void restoreOrderColors() {
        v.findViewById(R.id.information_txt).setBackground(getResources().getDrawable(R.drawable.nav_subcategories_button));
        v.findViewById(R.id.international_txt).setBackground(getResources().getDrawable(R.drawable.nav_subcategories_button));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SEARCH:
                if(resultCode == RESULT_OK) {
                    loadCollections(ApiConfig.searchCollections, "?q=" + data.getStringExtra(RESULT_SEARCH));
                }
                break;
        }
    }
}
