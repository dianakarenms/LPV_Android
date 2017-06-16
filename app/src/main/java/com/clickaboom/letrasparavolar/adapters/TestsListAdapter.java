package com.clickaboom.letrasparavolar.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.activities.InGameActivity;
import com.clickaboom.letrasparavolar.models.game.Game;
import com.clickaboom.letrasparavolar.models.game.TestCurioseando;

import java.util.List;

/**
 * Created by Karencita on 13/05/2017.
 */

public class TestsListAdapter extends RecyclerView.Adapter<TestsListAdapter.ViewHolder> {
    private static Context mContext;
    private static List<TestCurioseando> sTestList;
    public static String mColType;
    public static Game sGame;

    // Provide a suitable constructor (depends on the kind of dataset)
    public TestsListAdapter(List<TestCurioseando> bookList, Context context) {
        mContext = context;
        sTestList = bookList;
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
    public TestsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_curioseando_tests, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TestsListAdapter.ViewHolder holder, int position) {
        final TestCurioseando test = sTestList.get(position);
        // Title
        holder.mTitle.setText(test.nombre);

        holder.mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, game.gameType, Toast.LENGTH_SHORT).show();
                sGame.id = test.id;
                mContext.startActivity(InGameActivity.newIntent(mContext, sGame));
            }
        });
    }

    @Override
    public int getItemCount() {
        return sTestList.size();
    }
}
