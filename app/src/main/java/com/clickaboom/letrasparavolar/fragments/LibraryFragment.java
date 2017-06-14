package com.clickaboom.letrasparavolar.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.activities.BookDetailsActivity;
import com.clickaboom.letrasparavolar.activities.MainActivity;
import com.clickaboom.letrasparavolar.adapters.CollectionsAdapter;
import com.clickaboom.letrasparavolar.adapters.LegendsAdapter;
import com.clickaboom.letrasparavolar.models.collections.Colecciones;
import com.clickaboom.letrasparavolar.network.ApiConfig;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.clickaboom.letrasparavolar.activities.MainActivity.db;

/**
 * Created by Karencita on 15/05/2017.
 */

public class LibraryFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "com.lpv.library";
    public static final int REQUEST_SEARCH = 0;
    private static final String FAVORITES = "favorites";
    private static final String DOWNLOADED = "downloaded";
    private RecyclerView mLegendsRV, mColeccionesRV;
    private CollectionsAdapter mCollectionsAdapter;
    private LegendsAdapter mLegendsAdapter;
    private List<Colecciones> mCollectionsList, mLegendsList;
    private View v;
    private String url = "", params = "", mImgPath;
    private TextView mEmptyLegends, mEmptyColecciones;

    public LibraryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        mCollectionsList = new ArrayList<>();
        mLegendsList = new ArrayList<>();
        mImgPath = "";
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putSerializable("collections", (Serializable) mCollectionsList);
//        outState.putString("imgPath", mImgPath);

    }

    @Override
    public void onResume() {
        super.onResume();

        loadCollections();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*if(savedInstanceState != null) {
            mImgPath = savedInstanceState.getString("imgPath");
            mCollectionsList = (List<Colecciones>) savedInstanceState.getSerializable("collections");
            mCategoriesAdapter.notifyDataSetChanged();
            mCollectionsAdapter.notifyDataSetChanged();
        }*/
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
        v.findViewById(R.id.favorites_txt).setOnClickListener(this);
        v.findViewById(R.id.downloaded_txt).setOnClickListener(this);

        // Categories Recycler View
        mLegendsRV = (RecyclerView)v.findViewById(R.id.legends_recycler);
        mColeccionesRV = (RecyclerView)v.findViewById(R.id.collections_recycler);

        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mLegendsRV.setLayoutManager(mGridLayoutManager);
        mGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mColeccionesRV.setLayoutManager(mGridLayoutManager);

        // "is empty" texts
        mEmptyLegends = (TextView) v.findViewById(R.id.empty_legends);
        mEmptyColecciones = (TextView) v.findViewById(R.id.empty_collections);

        // specify an adapter (see also next example)
        mLegendsAdapter = new LegendsAdapter(mLegendsList, getContext());
        mLegendsAdapter.mColType = BookDetailsActivity.LEGENDS;
        mLegendsRV.setAdapter(mLegendsAdapter);

        mCollectionsAdapter = new CollectionsAdapter(mCollectionsList, getContext());
        mCollectionsAdapter.mColType = BookDetailsActivity.COLECCIONES;
        mColeccionesRV.setAdapter(mCollectionsAdapter);

        // Load favorites at fragment visible
        v.findViewById(R.id.favorites_txt).performClick();

        return v;
    }

    private void loadCollections() {
        // Clear previous data
        mLegendsList.clear();
        mCollectionsList.clear();

        ArrayList<Colecciones> mArrayList = db.getAllBooks();

        if(params.equals(DOWNLOADED)) { // Request all downloaded Books
            if (mArrayList != null) { // If there's data available in the db
                for (Colecciones book : mArrayList) {
                    if (book.mBookType.equals(BookDetailsActivity.LEGENDS))
                        mLegendsList.add(book);
                    else if (book.mBookType.equals(BookDetailsActivity.COLECCIONES))
                        mCollectionsList.add(book);
                }
            }
            // Set empty text
            mEmptyLegends.setText(getResources().getString(R.string.no_leyendas_descargadas));
            mEmptyColecciones.setText(getResources().getString(R.string.no_libros_descargados));
        } else if(params.equals(FAVORITES)){ // Request just the favorite books
            if (mArrayList != null) { // If there's data available in the db
                for (Colecciones book : mArrayList) {
                    if (book.mBookType.equals(BookDetailsActivity.LEGENDS) && book.favorito)
                        mLegendsList.add(book);
                    else if (book.mBookType.equals(BookDetailsActivity.COLECCIONES) && book.favorito)
                        mCollectionsList.add(book);
                }
            }
            // Set empty text
            mEmptyLegends.setText(getResources().getString(R.string.no_leyendas));
            mEmptyColecciones.setText(getResources().getString(R.string.no_libros));
        }


        // Show "is empty" text if needed,
        if(mLegendsList.isEmpty()) {
            mEmptyLegends.setVisibility(View.VISIBLE);
        } else {
            mEmptyLegends.setVisibility(View.GONE);
        }

        if(mCollectionsList.isEmpty()) {
            mEmptyColecciones.setVisibility(View.VISIBLE);
        } else {
            mEmptyColecciones.setVisibility(View.GONE);
        }

        // Update Adapters
        mLegendsAdapter.notifyDataSetChanged();
        mCollectionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        restoreOrderColors();
        url = ApiConfig.searchCollections;
        switch (v.getId()) {
            case R.id.favorites_txt:
                params = FAVORITES;
                break;
            case R.id.downloaded_txt:
                params = DOWNLOADED;
                break;

        }
        v.setBackgroundColor(getResources().getColor(R.color.order_back_pressed));
        loadCollections();
    }

    private void restoreOrderColors() {
        v.findViewById(R.id.favorites_txt).setBackground(getResources().getDrawable(R.drawable.nav_subcategories_button));
        v.findViewById(R.id.downloaded_txt).setBackground(getResources().getDrawable(R.drawable.nav_subcategories_button));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SEARCH:
                if(resultCode == RESULT_OK) {
                    loadCollections();
                }
                break;
        }
    }
}
