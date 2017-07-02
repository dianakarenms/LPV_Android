package com.clickaboom.letrasparavolar.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.models.collections.Colecciones;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.DownloadFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import nl.siegmann.epublib.domain.Book;

import static com.clickaboom.letrasparavolar.R.id.webView;

/**
 * Created by clickaboom on 6/10/17.
 */

public class EpubBookContentActivity extends Activity implements DownloadFile.OnTaskCompleted {

    private static final String TAG = "EpubBookContentActivity";
    private static final String EXTRA_EPUB = "lpv.epub.name";
    WebView mWebView;

    Book book;

    int position = 0;

    String line;
    int i = 0;
    private Context mContext;
    private Colecciones mEpub;
    private DownloadFile.OnTaskCompleted mDownloadsListener;
    private ProgressDialog barProgressDialog;
    private String linez;
    private String basePath;
    private ProgressBar progressBar;
    private String mEpubBaseURL;

    public static Intent newIntent(Context packageContext, Colecciones epub) {
        Intent i = new Intent(packageContext, EpubBookContentActivity.class);
        i.putExtra(EXTRA_EPUB, epub);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebook);

        mContext = this;
        mDownloadsListener = this;

        mEpub = (Colecciones) getIntent().getSerializableExtra(EXTRA_EPUB);
        basePath = Environment.getExternalStorageDirectory() + "/LPV_eBooks/epub_reader/epubs/" + mEpub.epub;

        mWebView = (WebView) findViewById(webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                //mWebView.loadUrl(mEpubBaseURL);
            }

        });

        File file = new File(basePath);
        if(!file.exists())
            descargar(ApiConfig.epubs + mEpub.epub, mEpub.epub);
        else
            loadEpubFromStorage();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.loadUrl("file:///android_asset/nonexistent.html");
        mWebView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    public void loadEpubFromStorage() {
        try {
            decom(mEpub.epub, basePath + "/");

            mEpubBaseURL = "file://" + Environment.getExternalStorageDirectory() + "/LPV_eBooks/epub_reader/index.html?book=" + mEpub.epub;
            mWebView.loadUrl(mEpubBaseURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void injectJavascript() {
        String js = "javascript:function initialize() { " +
                "\"use strict\";" +
                "var Book = ePub(\"epubs/rf0g18genz-6.epub/\");" +
                "Book.renderTo(\"area\");" +
                "}";

        mWebView.loadUrl(js);
        mWebView.loadUrl("javascript:initialize()");
    }

    /*public void injectJavascript() {
        String js = "javascript:function initialize() { " +
                "var d = document.getElementsByTagName('body')[0];" +
                "var ourH = window.innerHeight; " +
                "var ourW = window.innerWidth; " +
                "var fullH = d.offsetHeight; " +
                "var pageCount = Math.floor(fullH/ourH)+1;" +
                "var currentPage = 0; " +
                "var newW = pageCount*ourW; " +
                "d.style.height = ourH+'px';" +
                "d.style.width = newW+'px';" +
                "d.style.webkitColumnGap = '2px'; " +
                "d.style.margin = 0; " +
                "d.style.webkitColumnCount = pageCount;" +
                "var viewPortTag=document.createElement('meta');" +
                "viewPortTag.id='viewport';" +
                "viewPortTag.name = 'viewport';" +
                "viewPortTag.content = 'width=device-width, initial-scale=1.0; maximum-scale=1.0; user-scalable=0;';" +
                "document.getElementsByTagName('head')[0].appendChild(viewPortTag);" +
                "}";

        mWebView.loadUrl(js);
        mWebView.loadUrl("javascript:initialize()");
    }*/

    public Bitmap getResizedBitmap(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    public void descargar(String url, String fileName){
        DownloadFile downloadFile = new DownloadFile(mDownloadsListener, mContext, EpubBookContentActivity.this, true, mEpub);
        downloadFile.execute(url, fileName, fileName);
    }

    @Override
    public void onTaskCompleted() {
        loadEpubFromStorage();
    }

    private void decom(String zipname, String path) throws IOException {
        ZipFile zipFile = new ZipFile(path + zipname);
        //String path = Environment.getExternalStorageDirectory() + "/unzipped10/";

        Enumeration<?> files = zipFile.entries();
        while (files.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) files.nextElement();
            Log.d(TAG, "ZipEntry: "+entry);
            Log.d(TAG, "isDirectory: " + entry.isDirectory());

            if (entry.isDirectory()) {
                File file = new File(path + entry.getName());
                file.mkdir();
                Log.d(TAG, "Create dir " + entry.getName());
            } else {
                String filepath = path + entry.getName();
                File f = new File(filepath);
                f.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(f);
                InputStream is = zipFile.getInputStream(entry);
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                fos.close();
                Log.d(TAG, "Create File " + entry.getName());
            }
        }
        Log.d(TAG, "Done extracting epub file");
    }
}