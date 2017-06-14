package com.clickaboom.letrasparavolar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.models.game.Game;
import com.clickaboom.letrasparavolar.models.game.Respuesta;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.clickaboom.letrasparavolar.network.SQLiteDBHelper.imagen;

/**
 * Created by Karencita on 13/05/2017.
 */

public class InGameAdapter extends RecyclerView.Adapter<InGameAdapter.ViewHolder> {
    private static Context mContext;
    private static List<Respuesta> sGameList;
    public static String mColType;
    private ImageLoader imageLoader;

    // Provide a suitable constructor (depends on the kind of dataset)
    public InGameAdapter(List<Respuesta> bookList, Context context) {
        mContext = context;
        sGameList = bookList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTitle;
        public NetworkImageView mImage;
        public ImageView mCheckImg;
        public Respuesta mItem;
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mTitle = (TextView) v.findViewById(R.id.res_title);
            mImage = (NetworkImageView) v.findViewById(R.id.res_img);
            mCheckImg = (ImageView) v.findViewById(R.id.check_img);
        }

        @Override
        public void onClick(View v) {
            // Check img
            if(mItem.isCorrecta.equals("SI"))
                mCheckImg.setImageResource(R.drawable.checked);
            else
                mCheckImg.setImageResource(R.drawable.unchecked);
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

        // Title
        holder.mTitle.setText(respuesta.respuesta);

        // Image
        String imgUrl = ApiConfig.interImg + respuesta.imagen;
        imageLoader = ApiSingleton.getInstance(mContext).getImageLoader();
        imageLoader.get(imgUrl, ImageLoader.getImageListener(holder.mImage, R.drawable.book_placeholder, android.R.drawable.ic_dialog_alert));
        holder.mImage.setImageUrl(imgUrl, imageLoader);

        // Reset mcheckedimg value
        holder.mCheckImg.setImageResource(0);
    }

    @Override
    public int getItemCount() {
        return sGameList.size();
    }
}
