package com.clickaboom.letrasparavolar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.models.internationalization.Internationalization;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Karencita on 13/05/2017.
 */

public class InternationalizationAdapter extends RecyclerView.Adapter<InternationalizationAdapter.ViewHolder> {
    private static Context mContext;
    private static List<Internationalization> mBookList;

    // Provide a suitable constructor (depends on the kind of dataset)
    public InternationalizationAdapter(List<Internationalization> bookList, Context context) {
        mContext = context;
        mBookList = bookList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle, mDescription;
        public ImageView mImage;
        public ViewHolder(View v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.title_txt);
            mDescription = (TextView) v.findViewById(R.id.description_txt);
            mImage = (ImageView) v.findViewById(R.id.book_img);
        }
    }

    @Override
    public InternationalizationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_internationalization, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InternationalizationAdapter.ViewHolder holder, int position) {
        // Title
        holder.mTitle.setText(mBookList.get(position).titulo);

        // Subtitle
//        holder.mDescription.setText(mContext.getResources().getString(R.string.lorem_ipsum));
        holder.mDescription.setText(android.text.Html.fromHtml(mBookList.get(position).contenido).toString());

        // Image
        String imgUrl = ApiConfig.interImg + mBookList.get(position).imagen;
        Picasso.with(mContext)
                .load(imgUrl)
                .resize(800,800)
                .centerInside()
                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(holder.mImage);
        /*
        imageLoader = ApiSingleton.getInstance(mContext).getImageLoader();
        imageLoader.get(imgUrl, ImageLoader.getImageListener(holder.mImage, R.drawable.book_placeholder, android.R.drawable.ic_dialog_alert));
        holder.mImage.setImageUrl(imgUrl, imageLoader);*/
    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }
}
