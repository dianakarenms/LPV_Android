package com.clickaboom.letrasparavolar.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.webkit.WebView;

import com.clickaboom.letrasparavolar.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * Created by karen on 09/06/17.
 */

public class EPubDemo extends Activity {
    WebView webview;
    String line, line1 = "", finalstr = "";
    int i = 0;
    private String linez;
    private String bookName = ""; //"books/el_callejon.epub";
    private Book book;

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, EPubDemo.class);
        return i;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webview = (WebView) findViewById(R.id.webView1);

        webview.getSettings().setJavaScriptEnabled(true);
        AssetManager am = getAssets();
        try {
            InputStream epubInputStream = am.open(bookName);
            book = (new EpubReader()).readEpub(epubInputStream);
        } catch (IOException e) {
            Log.e("epublib", e.getMessage());
        }

        Spine spine = book.getSpine();
        List<SpineReference> spineList = spine.getSpineReferences() ;
        int count = spineList.size();
        //tv.setText(Integer.toString(count));
        StringBuilder string = new StringBuilder();
        for (int i = 0; count > i; i++) {
            Resource res = spine.getResource(i);

            try {
                InputStream is = res.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                try {
                    while ((line = reader.readLine()) != null) {
                        linez =   string.append(line + "\n").toString();
                    }

                } catch (IOException e) {e.printStackTrace();}

                //do something with stream
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        webview.loadData(linez, "text/html", "utf-8");

        /*AssetManager assetManager = getAssets();
        try {
            // find InputStream for book
            InputStream epubInputStream = assetManager
                    .open("epubs/el_callejon.epub");

            // Load Book from inputStream
            Book book = (new EpubReader()).readEpub(epubInputStream);

            // Log the book's authors
            Log.i("author", " : " + book.getMetadata().getAuthors());

            // Log the book's title
            Log.i("title", " : " + book.getTitle());

             Log the book's coverimage property
            // Bitmap coverImage =
            // BitmapFactory.decodeStream(book.getCoverImage()
            // .getInputStream());
            // Log.i("epublib", "Coverimage is " + coverImage.getWidth() +
            // " by "
            // + coverImage.getHeight() + " pixels");

            // String html = readFile(is);
            String baseUrl = "file:///android_asset/epubs/";
            String data = new String(book.getContents().get(2).getData());
            mWebView.loadDataWithBaseURL(baseUrl, data, "text/html", "UTF-8", null);


            // Log the tale of contents
            //logTableOfContents(book.getTableOfContents().getTocReferences(), 0);
        } catch (IOException e) {
            Log.e("epublib exception", e.getMessage());
        }

        String javascrips = "";
        try {
            // InputStream input = getResources().openRawResource(R.raw.lights);
            InputStream input = this.getAssets().open(
                    "epubs/el_callejon.epub");

            int size;
            size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            // byte buffer into a string
            javascrips = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.loadDataWithBaseURL("file:///android_asset/", javascrips,
//                "application/epub+zip", "UTF-8", null);*/
    }

    @SuppressWarnings("unused")
    private void logTableOfContents(List<TOCReference> tocReferences, int depth) {
        if (tocReferences == null) {
            return;
        }

        for (TOCReference tocReference : tocReferences) {
            StringBuilder tocString = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                tocString.append("\t");
            }
            tocString.append(tocReference.getTitle());
            Log.i("TOC", tocString.toString());

            try {
                InputStream is = tocReference.getResource().getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(is));

                while ((line = r.readLine()) != null) {
                    // line1 = Html.fromHtml(line).toString();
                    Log.v("line" + i, Html.fromHtml(line).toString());
                    // line1 = (tocString.append(Html.fromHtml(line).toString()+
                    // "\n")).toString();
                    line1 = line1.concat(Html.fromHtml(line).toString());
                }
                finalstr = finalstr.concat("\n").concat(line1);
                // Log.v("Content " + i, finalstr);
                i++;
            } catch (IOException e) {

            }

            logTableOfContents(tocReference.getChildren(), depth + 1);
        }
        webview.loadDataWithBaseURL("", finalstr, "text/html", "UTF-8", "");
    }
}
