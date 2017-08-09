package com.clickaboom.letrasparavolar.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.clickaboom.letrasparavolar.R;
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

public class InformationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "com.lpv.collections";
    private String url = "", params = "";
    private LinearLayout infoLay;
    private RecyclerView mInterRV;
    private GridLayoutManager mGridLayoutManager;
    private InternationalizationAdapter mInterAdapter;
    private List<Internationalization> mInterList = new ArrayList<>();
    private NestedScrollView mNestedScroll;
    private Context mContext;

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, InformationActivity.class);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        mContext = this;

        // Menu drawer onclicklistener
        findViewById(R.id.drawer_button).setVisibility(View.INVISIBLE);

        // Set toolbar_asistant title
        ((TextView) findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.information));
        findViewById(R.id.toolbar_prev_btn).setVisibility(View.GONE);
        findViewById(R.id.toolbar_next_btn).setVisibility(View.GONE);

        // BackBtn
        LinearLayout backBtn = (LinearLayout) findViewById(R.id.back_btn);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Order collections
        findViewById(R.id.information_txt).setOnClickListener(this);
        findViewById(R.id.international_txt).setOnClickListener(this);
        infoLay = (LinearLayout)findViewById(R.id.info_lay);

        // Nested Scroll
        mNestedScroll = (NestedScrollView) findViewById(R.id.nested_scroll);

        // Inter RecyclerView
        mInterRV = (RecyclerView) findViewById(R.id.inter_recycler);
        mGridLayoutManager = new GridLayoutManager(mContext, 1);
        mInterRV.setLayoutManager(mGridLayoutManager);
        mInterRV.setHasFixedSize(true);
        mInterAdapter = new InternationalizationAdapter(mInterList, mContext);
        mInterRV.setAdapter(mInterAdapter);

        // Load webview content
        loadInternationalization();

        // Click info button
        findViewById(R.id.information_txt).performClick();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

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
        findViewById(R.id.information_txt).setBackground(getResources().getDrawable(R.drawable.nav_subcategories_button));
        findViewById(R.id.international_txt).setBackground(getResources().getDrawable(R.drawable.nav_subcategories_button));
    }

    private void loadInternationalization() {
        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(mContext)
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
