package com.clickaboom.letrasparavolar.activities;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.adapters.BannerPagerAdapter;
import com.clickaboom.letrasparavolar.adapters.ColeccionesDefaultAdapter;
import com.clickaboom.letrasparavolar.adapters.LegendsDefaultAdapter;
import com.clickaboom.letrasparavolar.fragments.ColeccionesFragment;
import com.clickaboom.letrasparavolar.fragments.InformationFragment;
import com.clickaboom.letrasparavolar.fragments.LegendsFragment;
import com.clickaboom.letrasparavolar.fragments.LibraryFragment;
import com.clickaboom.letrasparavolar.models.banners.Banner;
import com.clickaboom.letrasparavolar.models.banners.ResBanners;
import com.clickaboom.letrasparavolar.models.collections.Colecciones;
import com.clickaboom.letrasparavolar.models.defaults.ResDefaults;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.clickaboom.letrasparavolar.network.DownloadFile;
import com.clickaboom.letrasparavolar.network.GsonRequest;
import com.clickaboom.letrasparavolar.network.SQLiteDBHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, DownloadFile.OnTaskCompleted {

    public RelativeLayout legendsBtn, collectionsBtn, libraryBtn;
    public static List<String> mLocalEpubsList = new ArrayList<>();

    private static final String TAG = "com.lpv.MainActivity";
    private RecyclerView mRecyclerView, mRecyclerView2;
    private LinearLayoutManager mLayoutManager;
    private ColeccionesDefaultAdapter mCollectionsAdapter;
    private LegendsDefaultAdapter mLegendsAdapter;
    private ViewPager view1;
    private BannerPagerAdapter mBannerAdapter;
    private ImageView image;
    private LinearLayoutManager mLayoutManager2;
    private List<Banner> mBannerItems = new ArrayList<>();
    private Context mContext;
    private List<Colecciones> mLegendsList = new ArrayList<>(), mCollectionsList = new ArrayList<>();;
    public static SQLiteDBHelper db;
    private DownloadFile.OnTaskCompleted mDownloadsListener;
    private ArrayList<String> mDefaultList = new ArrayList<>();
    public static DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* TODO: Remove NukeSSL in Release version as it can cause severe security issues */
        /*new NukeSSLCerts().nuke();*/

        mContext = this;
        mDownloadsListener = this;

        db = SQLiteDBHelper.getInstance(this);

        // Lateral Navigation View
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Drawer to control navigationView opening and closing
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Click listener of drawer button
        findViewById(R.id.drawer_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open right drawer
                drawer.openDrawer(GravityCompat.END);
            }
        });

        // Set toolbar_asistant title
        ((TextView)findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.news_title));
        findViewById(R.id.left_btn).setVisibility(View.GONE);
        findViewById(R.id.right_btn).setVisibility(View.GONE);

        legendsBtn = (RelativeLayout)findViewById(R.id.legends_btn);
        legendsBtn.setOnClickListener(this);

        collectionsBtn = (RelativeLayout)findViewById(R.id.collections_btn);
        collectionsBtn.setOnClickListener(this);

        libraryBtn = (RelativeLayout)findViewById(R.id.library_btn);
        libraryBtn.setOnClickListener(this);

        image = (ImageView)findViewById(R.id.mi_barquitoo);
        image.post(new Runnable() {
            @Override
            public void run() {
                animateBarquito(image);
            }
        });

        mRecyclerView = (RecyclerView)findViewById(R.id.home_recycler);
        mRecyclerView2 = (RecyclerView)findViewById(R.id.home_recycler2);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // use a linear layout manager
        mLayoutManager2 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView2.setLayoutManager(mLayoutManager2);

        // specify an adapter (see also next example)
        mLegendsAdapter = new LegendsDefaultAdapter(mLegendsList, mContext);
        mLegendsAdapter.mColType = BookDetailsActivity.LEGENDS;
        mRecyclerView.setAdapter(mLegendsAdapter);

        mCollectionsAdapter = new ColeccionesDefaultAdapter(mCollectionsList, mContext);
        mCollectionsAdapter.mColType = BookDetailsActivity.COLECCIONES;
        mRecyclerView2.setAdapter(mCollectionsAdapter);

        copyFileOrDir("epub_reader");

        view1 = (ViewPager)findViewById(R.id.banner);
        view1.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int index) {
                Log.v( "onPageSelected", String.valueOf( index ) );
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // Log.v("onPageScrolled", "");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.v("onPageScrollStateCh", String.valueOf(state));

                if (state ==ViewPager.SCROLL_STATE_IDLE) {
                    int index = view1.getCurrentItem();
                    if ( index == 0 ) {
                        view1.setCurrentItem(mBannerAdapter.getCount() - 2, false);
                    } else if ( index == mBannerAdapter.getCount() - 1 )
                        view1.setCurrentItem( 1 , false);
                }
            }
        });

        mBannerAdapter = new BannerPagerAdapter(getApplicationContext(), mBannerItems);
        view1.setAdapter(mBannerAdapter);
        loadBanners();

        mLocalEpubsList = getDownloadedEpubs();
        Log.d("MainActivity", mLocalEpubsList.toString());
    }
    
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            /*// If there're available fragments pop them from backstack
            // by overriding the backbutton
            FragmentManager fm = getSupportFragmentManager();
            if(fm.getBackStackEntryCount() > 0) {
                super.onBackPressed();
            }

            // If there aren't any fragments in backstack then
            // unselect all bottomNavView buttons
            if(fm.getBackStackEntryCount() == 0) {
                restoreBottonNavColors();
            }*/
            // Pop all fragments from backstack
            FragmentManager fm = getSupportFragmentManager();
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            restoreBottonNavColors();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    public void restoreBottonNavColors() {
        legendsBtn.setBackgroundColor(Color.TRANSPARENT);
        collectionsBtn.setBackgroundColor(Color.TRANSPARENT);
        libraryBtn.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onClick(View v) {
        // Set selected button
        restoreBottonNavColors();
        //v.setBackgroundColor(getResources().getColor(R.color.bottom_nav_pressed));

        switch (v.getId()) {
            case R.id.legends_btn:
                presentFragment(LegendsFragment.newInstance());
                break;
            case R.id.collections_btn:
                presentFragment(ColeccionesFragment.newInstance());
                break;
            case R.id.library_btn:
                Fragment libFrag = new LibraryFragment();
                presentFragment(libFrag);
                break;
        }
    }

    public void navOnClick(View v) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        switch (v.getId()) {
            case R.id.info_btn:
                Fragment programInfoFragmentFrag = new InformationFragment();
                replaceFragment(programInfoFragmentFrag);
                break;
            case R.id.news_btn:
                Toast.makeText(getApplicationContext(), "news_btn", Toast.LENGTH_SHORT).show();
                break;
            case R.id.games_btn:
                startActivity(JuegosActivity.newIntent(mContext));
                break;
            case R.id.participate_btn:
                startActivity(ParticipaActivity.newIntent(mContext));
                break;
            case R.id.portal_btn:
                startActivity(WebviewActivity.newIntent(
                        getApplicationContext(),
                        "http://letrasparavolar.org/"));
                break;
            case R.id.magazine_btn:
                startActivity(GacetitaActivity.newIntent(mContext));
                break;
        }
    }

    /*public void replaceFragment(Fragment fragment){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(R.id.fragment_container, fragment, backStateName);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }*/

    private void replaceFragment (Fragment fragment){
        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    public void presentFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        // re-use the old fragment
        if (manager.findFragmentByTag(backStateName) == null){
            ft.add(R.id.fragment_container, fragment);
            ft.addToBackStack(backStateName);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.show(fragment);
        } else {
            manager.popBackStack(backStateName, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        ft.commit();
    }

    public static void addFragment(Fragment fragment, FragmentActivity activity){
        String backStateName = fragment.getClass().getName();
        FragmentManager manager = activity.getSupportFragmentManager();

        FragmentTransaction ft = manager.beginTransaction();
        //ft.hide(manager.findFragmentById(R.id.fragment_container));
        ft.add(R.id.fragment_container, fragment);
        ft.addToBackStack(backStateName);
        ft.commit();

    }

    public int windowWidth() {
        int width = 0, height = 0;
        final DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        Method mGetRawH = null, mGetRawW = null;

        try {
            // For JellyBean 4.2 (API 17) and onward
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealMetrics(metrics);

                width = metrics.widthPixels;
                height = metrics.heightPixels;
            } else {
                mGetRawH = Display.class.getMethod("getRawHeight");
                mGetRawW = Display.class.getMethod("getRawWidth");

                try {
                    width = (Integer) mGetRawW.invoke(display);
                    height = (Integer) mGetRawH.invoke(display);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        }

        return width;
    }

    private void loadLegends() {

        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(mContext)
                .addToRequestQueue(new GsonRequest(ApiConfig.legendsDefaults,
                        ResDefaults.class,
                        Request.Method.GET,
                        null, null,
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                Log.d(TAG, response.toString());
                                List<Colecciones> res = ((ResDefaults) response).data;

                                mLegendsList.clear();
                                mLegendsList.addAll(res);

                                mLegendsAdapter.notifyDataSetChanged();
                                for(Colecciones item: mLegendsList) {
                                    item.favorito = false;
                                    item.mBookType = BookDetailsActivity.LEGENDS;
                                    descargar(ApiConfig.epubs + item.epub, item.epub, item);
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

    private void loadCollections() {

        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(mContext)
                .addToRequestQueue(new GsonRequest(ApiConfig.collectionsDefaults,
                        ResDefaults.class,
                        Request.Method.GET,
                        null, null,
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                Log.d(TAG, response.toString());
                                List<Colecciones> res = ((ResDefaults) response).data;

                                mCollectionsList.clear();
                                mCollectionsList.addAll(res);

                                mCollectionsAdapter.notifyDataSetChanged();
                                for(Colecciones item: mCollectionsList) {
                                    item.favorito = false;
                                    item.mBookType = BookDetailsActivity.COLECCIONES;
                                    descargar(ApiConfig.epubs + item.epub, item.epub, item);
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
                                if(book.mBookType.equals(BookDetailsActivity.COLECCIONES))
                                    mCollectionsList.add(book);
                            }
                            Toast.makeText(mContext, "Sin conexión", Toast.LENGTH_SHORT).show();
                        }
                        mCollectionsAdapter.notifyDataSetChanged();
                    }
                }));

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
                                mBannerItems.clear();

                                // Repeat last and first values to make an infinite ViewPager
                                mBannerItems.add(res.get(res.size()-1));
                                mBannerItems.addAll(res); // Add main book to list
                                mBannerItems.add(res.get(0));

                                mBannerAdapter.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }));

    }

    private void animateBarquito(final ImageView image) {
        final float startX = 0; //start position
        final float endX = windowWidth() - image.getWidth(); //end position - right edge of the parent
        image.animate().translationX(endX).withEndAction(new Runnable() {
            @Override
            public void run() {
                image.animate().translationX(startX).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        animateBarquito(image);
                    }
                }).start();
            }
        }).setDuration(6000).start();
    }

    @Override
    public void onTaskCompleted() {

    }

    public void backToMain() {
        // Clear all back stack.
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        for (int i = 0; i <= backStackCount; i++) {
            // Get the back stack fragment id.
            int backStackId = getSupportFragmentManager().getBackStackEntryAt(i).getId();
            getFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        } /* end of for */
    }

    public static class NukeSSLCerts {
        protected static final String TAG = "NukeSSLCerts";

        public static void nuke() {
            try {
                TrustManager[] trustAllCerts = new TrustManager[] {
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                                return myTrustedAnchors;
                            }

                            @Override
                            public void checkClientTrusted(X509Certificate[] certs, String authType) {}

                            @Override
                            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                        }
                };

                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }
    }

    public static String getStringFromListByCommas(List<?> list){
        String answer = "";
        for(int i = 0; i < list.size(); i++){
            answer = answer + list.get(i);
            if(i != list.size()-1){
                answer = answer + ",";
            }
        }
        return answer;
    }

    private static List<String> getDownloadedEpubs() {
        List<String> epubsList = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory() + "/LPV_eBooks/";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files != null) {
            Log.d("Files", "Size: " + files.length);
            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getName());
                epubsList.add(files[i].getName());
            }
        }
        return epubsList;
    }

    public void descargar(String url, String fileName, Colecciones ePub){
        DownloadFile downloadFile = new DownloadFile(mDownloadsListener, mContext, MainActivity.this, false, ePub);
        downloadFile.execute(url, fileName, fileName);
    }

    final static String TARGET_BASE_PATH = "/sdcard/LPV_eBooks/";

    private void copyFilesToSdCard() {
        copyFileOrDir(""); // copy all files in assets folder in my project
    }

    public void copyFileOrDir(String path) {
        AssetManager assetManager = this.getAssets();
        String assets[] = null;
        try {
            Log.i("tag", "copyFileOrDir() "+path);
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(path);
            } else {
                String fullPath =  TARGET_BASE_PATH + path;
                Log.i("tag", "path="+fullPath);
                File dir = new File(fullPath);
                if (!dir.exists() && !path.startsWith("images") && !path.startsWith("sounds") && !path.startsWith("webkit"))
                    if (!dir.mkdirs())
                        Log.i("tag", "could not create dir "+fullPath);
                for (int i = 0; i < assets.length; ++i) {
                    String p;
                    if (path.equals(""))
                        p = "";
                    else
                        p = path + "/";

                    if (!path.startsWith("images") && !path.startsWith("sounds") && !path.startsWith("webkit"))
                        copyFileOrDir( p + assets[i]);
                }

                loadLegends();
                loadCollections();

            }
        } catch (IOException ex) {
            Log.e("tag", "I/O Exception", ex);
        }
    }

    private void copyFile(String filename) {
        AssetManager assetManager = this.getAssets();

        InputStream in = null;
        OutputStream out = null;
        String newFileName = null;
        try {
            Log.i("tag", "copyFile() "+filename);
            in = assetManager.open(filename);
            if (filename.endsWith(".jpg")) // extension was added to avoid compression on APK file
                newFileName = TARGET_BASE_PATH + filename.substring(0, filename.length()-4);
            else
                newFileName = TARGET_BASE_PATH + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", "Exception in copyFile() of "+newFileName);
            Log.e("tag", "Exception in copyFile() "+e.toString());
        }

    }
}
