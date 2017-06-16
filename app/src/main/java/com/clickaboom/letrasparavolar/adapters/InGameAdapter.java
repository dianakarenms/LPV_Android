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
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.models.game.Respuesta;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Karencita on 13/05/2017.
 */

public class InGameAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_NAHUATLISMOS = 0;
    private static final int ITEM_CURIOSEANDO = 1;
    private static Context mContext;
    private static List<Respuesta> sGameList;
    public static String mColType;
    public static OnClickListener sListener;
    public ImageLoader imageLoader;
    public static boolean mAnswerClicked = false;
    public static View sParentView;
    public static String mGameType;

    // Provide a suitable constructor (depends on the kind of dataset)
    public InGameAdapter(List<Respuesta> bookList, Context context, OnClickListener listener, View parentView) {
        mContext = context;
        sGameList = bookList;
        sListener = listener;
        sParentView = parentView;
    }

    public void setGameType(String gameType) {
        mGameType = gameType;
    }


    public static class NahuatlismosHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTitle;
        public ImageView mImage;
        public ImageView mCheckImg, mCorrectImgView;
        public Respuesta mItem;
        public RelativeLayout mItemView;

        public NahuatlismosHolder(View v) {
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

    public static class CurioseandoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTitle;
        public Respuesta mItem;
        public RelativeLayout mItemView;

        public CurioseandoHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mTitle = (TextView) v.findViewById(R.id.res_title);
            mItemView = (RelativeLayout) v.findViewById(R.id.parent);
        }

        @Override
        public void onClick(View v) {
            sListener.OnItemClicked(mItem, getAdapterPosition());

        }

        public void setItem(Respuesta item) {
            mItem = item;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM_NAHUATLISMOS:
                v = inflater
                        .inflate(R.layout.item_game_nahuatlismos, parent, false);
                viewHolder = new NahuatlismosHolder(v);
            break;
            case ITEM_CURIOSEANDO:
                v = inflater
                        .inflate(R.layout.item_game_curioseando, parent, false);
                viewHolder = new CurioseandoHolder(v);
            break;
            default:
                v = inflater
                        .inflate(R.layout.item_game_curioseando, parent, false);
                viewHolder = new CurioseandoHolder(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Respuesta respuesta = sGameList.get(position);

        switch (getItemViewType(position)) {
            case ITEM_NAHUATLISMOS:
                NahuatlismosHolder nahuaHolder = (NahuatlismosHolder) holder;
                nahuaHolder.setItem(respuesta);

                // Holder size settings to fit screen
                RelativeLayout.LayoutParams params = new
                        RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);

                // Set the height by params
                params.height = sParentView.getHeight() / 2;
                //params.p(10, 10, 10, 10);

                // set height of RecyclerView
                nahuaHolder.itemView.setLayoutParams(params);
                nahuaHolder .itemView.setPadding(15, 15, 15, 15);
                //holder.itemView.setBackgroundResource(R.drawable.square_border);

                // Title
                nahuaHolder.mTitle.setText(respuesta.respuesta);

                // Image
                nahuaHolder.mImage.setImageResource(R.drawable.book_placeholder); // Initial empty value
                String imgUrl = ApiConfig.interImg + "thumb_" + respuesta.imagen;
                //        imageLoader = ApiSingleton.getInstance(mContext).getImageLoader();
                //        imageLoader.get(imgUrl, ImageLoader.getImageListener(holder.mImage, R.drawable.book_placeholder, android.R.drawable.ic_dialog_alert));
                //        holder.mImage.setImageUrl(imgUrl, imageLoader);
                Picasso.with(mContext)
                        .load(imgUrl)
                        .resize(400, 400)
                        .centerInside()
                        .placeholder(R.drawable.book_placeholder)
                        .into(nahuaHolder.mImage);

                // Reset mcheckedimg value
                nahuaHolder.mCheckImg.setImageResource(0); // Initial empty value
                break;

            case ITEM_CURIOSEANDO:
                CurioseandoHolder curioHolder = (CurioseandoHolder) holder;
                curioHolder.setItem(respuesta);

                // Holder size settings to fit screen
                RelativeLayout.LayoutParams params2 = new
                        RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);

                // Set the height by params
                params2.height = sParentView.getHeight() / 3;
                //params.p(10, 10, 10, 10);

                // set height of RecyclerView
                curioHolder.itemView.setLayoutParams(params2);
                curioHolder.itemView.setPadding(15, 15, 15, 15);
                //holder.itemView.setBackgroundResource(R.drawable.square_border);

                // Title
                curioHolder.mTitle.setText(respuesta.respuesta);
                break;
        }
    }
    @Override
    public int getItemCount() {
        return sGameList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mGameType
                .equals(mContext.getResources().getString(R.string.nahuatlismos))) {
            return ITEM_NAHUATLISMOS;
        } else {
            return ITEM_CURIOSEANDO;
        }
    }

    public interface OnClickListener {
        void OnItemClicked(Respuesta res, int correctPos);
    }
}