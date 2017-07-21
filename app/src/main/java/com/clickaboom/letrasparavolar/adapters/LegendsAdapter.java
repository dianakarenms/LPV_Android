package com.clickaboom.letrasparavolar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.activities.BookDetailsActivity;
import com.clickaboom.letrasparavolar.activities.MainActivity;
import com.clickaboom.letrasparavolar.models.collections.Colecciones;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karencita on 13/05/2017.
 */

public class LegendsAdapter extends RecyclerView.Adapter<LegendsAdapter.ViewHolder> {
    private static Context mContext;
    private static List<Colecciones> mBookList;
    public static String mColType;

    // Provide a suitable constructor (depends on the kind of dataset)
    public LegendsAdapter(List<Colecciones> bookList, Context context) {
        mContext = context;
        mBookList = bookList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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
            mContext.startActivity(BookDetailsActivity.newIntent(mContext, mBookList.get(getAdapterPosition()).id, mColType));
//            Fragment fragment = BookDetailsFragment.newInstance(mBookList.get(getAdapterPosition()));
//            MainActivity.addFragment(fragment, (MainActivity) mContext);
        }
    }

    @Override
    public LegendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LegendsAdapter.ViewHolder holder, int position) {
        // Title
        holder.mTitle.setText(mBookList.get(position).titulo);

        // Subtitle
        List<String> autoresList = new ArrayList<>();
        for(int i = 0; i<mBookList.get(position).autores.size(); i++) {
            autoresList.add(mBookList.get(position).autores.get(i).autor);
        }
        holder.mSubtitle.setText(MainActivity.getStringFromListByCommas(autoresList));

        // Image
        String imgUrl = ApiConfig.collectionsImg + mBookList.get(position).imagen;
        Picasso.with(mContext)
                .load(imgUrl)
                .resize(400,400)
                .centerInside()
                .into(holder.mImage);
    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }
}
