package com.clickaboom.letrasparavolar.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.activities.MainActivity;
import com.clickaboom.letrasparavolar.fragments.BookDetailsFragment;
import com.clickaboom.letrasparavolar.models.Book;

import java.util.List;

/**
 * Created by Karencita on 13/05/2017.
 */

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.ViewHolder> {
    private static Context mContext;
    private List<Book> mBookList;

    // Provide a suitable constructor (depends on the kind of dataset)
    public BooksAdapter(List<Book> bookList, Context context) {
        mContext = context;
        mBookList = bookList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTitle, mSubtitle;
        public ImageView mImage;
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mTitle = (TextView) v.findViewById(R.id.title_txt);
            mSubtitle = (TextView) v.findViewById(R.id.subtitle_id);
            mImage = (ImageView) v.findViewById(R.id.book_img);
        }

        @Override
        public void onClick(View v) {
            Fragment fragment = new BookDetailsFragment();
            MainActivity.replaceFragment(fragment, ((MainActivity) mContext));
        }
    }

    @Override
    public BooksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BooksAdapter.ViewHolder holder, int position) {
        holder.mTitle.setText(mBookList.get(position).getTitle());
        holder.mSubtitle.setText(mBookList.get(position).getSubtitle());
        //holder.mImage.setImageResource(mBookList.get(position).getImage());
//        Picasso.with(mContext)
//                .load(mBookList.get(position).getImage())
//                .resize(400, 400)
//                .centerCrop()
//                .into(holder.mImage);
    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }
}
