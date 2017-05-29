package com.clickaboom.letrasparavolar.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import com.clickaboom.letrasparavolar.adapters.CategoriesAdapter;
import com.clickaboom.letrasparavolar.adapters.CollectionsAdapter;
import com.clickaboom.letrasparavolar.models.Book;
import com.clickaboom.letrasparavolar.models.collections.by_category.Collections;
import com.clickaboom.letrasparavolar.models.collections.by_category.ResCollections;
import com.clickaboom.letrasparavolar.models.collections.categories.Categoria;
import com.clickaboom.letrasparavolar.models.collections.categories.ResCategories;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.clickaboom.letrasparavolar.network.GsonRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karencita on 15/05/2017.
 */

public class CollectionsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "com.lpv.collecByOrder";
    private Book mBook;
    private RecyclerView mCategoriesRV, mCollectionsRV;
    private CollectionsAdapter mCollectionsAdapter;
    private List<Collections> mCollectionsList;
    private List<Categoria> mCategoriesList;
    private View v;
    private String url = "", params = "", mImgPath;
    private CategoriesAdapter mCategoriesAdapter;

    public CollectionsFragment() {
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
            mCollectionsList = (List<Collections>) savedInstanceState.getSerializable("collections");
            mCategoriesAdapter.notifyDataSetChanged();
            mCollectionsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_collections, container, false);

        Toolbar toolbar = (Toolbar)v.findViewById(R.id.toolbar);
        ((MainActivity) getContext()).setSupportActionBar(toolbar);

        // Set toolbar_asistant title
        ((TextView)v.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.collections_title));
        v.findViewById(R.id.left_btn).setVisibility(View.VISIBLE);
        v.findViewById(R.id.right_bnt).setVisibility(View.VISIBLE);

        // Categories Recycler View
        mCategoriesRV = (RecyclerView) v.findViewById(R.id.categories_recycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mCategoriesRV.setLayoutManager(mLayoutManager);
        mCategoriesAdapter = new CategoriesAdapter(mCategoriesList, mImgPath, getContext(), new CategoriesAdapter.RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(String categoryId) {
                url = ApiConfig.collectionByCategory;
                params = "?categoria=" + categoryId;
                loadCollecByCategory();
                restoreOrderColors();
            }
        });
        mCategoriesRV.setAdapter(mCategoriesAdapter);
        if(mCategoriesList.isEmpty() || mImgPath.isEmpty()) {
            loadCategories();
        }

        // Collections RecyclerView
        mCollectionsRV = (RecyclerView) v.findViewById(R.id.collections_recycler);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mCollectionsRV.setLayoutManager(mGridLayoutManager);
        mCollectionsAdapter = new CollectionsAdapter(mCollectionsList, getContext());
        mCollectionsRV.setAdapter(mCollectionsAdapter);

        // show new collecByOrder on start
        if(mCollectionsList.isEmpty()) {
            url = ApiConfig.collecByOrder;
            params = "?order=nuevas";
            loadCollecByCategory();
        }

        // Order collecByOrder
        v.findViewById(R.id.news_txt).setOnClickListener(this);
        v.findViewById(R.id.top_txt).setOnClickListener(this);
        v.findViewById(R.id.month_theme_txt).setOnClickListener(this);
        v.findViewById(R.id.all_txt).setOnClickListener(this);

        return v;
    }

    private void loadCategories() {
        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(getActivity())
                .addToRequestQueue(new GsonRequest(ApiConfig.collectionsCategories,
                        ResCategories.class,
                        Request.Method.GET,
                        null, null,
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                Log.d(TAG, response.toString());
                                mCategoriesList.clear();
                                List<List<Categoria>> res = ((ResCategories) response).data;
                                for(List<Categoria> categorias : res) {
                                    mCategoriesList.addAll(categorias); // Add main book to list
                                }
                                mImgPath = ((ResCategories) response).pathIconos + "/";
                                mCategoriesAdapter.setImgPath(mImgPath);
                                mCategoriesAdapter.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }));

    }

    private void loadCollecByCategory() {

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
                                List<List<Collections>> res = ((ResCollections) response).data;

                                mCollectionsList.clear();
                                for(List<Collections> item : res) {
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
        url = ApiConfig.collecByOrder;
        switch (v.getId()) {
            case R.id.news_txt:
                params = "?order=nuevas";
            case R.id.top_txt:
                params = "?order=populares";
                break;
            case R.id.month_theme_txt:
                params = "?order=temadelmes";
                break;
            case R.id.all_txt:
                params = "";
                break;

        }
        v.setBackgroundColor(getResources().getColor(R.color.order_back_pressed));
        loadCollecByCategory();
        mCategoriesAdapter.clearActive();
        mCategoriesAdapter.notifyDataSetChanged();
    }

    private void restoreOrderColors() {
        v.findViewById(R.id.news_txt).setBackgroundColor(getResources().getColor(R.color.order_back));
        v.findViewById(R.id.top_txt).setBackgroundColor(getResources().getColor(R.color.order_back));
        v.findViewById(R.id.month_theme_txt).setBackgroundColor(getResources().getColor(R.color.order_back));
        v.findViewById(R.id.all_txt).setBackgroundColor(getResources().getColor(R.color.order_back));
    }
}
