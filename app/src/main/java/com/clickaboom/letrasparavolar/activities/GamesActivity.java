package com.clickaboom.letrasparavolar.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.adapters.GamesListAdapter;
import com.clickaboom.letrasparavolar.models.game.Game;

import java.util.ArrayList;

public class GamesActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = "com.lpv.GamesActivity";
    public static final String JUEGO_A = "Nahuatlismos";
    public static final String JUEGO_B = "Curioseando";
    //private static final String EXTRA_SEARCH_TYPE = "com.lpv.SearchType";
    private Context mContext;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mManager;
    private GamesListAdapter mAdapter;
    private ArrayList<Game> mGamesList = new ArrayList<>();

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, GamesActivity.class);
        //i.putExtra(EXTRA_SEARCH_TYPE, searchType);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);
        mContext = this;

        // Menu drawer onclicklistener
        findViewById(R.id.drawer_button).setVisibility(View.INVISIBLE);

        // Set toolbar_asistant title
        ((TextView)findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.games));
        findViewById(R.id.left_btn).setVisibility(View.GONE);
        findViewById(R.id.right_btn).setVisibility(View.GONE);

        // BackBtn
        LinearLayout backBtn = (LinearLayout) findViewById(R.id.back_btn);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // use a linear layout manager
        mManager = new LinearLayoutManager(mContext);
        mRecyclerView = (RecyclerView) findViewById(R.id.games_recycler);
        mRecyclerView.setLayoutManager(mManager);

        // Nahuatlismos
        mGamesList.add(new Game(
                "Nahuatlismos",
                "Relaciona las palabras de origen prehispánico con su significado.",
                R.drawable.juego_1,
                JUEGO_A,
                R.color.nahuatlismos_btn));

        // Curioseando
         mGamesList.add(new Game(
                 "Curioseando",
                 "Contesta los diferentes tests que tenemos para ti.",
                 R.drawable.juego_2,
                 JUEGO_B,
                 R.color.curioseando_btn));

        // specify an adapter (see also next example)
        mAdapter = new GamesListAdapter(mGamesList, mContext);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
        }
    }
}
