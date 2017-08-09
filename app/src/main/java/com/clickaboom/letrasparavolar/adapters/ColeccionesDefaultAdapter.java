package com.clickaboom.letrasparavolar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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

public class ColeccionesDefaultAdapter extends RecyclerView.Adapter<ColeccionesDefaultAdapter.ViewHolder> {
    private static Context mContext;
    private static List<Colecciones> mBookList;
    public static String mColType;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ColeccionesDefaultAdapter(List<Colecciones> bookList, Context context) {
        mContext = context;
        mBookList = bookList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public RelativeLayout mContainer;
        public TextView mTitle, mSubtitle;
        public ImageButton mImage;
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mContainer = (RelativeLayout) v.findViewById(R.id.container_rl);
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
    public ColeccionesDefaultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book_home, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ColeccionesDefaultAdapter.ViewHolder holder, int position) {
        // Container
        float widthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
        int width = (int) (widthPixels-50)/3; // 50 is the combined width of both scroll arrows
        holder.mContainer.setLayoutParams(new RelativeLayout.LayoutParams(width, RelativeLayout.LayoutParams.MATCH_PARENT));

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
                .resize(200,200)
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
