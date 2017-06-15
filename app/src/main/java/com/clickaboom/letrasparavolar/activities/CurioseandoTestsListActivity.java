package com.clickaboom.letrasparavolar.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.adapters.TestsListAdapter;
import com.clickaboom.letrasparavolar.models.game.Game;
import com.clickaboom.letrasparavolar.models.game.ResCurioseando;
import com.clickaboom.letrasparavolar.models.game.TestCurioseando;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.clickaboom.letrasparavolar.network.GsonRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CurioseandoTestsListActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = "com.lpv.TestsActivity";
    private static final String EXTRA_GAME = "mGame";
    private Context mContext;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mManager;
    private TestsListAdapter mAdapter;
    private List<TestCurioseando> mTestArrayList = new ArrayList<>();
    private Game mGame;

    public static Intent newIntent(Context packageContext, Game game) {
        Intent i = new Intent(packageContext, CurioseandoTestsListActivity.class);
        i.putExtra(EXTRA_GAME, (Serializable) game);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);
        mContext = this;

        mGame = (Game) getIntent().getSerializableExtra(EXTRA_GAME);

        // Menu drawer onclicklistener
        findViewById(R.id.drawer_button).setVisibility(View.INVISIBLE);

        // Set toolbar_asistant title
        ((TextView)findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.tests));
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

        // specify an adapter (see also next example)
        mAdapter = new TestsListAdapter(mTestArrayList, mContext);
        mAdapter.sGame = mGame;
        mRecyclerView.setAdapter(mAdapter);

        loadCurioseandoTests();
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

    private void loadCurioseandoTests() {
        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(mContext)
                .addToRequestQueue(new GsonRequest(ApiConfig.curioseandoTests,
                        ResCurioseando.class,
                        Request.Method.GET,
                        null, null,
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                Log.d(TAG, response.toString());
                                mTestArrayList.clear();
                                mTestArrayList.addAll(((ResCurioseando) response).mTests);
                                mAdapter.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }));
    }
}
