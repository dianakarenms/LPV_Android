package com.clickaboom.letrasparavolar.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.clickaboom.letrasparavolar.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.service.MediatypeService;

/**
 * Created by clickaboom on 6/10/17.
 */

public class EpubBookContentActivity extends Activity {

    private static final String TAG = "EpubBookContentActivity";
    WebView mWebView;

    Book book;

    int position = 0;

    String line;
    int i = 0;

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, EpubBookContentActivity.class);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebook);

        mWebView = (WebView) findViewById(R.id.webView1);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        AssetManager assetManager = getAssets();
        String[] files;

        try {

            files = assetManager.list("books");
            List<String> list = Arrays.asList(files);

            if (!this.makeDirectory("books")) {
                debug("faild to make books directory");
            }

            //copyBookToDevice(list.get(position));

            String basePath = Environment.getExternalStorageDirectory() + "/books/";

            InputStream epubInputStream = assetManager.open("books/"+list.get(position));

            book = (new EpubReader()).readEpub(epubInputStream);

            DownloadResource(basePath);

            String linez = "";
            Spine spine = book.getSpine();
            List<SpineReference> spineList = spine.getSpineReferences() ;
            int count = spineList.size();

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


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            linez = linez.replace("../", "");

            mWebView.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    // Column Count is just the number of 'screens' of text. Add one for partial 'screens'
                    double columnCount = Math.floor(view.getHeight() / view.getWidth())+1;

                    // Must be expressed as a percentage. If not set then the WebView will not stretch to give the desired effect.
                    double columnWidth = columnCount * 100;

                    injectJavascript();
                }
            });
            mWebView.loadDataWithBaseURL("file://"+Environment.getExternalStorageDirectory()+"/books/", linez, "text/html", "utf-8", null);

        } catch (IOException e) {
            Log.e("epublib exception", e.getMessage());
        }
    }

    class JsObject {
        @JavascriptInterface
        public String toString() { return "injectedObject"; }
    }

    public void injectJavascript() {
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
                "alert('Hola mundo');" +
//                            "var meta = document.createElement('meta');" +
//                            "meta.name = 'viewport'" +
//                            "meta.content = 'width=device-width, initial-scale=1.0'" +
//                            "document.getElementsByTagName('head')[0].appendChild(meta);" +
                "}";

        mWebView.loadUrl(js);
        mWebView.loadUrl("javascript:initialize()");
    }

    public boolean makeDirectory(String dirName) {
        boolean res;

        String filePath = new String(Environment.getExternalStorageDirectory()+"/"+dirName);

        debug(filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            res = file.mkdirs();
        }else {
            res = false;
        }
        return res;
    }

    public void debug(String msg) {
        //      if (Setting.isDebug()) {
        Log.d("EPub", msg);
        //      }
    }

    public void copyBookToDevice(String fileName) {
        System.out.println("Copy Book to donwload folder in phone");
        try
        {
            InputStream localInputStream = getAssets().open("books/"+fileName);
            String path = Environment.getExternalStorageDirectory() + "/books/"+fileName;
            FileOutputStream localFileOutputStream = new FileOutputStream(path);

            byte[] arrayOfByte = new byte[1024];
            int offset;
            while ((offset = localInputStream.read(arrayOfByte))>0)
            {
                localFileOutputStream.write(arrayOfByte, 0, offset);
            }
            localFileOutputStream.close();
            localInputStream.close();
            Log.d(TAG, fileName+" copied to phone");

        }
        catch (IOException localIOException)
        {
            localIOException.printStackTrace();
            Log.d(TAG, "failed to copy");
            return;
        }
    }



    private void DownloadResource(String directory) {
        try {

            Resources rst = book.getResources();
            Collection<Resource> clrst = rst.getAll();
            Iterator<Resource> itr = clrst.iterator();

            while (itr.hasNext()) {
                Resource rs = itr.next();

                if ((rs.getMediaType() == MediatypeService.NCX)
                        || (rs.getMediaType() == MediatypeService.OPENTYPE)
                        || (rs.getMediaType() == MediatypeService.XHTML)
                        || (rs.getMediaType() == MediatypeService.MP3)) {

                    Log.d(TAG, rs.getHref());

                    File oppath1 = new File(directory, rs.getHref().replace("OEBPS/", ""));

                    oppath1.getParentFile().mkdirs();
                    oppath1.createNewFile();

                    System.out.println("Path : "+oppath1.getParentFile().getAbsolutePath());


                    FileOutputStream fos1 = new FileOutputStream(oppath1);
                    fos1.write(rs.getData());
                    fos1.close();

                } else if (rs.getMediaType() == MediatypeService.CSS) {

                    File oppath = new File(directory, rs.getHref());

                    oppath.getParentFile().mkdirs();
                    oppath.createNewFile();

                    FileOutputStream fos = new FileOutputStream(oppath);
                    fos.write(rs.getData());
                    fos.close();

                } else if ((rs.getMediaType() == MediatypeService.JPG)
                        || (rs.getMediaType() == MediatypeService.PNG)) {

                    Log.d(TAG, rs.getHref());

                    File oppath2 = new File(directory, rs.getHref().replace("OEBPS/", ""));

                    oppath2.getParentFile().mkdirs();
                    oppath2.createNewFile();

                    System.out.println("Path : "+oppath2.getParentFile().getAbsolutePath());

//                    FileOutputStream fos1 = new FileOutputStream(oppath2);
//                    fos1.write(rs.getData());
//                    fos1.close();

                    // Get display dimensions
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;

                    Bitmap b = BitmapFactory.decodeByteArray(rs.getData(), 0, rs.getData().length);
//                    Bitmap out = Bitmap.createScaledBitmap(b, width, height, false);

                    Bitmap out = getResizedBitmap(b, width, height);

                    FileOutputStream fOut;
                    try {
                        fOut = new FileOutputStream(oppath2);
                        out.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                        b.recycle();
                        out.recycle();
                    } catch (Exception e) {}

                }

            }

        } catch (Exception e) {
            Log.e("EpubBook", e.getMessage());
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxWidth, int maxHeight) {
        /*int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;*/

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
}