package com.clickaboom.letrasparavolar.activities;

import android.graphics.Color;
import android.os.Bundle;
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
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.adapters.BannerPagerAdapter;
import com.clickaboom.letrasparavolar.adapters.BooksHomeAdapter;
import com.clickaboom.letrasparavolar.fragments.CollectionsFragment;
import com.clickaboom.letrasparavolar.fragments.LegendsFragment;
import com.clickaboom.letrasparavolar.models.Book;

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
        View.OnClickListener {

    private RelativeLayout legendsBtn, collectionsBtn, libraryBtn;
    private Book mBook;
    private RecyclerView mRecyclerView, mRecyclerView2;
    private LinearLayoutManager mLayoutManager;
    private BooksHomeAdapter mAdapter;
    private List<Book> mBooksList = new ArrayList<>();
    private ViewPager view1;
    private BannerPagerAdapter mBannerAdapter;
    private ImageView image;
    private LinearLayoutManager mLayoutManager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* TODO: Remove NukeSSL in Release version as it can cause severe security issues */
        new NukeSSLCerts().nuke();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        findViewById(R.id.drawer_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open right drawer
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.END);
            }
        });

        legendsBtn = (RelativeLayout)findViewById(R.id.legends_btn);
        legendsBtn.setOnClickListener(this);

        collectionsBtn = (RelativeLayout)findViewById(R.id.collections_btn);
        collectionsBtn.setOnClickListener(this);

        libraryBtn = (RelativeLayout)findViewById(R.id.library_btn);
        libraryBtn.setOnClickListener(this);

        mRecyclerView = (RecyclerView)findViewById(R.id.home_recycler);
        mRecyclerView2 = (RecyclerView)findViewById(R.id.home_recycler2);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set toolbar_asistant title
        ((TextView)findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.news_title));
        findViewById(R.id.left_btn).setVisibility(View.GONE);
        findViewById(R.id.right_bnt).setVisibility(View.GONE);

        image = (ImageView)findViewById(R.id.mi_barquitoo);
        image.post(new Runnable() {
            @Override
            public void run() {
                animateBarquito(image);
            }
        });

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // use a linear layout manager
        mLayoutManager2 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView2.setLayoutManager(mLayoutManager2);

        // specify an adapter (see also next example)
        mAdapter = new BooksHomeAdapter(mBooksList, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView2.setAdapter(mAdapter);

        loadBooks();

        view1 = (ViewPager)findViewById(R.id.banner);

        List<String> bannerItems = new ArrayList<>();
        bannerItems.add("http://letrasparavolar.org/wp-content/themes/nuevas_letras/slider/images/cajadeletras-01.png");
        bannerItems.add("http://letrasparavolar.org/wp-content/themes/nuevas_letras/slider/images/app.png");
        bannerItems.add("http://letrasparavolar.org/wp-content/themes/nuevas_letras/slider/images/RED_1000x250.jpg");

        mBannerAdapter = new BannerPagerAdapter(getApplicationContext(), bannerItems);
        view1.setAdapter(mBannerAdapter);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        restoreBottonNavColors();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else if (getSupportFragmentManager().findFragmentByTag("FragmentC") != null) {
            // I'm viewing Fragment C
            getSupportFragmentManager().popBackStack("A_B_TAG",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            super.onBackPressed();
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



    private void restoreBottonNavColors() {
        legendsBtn.setBackgroundColor(Color.TRANSPARENT);
        collectionsBtn.setBackgroundColor(Color.TRANSPARENT);
        libraryBtn.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onClick(View v) {
        // Set selected button
        restoreBottonNavColors();
        v.setBackgroundColor(getResources().getColor(R.color.bottom_nav_pressed));

        // show bottom greca
        //bottomOrnament.setVisibility(View.VISIBLE);

        switch (v.getId()) {
            case R.id.legends_btn:
                Fragment legFrag = new LegendsFragment();
                replaceFragment(legFrag, MainActivity.this);
                break;
            case R.id.collections_btn:
                Fragment colFrag = new CollectionsFragment();
                replaceFragment(colFrag, MainActivity.this);
                break;
            case R.id.library_btn:
                break;
        }
    }

    public static void replaceFragment (Fragment fragment, FragmentActivity activity){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = activity.getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
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

    private void loadBooks() {
        for(int i=0; i<4; i ++) {
            mBooksList.add(new Book("Los primeros dioses" + i, "Leyenda popular " + i, R.drawable.test_1));
            mAdapter.notifyItemInserted(i);
        }
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
}