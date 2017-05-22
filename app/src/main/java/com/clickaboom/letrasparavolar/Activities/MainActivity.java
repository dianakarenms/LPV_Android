package com.clickaboom.letrasparavolar.Activities;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.clickaboom.letrasparavolar.Fragments.CollectionsFragment;
import com.clickaboom.letrasparavolar.Fragments.LegendsFragment;
import com.clickaboom.letrasparavolar.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {

    private ImageButton legendsBtn, collectionsBtn, libraryBtn;
    private ImageView image;
    private FragmentManager fm;
    private ImageView bottomOrnament;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fm = getSupportFragmentManager();

        // Set toolbar_asistant title
        ((TextView)findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.legends_title));
        findViewById(R.id.left_btn).setVisibility(View.GONE);
        findViewById(R.id.right_bnt).setVisibility(View.GONE);

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

        image = (ImageView)findViewById(R.id.mi_barquitoo);
        image.post(new Runnable() {
            @Override
            public void run() {
                animateBarquito(image);
            }
        });

        legendsBtn = (ImageButton)findViewById(R.id.legends_btn);
        legendsBtn.setOnClickListener(this);

        collectionsBtn = (ImageButton)findViewById(R.id.collections_btn);
        collectionsBtn.setOnClickListener(this);

        libraryBtn = (ImageButton)findViewById(R.id.library_btn);
        libraryBtn.setOnClickListener(this);

    }
    
    @Override
    protected void onStart() {
        super.onStart();
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

    private int windowWidth() {
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
    @Override
    public void onBackPressed() {
        restoreBottonNavColors();
        // Set toolbar_asistant title
        ((TextView)findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.news_title));
        findViewById(R.id.left_btn).setVisibility(View.GONE);
        findViewById(R.id.right_bnt).setVisibility(View.GONE);

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
                Fragment legFrag = fm.findFragmentById(R.id.fragment_container);
                //if(legFrag == null) {
                    legFrag = new LegendsFragment();
                //}
                    /*fm.beginTransaction()
                            .replace(R.id.fragment_container, legFrag)
                            .addToBackStack("legends")
                            .commit();*/
                replaceFragment(legFrag);
                break;
            case R.id.collections_btn:
                Fragment colFrag = fm.findFragmentById(R.id.fragment_container);
                //if(colFrag == null) {
                    colFrag = new CollectionsFragment();
                //}
                    /*fm.beginTransaction()
                            .replace(R.id.fragment_container, colFrag)
                            .addToBackStack("collections")
                            .commit();*/
                replaceFragment(colFrag);
                break;
            case R.id.library_btn:
                break;
        }
    }

    private void replaceFragment (Fragment fragment){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }
}
