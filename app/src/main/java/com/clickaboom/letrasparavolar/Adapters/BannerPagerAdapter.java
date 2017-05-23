package com.clickaboom.letrasparavolar.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.clickaboom.letrasparavolar.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by clickaboom on 5/22/17.
 */

public class BannerPagerAdapter extends PagerAdapter
{
    public static int LOOPS_COUNT = 1000;
    private List<String> data = null;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public BannerPagerAdapter(Context context, List<String> data) {
        super();
        this.mContext = context;
        this.data = data;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.item_banner, container, false);

        ImageView imgIcon = (ImageView)itemView.findViewById(R.id.banner_img);
        imgIcon.setImageResource(R.drawable.test_banner);
        /*Picasso.with(mContext)
                .load(data.get(position))
                .resize(50, 50)
                .centerInside()
                .into(imgIcon);*/

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}