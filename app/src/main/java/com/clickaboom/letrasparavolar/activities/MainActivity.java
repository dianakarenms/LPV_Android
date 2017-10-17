package com.clickaboom.letrasparavolar.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.IntentCompat;
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
import android.widget.ImageButton;
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
import com.clickaboom.letrasparavolar.services.MyFirebaseInstanceIDService;
import com.google.firebase.iid.FirebaseInstanceId;

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

import static com.clickaboom.letrasparavolar.network.ApiConfig.epubs;
import static com.clickaboom.letrasparavolar.network.DownloadFile.isStoragePermissionGranted;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, DownloadFile.OnTaskCompleted {

    public static final String EXTRA_BOOK_ITEM = "com.lpv.bookItem";
    public static final String EXTRA_OPEN_GAMES = "com.lpv.isGameAction";
    public RelativeLayout legendsBtn, collectionsBtn, libraryBtn;
    public static List<String> mLocalEpubsList = new ArrayList<>();

    private static final String TAG = "com.lpv.MainActivity";
    private RecyclerView mLegendsRV, mColeccionesRV;
    private LinearLayoutManager mLegendsManager;
    private ColeccionesDefaultAdapter mCollectionsAdapter;
    private LegendsDefaultAdapter mLegendsAdapter;
    private ViewPager mBannerPager;
    private BannerPagerAdapter mBannerAdapter;
    private ImageView image;
    private LinearLayoutManager mColeccionsManager;
    private List<Banner> mBannerItems = new ArrayList<>();
    private Context mContext;
    private List<Colecciones> mLegendsList = new ArrayList<>(), mColectionesList = new ArrayList<>();;
    public static SQLiteDBHelper db;
    private DownloadFile.OnTaskCompleted mDownloadsListener;
    private ArrayList<String> mDefaultList = new ArrayList<>();
    public static DrawerLayout drawer;
    private LibraryFragment mLibraryFragment;
    private ColeccionesFragment mColeccionesFragment;
    private LegendsFragment mLeyendasFragment;
    private boolean doubleBackToExitPressedOnce = false;
    private boolean isHomeVisible = true;
    private int mBooksLoaded = 0;
    public static Colecciones mIntentBook;
    private TabLayout mTabLayout;
    private ImageButton mLegendsPrevBtn, mLegendsNextBtn, mColeccionesNextBtn, mColeccionesPrevBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* TODO: Remove NukeSSL in Release version as it can cause severe security issues */
        /*new NukeSSLCerts().nuke();*/

        mContext = this;
        mDownloadsListener = this;

        mIntentBook = (Colecciones) getIntent().getSerializableExtra(EXTRA_BOOK_ITEM);

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
        findViewById(R.id.toolbar_prev_btn).setVisibility(View.GONE);
        findViewById(R.id.toolbar_next_btn).setVisibility(View.GONE);

        legendsBtn = (RelativeLayout)findViewById(R.id.legends_btn);
        legendsBtn.setOnClickListener(this);

        collectionsBtn = (RelativeLayout)findViewById(R.id.collections_btn);
        collectionsBtn.setOnClickListener(this);

        libraryBtn = (RelativeLayout)findViewById(R.id.library_btn);
        libraryBtn.setOnClickListener(this);

        mLegendsNextBtn = (ImageButton) findViewById(R.id.leyendas_next_btn);
        mLegendsPrevBtn = (ImageButton) findViewById(R.id.leyendas_prev_btn);
        mColeccionesNextBtn = (ImageButton) findViewById(R.id.colecciones_next_btn);
        mColeccionesPrevBtn = (ImageButton) findViewById(R.id.colecciones_prev_btn);

        image = (ImageView)findViewById(R.id.mi_barquitoo);
        image.post(new Runnable() {
            @Override
            public void run() {
                animateBarquito(image);
            }
        });

        mLegendsRV = (RecyclerView)findViewById(R.id.leyendas_rv);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mLegendsRV.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    setScrollArrowsState(mLegendsManager, mLegendsList, mLegendsNextBtn, mLegendsPrevBtn);
                }
            });
        } else {
            mLegendsRV.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    setScrollArrowsState(mLegendsManager, mLegendsList, mLegendsNextBtn, mLegendsPrevBtn);
                }
            });
        }
        mColeccionesRV = (RecyclerView)findViewById(R.id.colecciones_rv);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mColeccionesRV.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    setScrollArrowsState(mColeccionsManager, mColectionesList, mColeccionesNextBtn, mColeccionesPrevBtn);
                }
            });
        } else {
            mColeccionesRV.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    setScrollArrowsState(mColeccionsManager, mColectionesList, mColeccionesNextBtn, mColeccionesPrevBtn);
                }
            });
        }

        // use a linear layout manager
        mLegendsManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mLegendsRV.setLayoutManager(mLegendsManager);
        // use a linear layout manager
        mColeccionsManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mColeccionesRV.setLayoutManager(mColeccionsManager);

        // specify an adapter (see also next example)
        mLegendsAdapter = new LegendsDefaultAdapter(mLegendsList, mContext);
        mLegendsAdapter.mColType = BookDetailsActivity.LEGENDS;
        mLegendsRV.setAdapter(mLegendsAdapter);

        mCollectionsAdapter = new ColeccionesDefaultAdapter(mColectionesList, mContext);
        mCollectionsAdapter.mColType = BookDetailsActivity.COLECCIONES;
        mColeccionesRV.setAdapter(mCollectionsAdapter);

        copyFileOrDir("epub_reader");

        mBannerPager = (ViewPager)findViewById(R.id.banner);
        mBannerPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

                /*// Infinite loop
                if (state ==ViewPager.SCROLL_STATE_IDLE) {
                    int index = mBannerPager.getCurrentItem();
                    if ( index == 0 ) {
                        mBannerPager.setCurrentItem(mBannerAdapter.getCount() - 2, false);
                    } else if ( index == mBannerAdapter.getCount() - 1 )
                        mBannerPager.setCurrentItem( 1 , false);
                }*/
            }
        });
        mTabLayout = (TabLayout) findViewById(R.id.tabDots);
        mTabLayout.setupWithViewPager(mBannerPager, true);
        mBannerAdapter = new BannerPagerAdapter(mContext, mBannerItems);
        mBannerPager.setAdapter(mBannerAdapter);
        loadBanners();

