package com.clickaboom.letrasparavolar.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import com.clickaboom.letrasparavolar.activities.BookDetailsActivity;
import com.clickaboom.letrasparavolar.activities.MainActivity;
import com.clickaboom.letrasparavolar.activities.MapsActivity;
import com.clickaboom.letrasparavolar.activities.SearchActivity;
import com.clickaboom.letrasparavolar.adapters.CategoriesAdapter;
import com.clickaboom.letrasparavolar.adapters.CollectionsAdapter;
import com.clickaboom.letrasparavolar.models.collections.Colecciones;
import com.clickaboom.letrasparavolar.models.collections.ResCollections;
import com.clickaboom.letrasparavolar.models.collections.categories.Categoria;
import com.clickaboom.letrasparavolar.models.collections.categories.ResCategories;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.clickaboom.letrasparavolar.network.GsonRequest;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Karencita on 15/05/2017.
 */

public class CollectionsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "com.lpv.collections";
    public static final int REQUEST_SEARCH = 0;
    public static final String RESULT_SEARCH = "searchText";
    private static final String LIST_STATE_KEY = "listState";
    private RecyclerView mCategoriesRV, mCollectionsRV;
    private CollectionsAdapter mCollectionsAdapter;
    private List<Colecciones> mCollectionsList;
    private List<Categoria> mCategoriesList;
    private View v;
    private String url = "", params = "", mImgPath;
    private CategoriesAdapter mCategoriesAdapter;
    private GridLayoutManager mGridLayoutManager;
    private Parcelable mListState;
    private NestedScrollView mNestedScroll;

    public static CollectionsFragment newInstance() {
        CollectionsFragment fragment = new CollectionsFragment();
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        mListState = mGridLayoutManager.onSaveInstanceState();
//        outState.putParcelable(LIST_STATE_KEY, mListState);
        /*outState.putSerializable("categories", (Serializable) mCategoriesList);
        outState.putSerializable("collections", (Serializable) mCollectionsList);
        outState.putString("imgPath", mImgPath);*/

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d("res", "restored");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_collections, container, false);

        // Set its bottomNavButton clicked
        ((MainActivity)getActivity()).restoreBottonNavColors();
        ((MainActivity)getActivity()).collectionsBtn.
                setBackgroundColor(getResources().getColor(R.color.bottom_nav_pressed));

        mNestedScroll = (NestedScrollView) v.findViewById(R.id.nested_scroll);
        mCollectionsList = new ArrayList<>();
        mCategoriesList = new ArrayList<>();
        mImgPath = "";

        // Set toolbar_asistant title
        ((TextView)v.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.collections_title));
        v.findViewById(R.id.left_btn).setVisibility(View.VISIBLE);
        v.findViewById(R.id.left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MapsActivity.newInstance(getContext(), BookDetailsActivity.COLECCIONES));
            }
        });
        v.findViewById(R.id.right_btn).setVisibility(View.VISIBLE);
        v.findViewById(R.id.right_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        SearchActivity.newIntent(getContext()),
                        REQUEST_SEARCH);
            }
        });

        // Order collections
        v.findViewById(R.id.news_txt).setOnClickListener(this);
        v.findViewById(R.id.top_txt).setOnClickListener(this);
        v.findViewById(R.id.month_theme_txt).setOnClickListener(this);
        v.findViewById(R.id.all_txt).setOnClickListener(this);

        // Categories Recycler View
        mCategoriesRV = (RecyclerView) v.findViewById(R.id.categories_recycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mCategoriesRV.setLayoutManager(mLayoutManager);
        mCategoriesAdapter = new CategoriesAdapter(mCategoriesList, mImgPath, R.color.collections_nav_pressed, getContext(), new CategoriesAdapter.RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(String categoryId) {
                url = ApiConfig.searchCollections;
                params = "?categoria=" + categoryId;
                loadCollections(url, params);
                restoreOrderColors();
            }
        });
        mCategoriesRV.setAdapter(mCategoriesAdapter);
        if(mCategoriesList.isEmpty() || mImgPath.isEmpty()) {
            loadCategories();
        }

        // Collections RecyclerView
        mCollectionsRV = (RecyclerView) v.findViewById(R.id.collections_recycler);
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mCollectionsRV.setLayoutManager(mGridLayoutManager);
        mCollectionsRV.setHasFixedSize(true);
        mCollectionsAdapter = new CollectionsAdapter(mCollectionsList, getContext());
        mCollectionsAdapter.mColType = BookDetailsActivity.COLECCIONES;
        mCollectionsRV.setAdapter(mCollectionsAdapter);

        // show new collections on start
        if(mCollectionsList.isEmpty()) {
            v.findViewById(R.id.news_txt).performClick();
        }

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
                                mNestedScroll.scrollTo(0, 0);
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
        url = ApiConfig.collections;
        switch (v.getId()) {
            case R.id.news_txt:
                params = "?order=nuevas";
                break;
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
        loadCollections(url, params);
        mCategoriesAdapter.clearActive();
        mCategoriesAdapter.notifyDataSetChanged();
    }

    private void restoreOrderColors() {
        v.findViewById(R.id.news_txt).setBackground(getResources().getDrawable(R.drawable.nav_subcategories_button));
        v.findViewById(R.id.top_txt).setBackground(getResources().getDrawable(R.drawable.nav_subcategories_button));
        v.findViewById(R.id.month_theme_txt).setBackground(getResources().getDrawable(R.drawable.nav_subcategories_button));
        v.findViewById(R.id.all_txt).setBackground(getResources().getDrawable(R.drawable.nav_subcategories_button));
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
