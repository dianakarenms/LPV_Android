package com.clickaboom.letrasparavolar.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.activities.WebviewActivity;
import com.clickaboom.letrasparavolar.models.banners.Banner;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by clickaboom on 5/22/17.
 */

public class BannerPagerAdapter extends PagerAdapter
{
    private static final String TARGET_URL = "URL";
    private static final String TARGET_COLECCION = "COLECCION";
    public static int LOOPS_COUNT = 1000;
    private List<Banner> data = null;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public BannerPagerAdapter(Context context, List<Banner> data) {
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

        final Banner banner = data.get(position);
        ImageView imgIcon = (ImageView)itemView.findViewById(R.id.banner_img);
//        imgIcon.setImageResource(R.drawable.test_banner);
        Picasso.with(mContext)
                .load(ApiConfig.baseUrl + "/" + banner.imagen)
                .fit()
//                .resize(1000, 200)
//                .centerInside()
                .into(imgIcon);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(banner.target.equals(TARGET_URL)) {
                    if(!banner.url.isEmpty()) {
                        mContext.startActivity(WebviewActivity.newIntent(
                                mContext,
                                banner.url));
                    }
                } else if(banner.target.equals(TARGET_COLECCION)) {

                }
            }
        });
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}