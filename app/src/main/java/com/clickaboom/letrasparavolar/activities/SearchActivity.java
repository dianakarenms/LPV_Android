package com.clickaboom.letrasparavolar.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.fragments.CollectionsFragment;
import com.clickaboom.letrasparavolar.models.banners.Banner;
import com.clickaboom.letrasparavolar.models.banners.ResBanners;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.clickaboom.letrasparavolar.network.GsonRequest;

import java.util.List;

public class SearchActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = "com.lpv.SearchActivity";
    //private static final String EXTRA_SEARCH_TYPE = "com.lpv.SearchType";
    private Context mCxt;

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, SearchActivity.class);
        //i.putExtra(EXTRA_SEARCH_TYPE, searchType);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mCxt = this;

        final EditText searchField = (EditText)findViewById(R.id.search_edit);
        searchField.requestFocus();
        searchField.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(CollectionsFragment.RESULT_SEARCH, searchField.getText().toString());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                    handled = true;
                }
                return handled;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_back:
                finish();
                break;
        }
    }

    private void loadBanners() {

        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(getApplicationContext())
                .addToRequestQueue(new GsonRequest(ApiConfig.banners,
                        ResBanners.class,
                        Request.Method.GET,
                        null, null,
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                Log.d(TAG, response.toString());
                                List<Banner> res = ((ResBanners) response).data;
                                /*mBannerItems.clear();

                                // Repeat last and first values to make an infinite ViewPager
                                mBannerItems.add(res.get(res.size()-1));
                                mBannerItems.addAll(res); // Add main book to list
                                mBannerItems.add(res.get(0));

                                mBannerAdapter.notifyDataSetChanged();*/
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }));

    }
}
