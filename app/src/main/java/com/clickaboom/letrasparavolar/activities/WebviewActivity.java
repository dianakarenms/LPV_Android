package com.clickaboom.letrasparavolar.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import com.clickaboom.letrasparavolar.R;

public class WebviewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "com.lpv.SearchActivity";
    private static final String EXTRA_URL = "com.lpv.mWebView.url";
    //private static final String EXTRA_SEARCH_TYPE = "com.lpv.SearchType";
    private Context mCxt;
    private WebView webView;

    public static Intent newIntent(Context packageContext, String url) {
        Intent i = new Intent(packageContext, WebviewActivity.class);
        i.putExtra(EXTRA_URL, url);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mCxt = this;

        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getIntent().getStringExtra(EXTRA_URL));
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
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
}
