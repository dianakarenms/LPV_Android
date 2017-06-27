package com.clickaboom.letrasparavolar.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.models.collections.Colecciones;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.DownloadFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.service.MediatypeService;

/**
 * Created by clickaboom on 6/10/17.
 */

public class EpubBookContentActivity2 extends Activity implements DownloadFile.OnTaskCompleted {

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
        Intent i = new Intent(packageContext, EpubBookContentActivity2.class);
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

        descargar(ApiConfig.epubs + mEpub.epub, mEpub.epub);

        mWebView = (WebView) findViewById(R.id.webView1);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                //injectJavascript();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                mWebView.loadUrl(mEpubBaseURL + "?book=" + mEpub.epub);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    public void loadEpubFromStorage() {

        basePath = Environment.getExternalStorageDirectory() + "/LPV_eBooks/epub_reader/epubs/" + mEpub.epub;

        // read epub
        /*EpubReader epubReader = new EpubReader();
        try {
            book = epubReader.readEpub(new FileInputStream(basePath + "/" + mEpub.epub));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        try {
            decom(mEpub.epub, basePath + "/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //DownloadResource(basePath);

        /*linez = "";
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
        //mWebView.loadDataWithBaseURL("file://" + basePath + "/", linez, "text/html", "utf-8", null);*/
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
                        || (rs.getMediaType() == MediatypeService.XHTML)
                        || (rs.getMediaType() == MediatypeService.MP3)) {

                    Log.d(TAG, rs.getHref());

                    File oppath1 = new File(directory, rs.getHref());

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

                    File oppath2 = new File(directory, rs.getHref());

                    oppath2.getParentFile().mkdirs();
                    oppath2.createNewFile();

                    System.out.println("Path : "+oppath2.getParentFile().getAbsolutePath());

                    // Get display dimensions
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;

                    Bitmap b = BitmapFactory.decodeByteArray(rs.getData(), 0, rs.getData().length);

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
        DownloadFile downloadFile = new DownloadFile(mDownloadsListener, mContext, EpubBookContentActivity2.this, true, mEpub);
        downloadFile.execute(url, fileName, fileName);
    }

    @Override
    public void onTaskCompleted() {
        new LoadBook(mContext, barProgressDialog).execute();
    }

    public class LoadBook extends AsyncTask<Void, Void, Void> {

        private ProgressDialog barProgressDialog;
        private Context mContext;

        public LoadBook(Context context, ProgressDialog barProg) {
            mContext = context;
            barProgressDialog = barProg;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RelativeLayout layout = new RelativeLayout(mContext);
            progressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleLarge);
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(progressBar,params);

            setContentView(layout);

            /*barProgressDialog = new ProgressDialog(mContext);
            barProgressDialog.setMessage("Abriendo...");
            barProgressDialog.setProgressStyle(barProgressDialog.STYLE_SPINNER);
            barProgressDialog.setIndeterminate(true);
            barProgressDialog.setCancelable(false);
            barProgressDialog.show();*/
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            //copyFileOrDir("epub_reader/");
            loadEpubFromStorage();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mEpubBaseURL = "file://" + Environment.getExternalStorageDirectory() + "/LPV_eBooks/epub_reader/index.html";

//            mWebView.loadDataWithBaseURL("file://" + basePath + "/", linez, "text/html", "utf-8", null);
            mWebView.loadUrl(mEpubBaseURL);
            progressBar.setVisibility(View.GONE);
//             barProgressDialog.dismiss();
        }
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

    public boolean unzip(String zipname, String path) {
        InputStream is;
        ZipInputStream zis;
        try {
            String filename;
            is = new FileInputStream(path + "/" + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null) {
                // zapis do souboru
                filename = ze.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    File fmd = new File(path, filename);
                    fmd.mkdirs();
                    continue;
                } else {
                    // Make this part of the code more efficient .code-revise
                    File fmd = new File(path, filename);
                    Log.d("Unzipping", fmd.getParentFile().getPath());
                    String parent = fmd.getParentFile().getPath();

                    File fmd_1 = new File(parent);
                    fmd_1.mkdirs();
                    // end of .code-revise
                }

                FileOutputStream fout = new FileOutputStream(path + filename);

                // cteni zipu a zapis
                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}