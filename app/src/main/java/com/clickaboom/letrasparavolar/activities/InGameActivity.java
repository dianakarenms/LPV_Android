package com.clickaboom.letrasparavolar.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.adapters.InGameAdapter;
import com.clickaboom.letrasparavolar.models.game.Game;
import com.clickaboom.letrasparavolar.models.game.Pregunta;
import com.clickaboom.letrasparavolar.models.game.ResNahuatlismos;
import com.clickaboom.letrasparavolar.models.game.Respuesta;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.clickaboom.letrasparavolar.network.GsonRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InGameActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = "com.lpv.GamesActivity";
    private static final String JUEGO_A = "nahuatlismos";
    private static final String JUEGO_B = "curioseando";
    private static final String EXTRA_GAME = "game";
    //private static final String EXTRA_SEARCH_TYPE = "com.lpv.SearchType";
    private Context mContext;
    private RecyclerView mRecyclerView;
    private InGameAdapter mAdapter;
    private ArrayList<Respuesta> mRespList = new ArrayList<>();
    private int mQuestionIndex = 0;
    private List<Pregunta> mPregList;

    public static Intent newIntent(Context packageContext, Game game) {
        Intent i = new Intent(packageContext, InGameActivity.class);
        i.putExtra(EXTRA_GAME, (Serializable) game);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);
        mContext = this;

        Game game = (Game) getIntent().getSerializableExtra(EXTRA_GAME);

        // Menu drawer onclicklistener
        findViewById(R.id.drawer_button).setVisibility(View.INVISIBLE);

        // Set toolbar_asistant title
        ((TextView)findViewById(R.id.toolbar_title)).setText(game.gameType);
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
        mRecyclerView = (RecyclerView) findViewById(R.id.answers_recycler);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));

        // specify an adapter (see also next example)
        mAdapter = new InGameAdapter(mRespList, mContext);
        mRecyclerView.setAdapter(mAdapter);

        if(game.gameType.equals(GamesActivity.JUEGO_A))
            loadNahuatlismosQuestions();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_btn:
                if(mQuestionIndex < mRespList.size() - 1) {
                    mQuestionIndex++;
                    setQuestion(mPregList.get(mQuestionIndex));
                } else {
                    Toast.makeText(mContext, "End of test", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void loadNahuatlismosQuestions() {
        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(mContext)
                .addToRequestQueue(new GsonRequest(ApiConfig.nahuatlismosGame,
                        ResNahuatlismos.class,
                        Request.Method.GET,
                        null, null,
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                Log.d(TAG, response.toString());
                                mPregList = ((ResNahuatlismos) response).data;
                                setQuestion(mPregList.get(mQuestionIndex));
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }));

    }

    private void setQuestion(Pregunta pregunta){
        ((TextView)findViewById(R.id.question_title)).setText(pregunta.pregunta);
        mRespList.clear();
        mRespList.addAll(pregunta.respuestas);
        mAdapter.notifyDataSetChanged();
    }
}
