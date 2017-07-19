package com.clickaboom.letrasparavolar.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.models.noticias.Noticia;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Karencita on 13/05/2017.
 */

public class NoticiaAdapter extends RecyclerView.Adapter<NoticiaAdapter.ViewHolder> {
    private static Context mContext;
    private static List<Noticia> mItems;
    public static String mColType;

    // Provide a suitable constructor (depends on the kind of dataset)
    public NoticiaAdapter(List<Noticia> items, Context context) {
        mContext = context;
        mItems = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public Noticia mItem;
        public TextView mTitle, mDate, mDescription;
        public ImageButton mImage;
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mTitle = (TextView) v.findViewById(R.id.titleTxt);
            mDate = (TextView) v.findViewById(R.id.dateTxt);
            mDescription = (TextView) v.findViewById(R.id.descriptionTxt);
            //mImage = (ImageButton)v.findViewById(R.id.book_img);
        }

        @Override
        public void onClick(View v) {/*
            String url = ApiConfig.gacetitaPdf + mItem.epub;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            mContext.startActivity(i);*/
        }

        public void setItem(Noticia item) {
            mItem = item;
        }
    }

    @Override
    public NoticiaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NoticiaAdapter.ViewHolder holder, int position) {
        Noticia item = mItems.get(position);
        holder.setItem(item);
        // Title
        holder.mTitle.setText(item.title);

        // Date
        holder.mDate.setText(item.date);

        // Subtitle
        holder.mDescription.setText(item.description);

        // Image
        /*String imgUrl = ApiConfig.gacetitaImg + item.image;
        Picasso.with(mContext)
                .load(imgUrl)
                .resize(200,200)
                .centerInside()
                .into(holder.mImage);*/
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
