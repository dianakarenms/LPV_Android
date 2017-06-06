package com.clickaboom.letrasparavolar.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.activities.MainActivity;
import com.clickaboom.letrasparavolar.network.ApiConfig;

/**
 * Created by Karencita on 15/05/2017.
 */

public class ProgramInfoFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "com.lpv.collections";
    private View v;
    private String url = "", params = "";

    public ProgramInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_information, container, false);
        // Set Suppor Toolbar
        Toolbar toolbar = (Toolbar)v.findViewById(R.id.toolbar);
        ((MainActivity) getContext()).setSupportActionBar(toolbar);

        // Set toolbar_asistant title
        ((TextView)v.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.library_title));

        // Order collections
        v.findViewById(R.id.favorites_txt).setOnClickListener(this);
        v.findViewById(R.id.downloaded_txt).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        restoreOrderColors();
        url = ApiConfig.searchCollections;
        switch (v.getId()) {
            case R.id.favorites_txt:
                params = "?categoria=" + "13";
            case R.id.downloaded_txt:
                params = "?categoria=" + "15";
                break;

        }
        v.setBackgroundColor(getResources().getColor(R.color.order_back_pressed));
    }

    private void restoreOrderColors() {
        v.findViewById(R.id.favorites_txt).setBackground(getResources().getDrawable(R.drawable.nav_subcategories_button));
        v.findViewById(R.id.downloaded_txt).setBackground(getResources().getDrawable(R.drawable.nav_subcategories_button));
    }
}
