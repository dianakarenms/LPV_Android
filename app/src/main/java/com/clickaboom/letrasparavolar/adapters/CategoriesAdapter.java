package com.clickaboom.letrasparavolar.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.models.collections.categories.Categoria;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;

import java.util.List;

/**
 * Created by Karencita on 13/05/2017.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private static Context mContext;
    private static List<Categoria> mList;
    private String mImgPath;
    private static RecyclerViewClickListener mItemListener;
    private ImageLoader imageLoader;

    // Provide a suitable constructor (depends on the kind of dataset)
    public CategoriesAdapter(List<Categoria> list, String imgPath, Context context, RecyclerViewClickListener itemListener) {
        mList = list;
        mImgPath = imgPath;
        mContext = context;
        mItemListener = itemListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public NetworkImageView mImage;
        public LinearLayout mCategoryLay;
        public ViewHolder(View v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.title_txt);
            mImage = (NetworkImageView)v.findViewById(R.id.book_img);
            mCategoryLay = (LinearLayout)v.findViewById(R.id.category_item_lay);
        }
    }

    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nav_categories, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CategoriesAdapter.ViewHolder holder, final int position) {
        final Categoria category = mList.get(position);
        holder.mTitle.setText(category.nombre);
        //holder.mImage.setDefaultImageResId(R.drawable.book_placeholder);

        String imgUrl = ApiConfig.baseUrl + mImgPath + category.icono;
        imageLoader = ApiSingleton.getInstance(mContext).getImageLoader();
        imageLoader.get(imgUrl, ImageLoader.getImageListener(holder.mImage, R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert));
        holder.mImage.setImageUrl(imgUrl, imageLoader);
//        holder.mImage.setDefaultImageResId(R.drawable.book_placeholder);
//        String imgUrl = ApiConfig.baseUrl + mImgPath + category.icono;
//        holder.mImage.setImageUrl(imgUrl, ApiSingleton.getInstance(mContext).getImageLoader());

        if(category.active) {
            holder.mCategoryLay.setBackgroundColor(mContext.getResources().getColor(R.color.collections_nav_pressed));
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
        public void recyclerViewListClicked(String categoryId);
    }

    public void setImgPath(String imgPath) {
        mImgPath = imgPath;
    }
}