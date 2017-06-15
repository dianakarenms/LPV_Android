package com.clickaboom.letrasparavolar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.models.game.Respuesta;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Karencita on 13/05/2017.
 */

public class InGameAdapter extends RecyclerView.Adapter<InGameAdapter.ViewHolder> {
    private static Context mContext;
    private static List<Respuesta> sGameList;
    public static String mColType;
    public static OnClickListener sListener;
    public ImageLoader imageLoader;
    public static boolean mAnswerClicked = false;
    public static View sParentView;

    // Provide a suitable constructor (depends on the kind of dataset)
    public InGameAdapter(List<Respuesta> bookList, Context context, OnClickListener listener, View parentView) {
        mContext = context;
        sGameList = bookList;
        sListener = listener;
        sParentView = parentView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTitle;
        public ImageView mImage;
        public ImageView mCheckImg, mCorrectImgView;
        public Respuesta mItem;
        public RelativeLayout mItemView;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mTitle = (TextView) v.findViewById(R.id.res_title);
            mImage = (ImageView) v.findViewById(R.id.res_img);
            mCheckImg = (ImageView) v.findViewById(R.id.check_img);
            mItemView = (RelativeLayout) v.findViewById(R.id.parent);
        }

        @Override
        public void onClick(View v) {
            // If there hasn't been chosen an answer before, show the value
            if(!mAnswerClicked) {
                mAnswerClicked = true;
                int corrPos = 0;
                if (mItem.isCorrecta.equals("SI"))
                    mCheckImg.setImageResource(R.drawable.checked);
                else {
                    // Show that user has done wrong, and show him the
                    // correct answer
                    mCheckImg.setImageResource(R.drawable.unchecked);
                    for(int i = 0; i < sGameList.size(); i++){
                        if(sGameList.get(i).isCorrecta.equals("SI")) {
                            corrPos = i;
                        }
                    }
                }
                sListener.OnItemClicked(mItem, corrPos);
            }
        }

        public void setItem(Respuesta item) {
            mItem = item;
        }
    }

    @Override
    public InGameAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game_nahuatlismos, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InGameAdapter.ViewHolder holder, int position) {
        final Respuesta respuesta = sGameList.get(position);
        holder.setItem(respuesta);

        // Holder size settings to fit screen
        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        // Set the height by params
        params.height = sParentView.getHeight() / 2;
        //params.p(10, 10, 10, 10);

        // set height of RecyclerView
        holder.itemView.setLayoutParams(params);
        holder.itemView.setPadding(15, 15, 15, 15);
        //holder.itemView.setBackgroundResource(R.drawable.square_border);

        // Title
        holder.mTitle.setText(respuesta.respuesta);

        // Image
        holder.mImage.setImageResource(R.drawable.book_placeholder); // Initial empty value
        String imgUrl = ApiConfig.interImg + "thumb_" + respuesta.imagen;
//        imageLoader = ApiSingleton.getInstance(mContext).getImageLoader();
//        imageLoader.get(imgUrl, ImageLoader.getImageListener(holder.mImage, R.drawable.book_placeholder, android.R.drawable.ic_dialog_alert));
//        holder.mImage.setImageUrl(imgUrl, imageLoader);
        Picasso.with(mContext)
                .load(imgUrl)
                .resize(400,400)
                .centerInside()
                .placeholder(R.drawable.book_placeholder)
                .into(holder.mImage);

        // Reset mcheckedimg value
        holder.mCheckImg.setImageResource(0); // Initial empty value
    }

    @Override
    public int getItemCount() {
        return sGameList.size();
    }


    public interface OnClickListener {
        void OnItemClicked(Respuesta res, int correctPos);
    }
}