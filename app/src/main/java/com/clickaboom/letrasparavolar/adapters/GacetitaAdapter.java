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
import com.clickaboom.letrasparavolar.models.gacetita.Gacetita;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Karencita on 13/05/2017.
 */

public class GacetitaAdapter extends RecyclerView.Adapter<GacetitaAdapter.ViewHolder> {
    private static Context mContext;
    private static List<Gacetita> mItems;
    public static String mColType;

    // Provide a suitable constructor (depends on the kind of dataset)
    public GacetitaAdapter(List<Gacetita> items, Context context) {
        mContext = context;
        mItems = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public Gacetita mItem;
        public TextView mTitle, mSubtitle;
        public ImageButton mImage;
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mTitle = (TextView) v.findViewById(R.id.title_txt);
            mSubtitle = (TextView) v.findViewById(R.id.subtitle_id);
            mImage = (ImageButton)v.findViewById(R.id.book_img);
        }

        @Override
        public void onClick(View v) {
            String url = ApiConfig.gacetitaPdf + mItem.epub;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            mContext.startActivity(i);
        }

        public void setItem(Gacetita item) {
            mItem = item;
        }
    }

    @Override
    public GacetitaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GacetitaAdapter.ViewHolder holder, int position) {
        Gacetita item = mItems.get(position);
        holder.setItem(item);
        // Title
        holder.mTitle.setText(item.titulo);

        // Subtitle
        holder.mSubtitle.setText(item.autor);

        // Image
        String imgUrl = ApiConfig.gacetitaImg + item.image;
        Picasso.with(mContext)
                .load(imgUrl)
                .resize(200,200)
                .centerInside()
                .into(holder.mImage);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
