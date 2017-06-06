package com.clickaboom.letrasparavolar.activities;

import android.content.Context;
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
import com.clickaboom.letrasparavolar.adapters.CollectionsDefaultAdapter;
import com.clickaboom.letrasparavolar.adapters.LegendsDefaultAdapter;
import com.clickaboom.letrasparavolar.fragments.CollectionsFragment;
import com.clickaboom.letrasparavolar.fragments.LegendsFragment;
import com.clickaboom.letrasparavolar.fragments.LibraryFragment;
import com.clickaboom.letrasparavolar.fragments.ProgramInfoFragment;
import com.clickaboom.letrasparavolar.models.Book;
import com.clickaboom.letrasparavolar.models.banners.Banner;
import com.clickaboom.letrasparavolar.models.banners.ResBanners;
import com.clickaboom.letrasparavolar.models.collections.Collections;
import com.clickaboom.letrasparavolar.models.defaults.ResDefaults;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.clickaboom.letrasparavolar.network.GsonRequest;

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

    private static final String TAG = "com.lpv.MainActivity";
    public RelativeLayout legendsBtn, collectionsBtn, libraryBtn;
    private RecyclerView mRecyclerView, mRecyclerView2;
    private LinearLayoutManager mLayoutManager;
    private CollectionsDefaultAdapter mCollectionsAdapter;
    private LegendsDefaultAdapter mLegendsAdapter;
    private List<Book> mBooksList = new ArrayList<>();
    private ViewPager view1;
    private BannerPagerAdapter mBannerAdapter;
    private ImageView image;
    private LinearLayoutManager mLayoutManager2;
    private List<Banner> mBannerItems = new ArrayList<>();
    private Context mContext;
    private List<Collections> mLegendsList = new ArrayList<>(), mCollectionsList = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* TODO: Remove NukeSSL in Release version as it can cause severe security issues */
        new NukeSSLCerts().nuke();

        mContext = this;

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

        //Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // Set toolbar_asistant title
        ((TextView)findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.news_title));
        findViewById(R.id.left_btn).setVisibility(View.GONE);
        findViewById(R.id.right_btn).setVisibility(View.GONE);

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
        mLegendsAdapter = new LegendsDefaultAdapter(mLegendsList, mContext);
        mRecyclerView.setAdapter(mLegendsAdapter);

        mCollectionsAdapter = new CollectionsDefaultAdapter(mCollectionsList, mContext);
        mRecyclerView2.setAdapter(mCollectionsAdapter);

        loadLegends();
        loadCollections();

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
            // If there're available fragments pop them from backstack
            // by overriding the backbutton
            FragmentManager fm = getSupportFragmentManager();
            if(fm.getBackStackEntryCount() > 0) {
                super.onBackPressed();
            }

            // If there aren't any fragments in backstack then
            // unselect all bottomNavView buttons
            if(fm.getBackStackEntryCount() == 0) {
                restoreBottonNavColors();
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
        //v.setBackgroundColor(getResources().getColor(R.color.bottom_nav_pressed));

        switch (v.getId()) {
            case R.id.legends_btn:
                replaceFragment(LegendsFragment.newInstance());
                break;
            case R.id.collections_btn:
                replaceFragment(CollectionsFragment.newInstance());
                break;
            case R.id.library_btn:
                Fragment libFrag = new LibraryFragment();
                replaceFragment(libFrag);
                break;
        }
    }

    public void navOnClick(View v) {
        switch (v.getId()) {
            case R.id.info_btn:
                Toast.makeText(getApplicationContext(), "info_btn", Toast.LENGTH_SHORT).show();
                Fragment programInfoFragmentFrag = new ProgramInfoFragment();
                replaceFragment(programInfoFragmentFrag);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.END);
                break;
            case R.id.news_btn:
                Toast.makeText(getApplicationContext(), "news_btn", Toast.LENGTH_SHORT).show();
                break;
            case R.id.games_btn:
                Toast.makeText(getApplicationContext(), "games_btn", Toast.LENGTH_SHORT).show();
                break;
            case R.id.participate_btn:
                Toast.makeText(getApplicationContext(), "participate_btn", Toast.LENGTH_SHORT).show();
                break;
            case R.id.portal_btn:
                startActivity(WebviewActivity.newIntent(
                        getApplicationContext(),
                        "http://letrasparavolar.org/"));
                break;
            case R.id.magazine_btn:
                Toast.makeText(getApplicationContext(), "magazine_btn", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void replaceFragment(Fragment fragment){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, backStateName);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
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
                                List<Collections> res = ((ResDefaults) response).data;

                                mLegendsList.clear();
                                mLegendsList.addAll(res);
                                /*for(Collections item : res) {
                                    mLegendsList.add(item); // Add main book to list
                                }
*/                                mLegendsAdapter.notifyDataSetChanged();
                                //mNestedScroll.scrollTo(0, 0);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
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
                                List<Collections> res = ((ResDefaults) response).data;

                                mCollectionsList.clear();
                                mCollectionsList.addAll(res);
                                /*for(Collections item : res) {
                                    mCollectionsList.add(item); // Add main book to list
                                }*/

                                mCollectionsAdapter.notifyDataSetChanged();
                                //mNestedScroll.scrollTo(0, 0);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
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
