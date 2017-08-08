package com.clickaboom.letrasparavolar.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.activities.MainActivity;
import com.clickaboom.letrasparavolar.adapters.InternationalizationAdapter;
import com.clickaboom.letrasparavolar.models.internationalization.Internationalization;
import com.clickaboom.letrasparavolar.models.internationalization.ResInter;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.clickaboom.letrasparavolar.network.GsonRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karencita on 15/05/2017.
 */

public class InformationFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "com.lpv.collections";
    private View v;
    private String url = "", params = "";
    private LinearLayout infoLay;
    private RecyclerView mInterRV;
    private GridLayoutManager mGridLayoutManager;
    private InternationalizationAdapter mInterAdapter;
    private List<Internationalization> mInterList = new ArrayList<>();
    private NestedScrollView mNestedScroll;

    public InformationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_information, container, false);

        // Set toolbar_asistant title
        ((TextView)v.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.information));
        v.findViewById(R.id.toolbar_prev_btn).setVisibility(View.GONE);
        v.findViewById(R.id.toolbar_next_btn).setVisibility(View.GONE);

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
        v.findViewById(R.id.information_txt).setOnClickListener(this);
        v.findViewById(R.id.international_txt).setOnClickListener(this);
        infoLay = (LinearLayout)v.findViewById(R.id.info_lay);

        // Nested Scroll
        mNestedScroll = (NestedScrollView) v.findViewById(R.id.nested_scroll);

        // Inter RecyclerView
        mInterRV = (RecyclerView) v.findViewById(R.id.inter_recycler);
        mGridLayoutManager = new GridLayoutManager(getContext(), 1);
        mInterRV.setLayoutManager(mGridLayoutManager);
        mInterRV.setHasFixedSize(true);
        mInterAdapter = new InternationalizationAdapter(mInterList, getContext());
        mInterRV.setAdapter(mInterAdapter);

        // Load webview content
        loadInternationalization();

        // Click info button
        v.findViewById(R.id.information_txt).performClick();
        return v;
    }

    @Override
    public void onClick(View v) {
        restoreOrderColors();
        url = ApiConfig.searchCollections;
        switch (v.getId()) {
            case R.id.information_txt:
                infoLay.setVisibility(View.VISIBLE);
                mInterRV.setVisibility(View.GONE);
                break;
            case R.id.international_txt:
                infoLay.setVisibility(View.GONE);
                mInterRV.setVisibility(View.VISIBLE);
                break;

        }
        mNestedScroll.scrollTo(0, 0);
        v.setBackgroundColor(getResources().getColor(R.color.order_back_pressed));
    }

    private void restoreOrderColors() {
        v.findViewById(R.id.information_txt).setBackground(getResources().getDrawable(R.drawable.nav_subcategories_button));
        v.findViewById(R.id.international_txt).setBackground(getResources().getDrawable(R.drawable.nav_subcategories_button));
    }

    private void loadInternationalization() {
        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(getActivity())
                .addToRequestQueue(new GsonRequest(ApiConfig.internacionalization,
                        ResInter.class,
                        Request.Method.GET,
                        null, null,
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                Log.d(TAG, response.toString());
                                List<Internationalization> res = ((ResInter) response).data;
                                mInterList.addAll(res);
                                mInterAdapter.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }));

    }

}
