package com.clickaboom.letrasparavolar.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.models.collections.Categoria;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Karencita on 13/05/2017.
 */

public class LegendsCategoriesAdapter extends RecyclerView.Adapter<LegendsCategoriesAdapter.ViewHolder> {
    private static Context mContext;
    private static List<Categoria> mList;
    private final int mPressedColor;
    private String mImgPath;
    private static RecyclerViewClickListener mItemListener;
    private ImageLoader imageLoader;

    // Provide a suitable constructor (depends on the kind of dataset)
    public LegendsCategoriesAdapter(List<Categoria> list, String imgPath, int pressedColor, Context context, RecyclerViewClickListener itemListener) {
        mList = list;
        mImgPath = imgPath;
        mPressedColor = pressedColor;
        mContext = context;
        mItemListener = itemListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public ImageView mImage;
        public LinearLayout mCategoryLay;
        public ViewHolder(View v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.title_txt);
            mImage = (ImageView) v.findViewById(R.id.book_img);
            mCategoryLay = (LinearLayout)v.findViewById(R.id.category_item_lay);
        }
    }

    @Override
    public LegendsCategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nav_categories, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final LegendsCategoriesAdapter.ViewHolder holder, final int position) {
        final Categoria category = mList.get(position);
        holder.mTitle.setText(category.nombre);
        //holder.mImage.setDefaultImageResId(R.drawable.book_placeholder);
        final String imgUrl = ApiConfig.catImgPath + category.icono;
        /*
        imageLoader = ApiSingleton.getInstance(mContext).getImageLoader();
        imageLoader.get(imgUrl, ImageLoader.getImageListener(holder.mImage, R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert));
        holder.mImage.setImageUrl(imgUrl, imageLoader);*/
        Picasso.with(mContext)
                .load(imgUrl)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .resize(100, 100)
                .centerInside()
                .into(holder.mImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        // Try again online if cache failed
                        Picasso.with(mContext)
                                .load(Uri.parse(imgUrl))
                                .into(holder.mImage);
                    }
                });
//        holder.mImage.setDefaultImageResId(R.drawable.book_placeholder);
//        String imgUrl = ApiConfig.baseUrl + mImgPath + category.icono;
//        holder.mImage.setImageUrl(imgUrl, ApiSingleton.getInstance(mContext).getImageLoader());

        if(category.active) {
            holder.mCategoryLay.setBackgroundColor(mContext.getResources().getColor(mPressedColor));
        } else {
            holder.mCategoryLay.setBackgroundColor(Color.TRANSPARENT);
        }
        holder.mCategoryLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear previous active categories and active this
                clearActive();
                category.active = true;
                notifyDataSetChanged();
                mItemListener.recyclerViewListClicked(category.id);
            }
        });

    }

    public void clearActive() {
        for(Categoria categoria : mList){
            categoria.active = false;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface RecyclerViewClickListener {
        public void recyclerViewListClicked(Integer categoryId);
    }

    public void setImgPath(String imgPath) {
        mImgPath = imgPath;
    }
}
