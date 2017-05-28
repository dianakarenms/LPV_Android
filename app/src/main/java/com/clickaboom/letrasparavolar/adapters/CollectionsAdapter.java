package com.clickaboom.letrasparavolar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.models.Collections;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Karencita on 13/05/2017.
 */

public class CollectionsAdapter extends RecyclerView.Adapter<CollectionsAdapter.ViewHolder> {
    private Context mContext;
    private List<Collections> mBookList;

    // Provide a suitable constructor (depends on the kind of dataset)
    public CollectionsAdapter(List<Collections> bookList, Context context) {
        mContext = context;
        mBookList = bookList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle, mSubtitle;
        public ImageView mImage;
        public ViewHolder(View v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.title_txt);
            mSubtitle = (TextView) v.findViewById(R.id.subtitle_id);
            mImage = (ImageView) v.findViewById(R.id.book_img);
        }
    }

    @Override
    public CollectionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CollectionsAdapter.ViewHolder holder, int position) {
        holder.mTitle.setText(mBookList.get(position).titulo);
        holder.mSubtitle.setText(mBookList.get(position).descripcion);
        Picasso.with(mContext)
                .load(mBookList.get(position).imagen)
                .resize(400, 400)
                .centerInside()
                .into(holder.mImage);
    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }
}
