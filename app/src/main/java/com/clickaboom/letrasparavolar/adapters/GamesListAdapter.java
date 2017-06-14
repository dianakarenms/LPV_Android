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

import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.activities.BookDetailsActivity;
import com.clickaboom.letrasparavolar.activities.InGameActivity;
import com.clickaboom.letrasparavolar.activities.MainActivity;
import com.clickaboom.letrasparavolar.models.collections.Colecciones;
import com.clickaboom.letrasparavolar.models.game.Game;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karencita on 13/05/2017.
 */

public class GamesListAdapter extends RecyclerView.Adapter<GamesListAdapter.ViewHolder> {
    private static Context mContext;
    private static List<Game> sGameList;
    public static String mColType;

    // Provide a suitable constructor (depends on the kind of dataset)
    public GamesListAdapter(List<Game> bookList, Context context) {
        mContext = context;
        sGameList = bookList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitle, mSubtitle;
        public ImageView mImage, mPlayBtn;
        public ViewHolder(View v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.game_title);
            mSubtitle = (TextView) v.findViewById(R.id.game_subtitle);
            mImage = (ImageView)v.findViewById(R.id.game_img);
            mPlayBtn = (ImageView)v.findViewById(R.id.game_play_btn);
        }
    }

    @Override
    public GamesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GamesListAdapter.ViewHolder holder, int position) {
        final Game game = sGameList.get(position);
        // Title
        holder.mTitle.setText(game.title);

        // Subtitle
        holder.mSubtitle.setText(game.subtitle);

        // Image
        Picasso.with(mContext)
                .load(game.imgResource)
                .resize(200,200)
                .centerInside()
                .into(holder.mImage);

        holder.mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, game.gameType, Toast.LENGTH_SHORT).show();
                mContext.startActivity(InGameActivity.newIntent(mContext, game));
            }
        });
    }

    @Override
    public int getItemCount() {
        return sGameList.size();
    }
}
