package com.clickaboom.letrasparavolar.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.activities.BookDetailsActivity;
import com.clickaboom.letrasparavolar.activities.MainActivity;
import com.clickaboom.letrasparavolar.activities.MapsActivity;
import com.clickaboom.letrasparavolar.activities.BuscarActivity;
import com.clickaboom.letrasparavolar.adapters.LegendsCategoriesAdapter;
import com.clickaboom.letrasparavolar.adapters.LegendsAdapter;
import com.clickaboom.letrasparavolar.models.collections.Categoria;
import com.clickaboom.letrasparavolar.models.collections.Colecciones;
import com.clickaboom.letrasparavolar.models.collections.ResCollections;
import com.clickaboom.letrasparavolar.models.collections.categories.ResCategories;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.clickaboom.letrasparavolar.network.GsonRequest;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.clickaboom.letrasparavolar.activities.MainActivity.EXTRA_BOOK_ITEM;
import static com.clickaboom.letrasparavolar.activities.MainActivity.db;

/**
 * Created by Karencita on 15/05/2017.
 */

public class LegendsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "com.lpv.collections";
    public static final int REQUEST_SEARCH = 0;
    public static final String RESULT_SEARCH = "searchText";
    private static final String LIST_STATE_KEY = "listState";
    private RecyclerView mCategoriesRV, mCollectionsRV;
    private LegendsAdapter mLegendsAdapter;
    private List<Colecciones> mLegendsList = new ArrayList<>();
    private List<Categoria> mCategoriesList = new ArrayList<>();
    private View v;
    private String url = "", params = "", mImgPath = "";
    private LegendsCategoriesAdapter mLegendsCategoriesAdapter;
    private GridLayoutManager mGridLayoutManager;
    private NestedScrollView mNestedScroll;
    private CoordinatorLayout mCoordinatorLayout;
    private Context mContext;

    public static LegendsFragment newInstance(Colecciones book) {
        LegendsFragment fragment = new LegendsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_BOOK_ITEM, book);
        fragment.setArguments(bundle);
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
//        setRetainInstance(true);
        mLegendsAdapter = new LegendsAdapter(mLegendsList, getContext());
        mLegendsCategoriesAdapter = new LegendsCategoriesAdapter(mCategoriesList, mImgPath, R.color.legends_nav_pressed, getContext(), new LegendsCategoriesAdapter.RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(Integer categoryId) {
                url = ApiConfig.searchLegends;
                params = "?categoria=" + categoryId;
                loadLegends(url, params);
                restoreOrderColors();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_legends, container, false);

        mContext = getContext();

        // Set its bottomNavButton clicked
        ((MainActivity)getActivity()).restoreBottonNavColors();
        ((MainActivity)getActivity()).legendsBtn.
                setBackgroundColor(getResources().getColor(R.color.bottom_nav_pressed));

        // ScrollView setup
        mNestedScroll = (NestedScrollView) v.findViewById(R.id.nested_scroll);
        mCoordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.coordinator_lay);

       /* mLegendsList = new ArrayList<>();
        mCategoriesList = new ArrayList<>();
        mImgPath = "";*/

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
                        BuscarActivity.newIntent(getContext()),
                        REQUEST_SEARCH);
            }
        })
        ;

        // BackBtn
        LinearLayout backBtn = (LinearLayout) v.findViewById(R.id.back_btn);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        // Menu drawer onclicklistener
        v.findViewById(R.id.drawer_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open right drawer
                MainActivity.drawer.openDrawer(GravityCompat.END);
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
        mCategoriesRV.setAdapter(mLegendsCategoriesAdapter);
        if(mCategoriesList.isEmpty() || mImgPath.isEmpty()) {
            loadCategories();
        }

        // Collections RecyclerView
        mCollectionsRV = (RecyclerView) v.findViewById(R.id.collections_recycler);
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mCollectionsRV.setLayoutManager(mGridLayoutManager);
        mCollectionsRV.setHasFixedSize(true);
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
                                    db.addAllCategories(categorias, BookDetailsActivity.LEGENDS);
                                }
                                mImgPath = ((ResCategories) response).pathIconos + "/";
                                mLegendsCategoriesAdapter.setImgPath(mImgPath);
                                mLegendsCategoriesAdapter.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                        ArrayList<Categoria> allCategories = db.getAllCategories();
                        if(allCategories.isEmpty()) {
//                            Toast.makeText(mContext, "Error de conexión", Toast.LENGTH_SHORT).show();
                        } else {
                            for(Categoria categoria: allCategories) {
                                if(categoria.categoryType.equals(BookDetailsActivity.LEGENDS))
                                    mCategoriesList.add(categoria);
                            }
//                            Toast.makeText(mContext, "Sin conexión", Toast.LENGTH_SHORT).show();
                        }
                        mLegendsCategoriesAdapter.notifyDataSetChanged();
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
//                                    db.addAllBooks(item, BookDetailsActivity.LEGENDS);
                                }

                                mLegendsAdapter.notifyDataSetChanged();
                                mNestedScroll.scrollTo(0, 0);
                                if(mNestedScroll.getHeight() < mCoordinatorLayout.getHeight()) {
                                    mNestedScroll.setMinimumHeight(mCoordinatorLayout.getHeight());
                                }

                                Colecciones book = (Colecciones) getArguments().getSerializable(EXTRA_BOOK_ITEM);
                                if(book != null) {
                                    startActivity(BookDetailsActivity.newIntent(mContext, book.id, book.mBookType));
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                        ArrayList<Colecciones> allBooks = db.getAllBooks();
                        if(allBooks.isEmpty())
                            Toast.makeText(mContext, "Error de conexión", Toast.LENGTH_SHORT).show();
                        else {
                            for(Colecciones book: allBooks) {
                                if(book.mBookType.equals(BookDetailsActivity.LEGENDS))
                                    mLegendsList.add(book);
                            }

                            Toast.makeText(mContext, "Sin conexión", Toast.LENGTH_SHORT).show();
                        }
                        mLegendsAdapter.notifyDataSetChanged();
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
        mLegendsCategoriesAdapter.clearActive();
        mLegendsCategoriesAdapter.notifyDataSetChanged();
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