//        mLocalEpubsList = getDownloadedEpubs();
        getLocalEpubs(false);
        Log.d("MainActivity", mLocalEpubsList.toString());

    }

    private static void setScrollArrowsState(LinearLayoutManager manager, List<Colecciones> list, ImageButton nextBtn, ImageButton prevBtn) {
        int firstPos = manager.findFirstCompletelyVisibleItemPosition();
        int lastPos = manager.findLastCompletelyVisibleItemPosition();
        int size = list.size();

        if(firstPos > 0 && lastPos < size-1) {
            nextBtn.setVisibility(View.VISIBLE);
            prevBtn.setVisibility(View.VISIBLE);
        } else if(lastPos == size-1) {
            // NextPressed
            nextBtn.setVisibility(View.INVISIBLE);
            prevBtn.setVisibility(View.VISIBLE);
        } else if(firstPos == 0) {
            // PrevPressed
            nextBtn.setVisibility(View.VISIBLE);
            prevBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Get token
        String token = FirebaseInstanceId.getInstance().getToken();
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        MyFirebaseInstanceIDService.sendRegistrationToServer(token, deviceId, mContext);
    }

    @Override
    protected void onPause() {
        super.onPause();
        doubleBackToExitPressedOnce = false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            } else {
                if(!isHomeVisible) {
                    // Pop all fragments from backstack
                    FragmentManager fm = getSupportFragmentManager();
                    fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    restoreBottonNavColors();

                    // Check if there were more epubs downloaded and show them in homeFragment
                    getLocalEpubs(true);

                    isHomeVisible = true;
                } else {
                    if (doubleBackToExitPressedOnce) {
                        super.onBackPressed();
                        return;
                    }

                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, "Presiona de nuevo para salir", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 5000);
                }
            }
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
        doubleBackToExitPressedOnce = false;
        isHomeVisible = false;

        switch (v.getId()) {
            case R.id.legends_btn:
                if(mLeyendasFragment == null)
                    mLeyendasFragment = LegendsFragment.newInstance(mIntentBook);
                presentFragment(mLeyendasFragment);
                legendsBtn.setBackgroundColor(getResources().getColor(R.color.bottom_nav_pressed));
                break;
            case R.id.collections_btn:
                if(mColeccionesFragment == null)
                    mColeccionesFragment = ColeccionesFragment.newInstance(mIntentBook);
                collectionsBtn.setBackgroundColor(getResources().getColor(R.color.bottom_nav_pressed));
                presentFragment(mColeccionesFragment);
                break;
            case R.id.library_btn:
                if(mLibraryFragment == null)
                    mLibraryFragment = new LibraryFragment();
                presentFragment(mLibraryFragment);
                libraryBtn.setBackgroundColor(getResources().getColor(R.color.bottom_nav_pressed));
                break;
        }
    }

    public void navOnClick(View v) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        switch (v.getId()) {
            case R.id.info_btn:
                startActivity(InformationActivity.newIntent(mContext));
                break;
            case R.id.news_btn:
                startActivity(NoticiasActivity.newIntent(mContext));
                break;
            case R.id.games_btn:
                startActivity(JuegosMenuActivity.newIntent(mContext));
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

    public void arrowsNavOnClick(View v) {
        int scrollPos = 0;
        switch (v.getId()) {
            case R.id.leyendas_next_btn:
                scrollPos = mLegendsManager.findLastCompletelyVisibleItemPosition() + 3;
                if(scrollPos >= mLegendsList.size())
                    scrollPos = mLegendsList.size() - 1;
                mLegendsRV.smoothScrollToPosition(scrollPos);
                break;
            case R.id.leyendas_prev_btn:
                scrollPos = mLegendsManager.findLastCompletelyVisibleItemPosition() - 3;
                if(scrollPos < 0)
                    scrollPos = 0;
                mLegendsRV.smoothScrollToPosition(scrollPos);
                break;
            case R.id.colecciones_next_btn:
                scrollPos = mColeccionsManager.findLastCompletelyVisibleItemPosition() + 3;
                if(scrollPos >= mColectionesList.size())
                    scrollPos = mColectionesList.size() - 1;
                mColeccionesRV.smoothScrollToPosition(scrollPos);
                break;
            case R.id.colecciones_prev_btn:
                scrollPos = mColeccionsManager.findLastCompletelyVisibleItemPosition() - 3;
                if(scrollPos < 0)
                    scrollPos = 0;
                mColeccionesRV.smoothScrollToPosition(scrollPos);
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

    public void presentFragment (Fragment fragment){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped && !fragment.isVisible()){ //mMainPagerFrag not in back stack and not visible.
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(R.id.fragment_container, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }


    /*public void presentFragment(Fragment fragment) {
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
    }*/

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

                                for(Colecciones book: res) {
                                    if(!mLegendsList.contains(book)) {
                                        mLegendsList.add(0, book);
                                    }
                                }
                                //mLegendsList.clear();
//                                mLegendsList.addAll(res);

                                mLegendsAdapter.notifyDataSetChanged();
                                downloadDefaultBooks();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                        /*ArrayList<Colecciones> allBooks = db.getAllBooks();
                        if(allBooks.isEmpty())
                            Toast.makeText(mContext, "Error de conexión", Toast.LENGTH_SHORT).show();
                        else {
                            for(Colecciones book: allBooks) {
                                if(book.mBookType.equals(BookDetailsActivity.LEGENDS))
                                    mLegendsList.add(book);
                            }*/

                            Toast.makeText(mContext, "Sin conexión a internet", Toast.LENGTH_SHORT).show();
                        //}
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

                                for(Colecciones book: res) {
                                    if(!mColectionesList.contains(book)) {
                                        mColectionesList.add(0, book);
                                    }
                                }
//                                mColectionesList.clear();
//                                mColectionesList.addAll(res);

                                mCollectionsAdapter.notifyDataSetChanged();
                                downloadDefaultBooks();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                        /*ArrayList<Colecciones> allBooks = db.getAllBooks();
                        if(allBooks.isEmpty())
                            Toast.makeText(mContext, "Error de conexión", Toast.LENGTH_SHORT).show();
                        else {
                            for(Colecciones book: allBooks) {
                                if(book.mBookType.equals(BookDetailsActivity.COLECCIONES))
                                    mColectionesList.add(book);
                            }*/
                            Toast.makeText(mContext, "Sin conexión a internet", Toast.LENGTH_SHORT).show();
//                        }
                        mCollectionsAdapter.notifyDataSetChanged();
                    }
                }));

    }

    private void downloadDefaultBooks() {
        mBooksLoaded++;
        if(mBooksLoaded == 2) {
            if(isStoragePermissionGranted(MainActivity.this)) {
                recursiveDownload();
            }
        }
    }

    private void recursiveDownload() {
        for (Colecciones item : mLegendsList) {
            item.favorito = false;
            item.mBookType = BookDetailsActivity.LEGENDS;
            descargar(epubs + item.epub, item.epub, item);
        }

        for (Colecciones item : mColectionesList) {
            item.favorito = false;
            item.mBookType = BookDetailsActivity.COLECCIONES;
            descargar(epubs + item.epub, item.epub, item);
        }

        checkIntentData();
    }

    private void checkIntentData() {
        // Checks if a downloadBook or opening GamesActivity was requested.
        if(mIntentBook != null) {
            if(mIntentBook.mBookType.equals(BookDetailsActivity.LEGENDS)) {
                legendsBtn.performClick();
            } else if(mIntentBook.mBookType.equals(BookDetailsActivity.COLECCIONES)) {
                collectionsBtn.performClick();
            }
        } else if(getIntent().getBooleanExtra(EXTRA_OPEN_GAMES, false)) {
            getIntent().putExtra(MainActivity.EXTRA_OPEN_GAMES, false);
            findViewById(R.id.games_btn).performClick();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);

            Toast.makeText(mContext, "Habilitando permisos...", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Restart the app since in order to change the ID the process
                    // has to be restarted. Next time you open the app,
                    // the new groupID is set and the permission is granted.
                    PackageManager packageManager = getPackageManager();
                    Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
                    ComponentName componentName = intent.getComponent();
                    Intent mainIntent = IntentCompat.makeRestartActivityTask(componentName);
                    startActivity(mainIntent);
                    System.exit(0);
                }
            }, 2000);

        } else {
            Toast.makeText(mContext,
                    "Habilite permiso de \"almacenamiento local\" para visualizar los epubs",
                    Toast.LENGTH_LONG)
                    .show();
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
                                mBannerItems.clear();

                                // Repeat last and first values to make an infinite ViewPager
//                                mBannerItems.add(res.get(res.size()-1));
                                mBannerItems.addAll(res); // Add main book to list
//                                mBannerItems.add(res.get(0));

                                mBannerAdapter.notifyDataSetChanged();

//                                mTabLayout.removeTabAt(0);
//                                mTabLayout.removeTabAt(res.size()-1);
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

    private List<String> getDownloadedEpubs() {
        List<String> epubsList = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory() + "/LPV_eBooks/epub_reader/epubs/";
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

    private void getLocalEpubs(boolean showLastAddedBooks) {
        // Clear previous data
        mLegendsList.clear();
        mColectionesList.clear();

        ArrayList<Colecciones> mArrayList = db.getAllBooks();
        if (mArrayList != null) { // If there's data available in the db
            for (Colecciones book : mArrayList) {
                if (book.mBookType.equals(BookDetailsActivity.LEGENDS) && book.descargado)
                    mLegendsList.add(book);
                else if (book.mBookType.equals(BookDetailsActivity.COLECCIONES) && book.descargado)
                    mColectionesList.add(book);
            }
        }

        mLegendsAdapter.notifyDataSetChanged();
        mCollectionsAdapter.notifyDataSetChanged();

        /*if(showLastAddedBooks) {
            mLegendsRV.scrollToPosition(mLegendsList.size() - 1);
            mColeccionesRV.scrollToPosition(mColectionesList.size() - 1);
        }*/

        loadLegends();
        loadCollections();
    }

    public void descargar(String url, String fileName, Colecciones ePub){
        String basePath = Environment.getExternalStorageDirectory() + "/LPV_eBooks/epub_reader/epubs/" + ePub.epub;
        File file = new File(basePath);
        if(!file.exists()) {
            DownloadFile downloadFile = new DownloadFile(mDownloadsListener, mContext, MainActivity.this, false, ePub);
            String fileFolder = fileName.replace(".epub", "");
            downloadFile.execute(url, fileFolder, fileName);
        } else {
            // if localStored
            if(db.getBookByePub(ePub.epub).isEmpty()) {
                // Epub was already downloaded but not yet added to database
                ePub.descargado = true;
                ePub.favorito = false;
                if (db.insertBook(ePub)) {
                    Log.d("ebookContent", "stored in db");
                }
            }
        }

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
