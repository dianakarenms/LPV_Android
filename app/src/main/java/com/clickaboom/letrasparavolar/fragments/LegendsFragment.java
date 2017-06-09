package com.clickaboom.letrasparavolar.fragments;

import android.content.Intent;
import android.os.Bundle;
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

public class LegendsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "com.lpv.collections";
    public static final int REQUEST_SEARCH = 0;
    public static final String RESULT_SEARCH = "searchText";
    private static final String LIST_STATE_KEY = "listState";
    private RecyclerView mCategoriesRV, mCollectionsRV;
    private CollectionsAdapter mLegendsAdapter;
    private List<Colecciones> mLegendsList;
    private List<Categoria> mCategoriesList;
    private View v;
    private String url = "", params = "", mImgPath;
    private CategoriesAdapter mCategoriesAdapter;
    private GridLayoutManager mGridLayoutManager;
    private NestedScrollView mNestedScroll;

    public static LegendsFragment newInstance() {
        LegendsFragment fragment = new LegendsFragment();
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        mListState = mGridLayoutManager.onSaveInstanceState();
//        outState.putParcelable(LIST_STATE_KEY, mListState);
        /*outState.putSerializable("categories", (Serializable) mCategoriesList);
        outState.putSerializable("collections", (Serializable) mLegendsList);
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
        v = inflater.inflate(R.layout.fragment_legends, container, false);

        // Set its bottomNavButton clicked
        ((MainActivity)getActivity()).restoreBottonNavColors();
        ((MainActivity)getActivity()).legendsBtn.
                setBackgroundColor(getResources().getColor(R.color.bottom_nav_pressed));

        mNestedScroll = (NestedScrollView) v.findViewById(R.id.nested_scroll);
        mLegendsList = new ArrayList<>();
        mCategoriesList = new ArrayList<>();
        mImgPath = "";

        // Set toolbar_asistant title

        ((TextView)v.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.legends_title));
        v.findViewById(R.id.left_btn).setVisibility(View.VISIBLE);
        v.findViewById(R.id.left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MapsActivity.newInstance(getContext(), BookDetailsActivity.LEGENDS));
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
        mCategoriesAdapter = new CategoriesAdapter(mCategoriesList, mImgPath, R.color.legends_nav_pressed, getContext(), new CategoriesAdapter.RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(String categoryId) {
                url = ApiConfig.searchLegends;
                params = "?categoria=" + categoryId;
                loadLegends(url, params);
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
        mLegendsAdapter = new CollectionsAdapter(mLegendsList, getContext());
        mLegendsAdapter.mColType = BookDetailsActivity.LEGENDS;
        mCollectionsRV.setAdapter(mLegendsAdapter);

        // show new collections on start
        if(mLegendsList.isEmpty()) {
            v.findViewById(R.id.news_txt).performClick();
        }

        return v;
    }

    private void loadCategories() {
        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(getActivity())
                .addToRequestQueue(new GsonRequest(ApiConfig.legendsCategories,
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

    private void loadLegends(String url, String params) {

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

                                mLegendsList.clear();
                                for(List<Colecciones> item : res) {
                                    mLegendsList.addAll(item); // Add main book to list
                                }

                                mLegendsAdapter.notifyDataSetChanged();
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
        url = ApiConfig.legends;
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
        loadLegends(url, params);
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
                    loadLegends(ApiConfig.searchLegends, "?q=" + data.getStringExtra(RESULT_SEARCH));
                }
                break;
        }
    }
}
