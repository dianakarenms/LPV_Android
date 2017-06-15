package com.clickaboom.letrasparavolar.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InGameActivity extends AppCompatActivity
        implements View.OnClickListener, InGameAdapter.OnClickListener {

    private static final String TAG = "com.lpv.GamesActivity";
    private static final String JUEGO_A = "nahuatlismos";
    private static final String JUEGO_B = "curioseando";
    private static final String EXTRA_GAME = "mGame";
    //private static final String EXTRA_SEARCH_TYPE = "com.lpv.SearchType";
    private Context mContext;
    private RecyclerView mRecyclerView;
    private InGameAdapter mAdapter;
    private ArrayList<Respuesta> mRespList = new ArrayList<>();
    private int mQuestionIndex = 0;
    private List<Pregunta> mPregList;
    private InGameAdapter.OnClickListener mListener;
    private int mCorrectCounter = 0;
    private Button mNextBtn;
    private Game mGame;
    private Button mRepeatBtn, mFinishBtn, mShareBtn;

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
        mListener = this;

        mGame = (Game) getIntent().getSerializableExtra(EXTRA_GAME);

        // Menu drawer onclicklistener
        findViewById(R.id.drawer_button).setVisibility(View.INVISIBLE);

        // Set toolbar_asistant title
        ((TextView)findViewById(R.id.toolbar_title)).setText(mGame.gameType);
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
        mAdapter = new InGameAdapter(mRespList, mContext, mListener, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

       /* // Modal buttons controller
        mRepeatBtn = (Button) findViewById(R.id.repeat_btn);
        mFinishBtn = (Button) findViewById(R.id.repeat_btn);
        mShareBtn = (Button) findViewById(R.id.share_btn);
        mRepeatBtn.setOnClickListener(this);
        mFinishBtn.setOnClickListener(this);
        mShareBtn.setOnClickListener(this);*/

        // Setup nextbtn
        mNextBtn = (Button) findViewById(R.id.next_btn);
        invalidNextBtn();

        if(mGame.gameType.equals(GamesActivity.JUEGO_A))
            loadNahuatlismosQuestions();

    }

    private void invalidNextBtn() {
        mNextBtn.setClickable(false);
        mNextBtn.setBackgroundColor(getResources().getColor(R.color.gray));
    }

    private void validNahuatlNextBtn(int btnColor) {
        mNextBtn.setClickable(true);
        mNextBtn.setBackgroundColor(getResources().getColor(btnColor));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_btn:
                if(mQuestionIndex < mPregList.size() - 1) {
                    mQuestionIndex++;
                    setQuestion(mPregList.get(mQuestionIndex));
                } else {
                    RelativeLayout modalView = (RelativeLayout) findViewById(R.id.modal_game_over);
                    modalView.setVisibility(View.VISIBLE);
                    TextView tv = (TextView) modalView.findViewById(R.id.puntua_txt);
                    if(tv != null)
                        tv.setText(String.valueOf(mCorrectCounter));
                }
                break;
            case R.id.repeat_btn:
                mCorrectCounter = 0;
                mQuestionIndex = 0;
                findViewById(R.id.modal_game_over).setVisibility(View.GONE);
                setQuestion(mPregList.get(mQuestionIndex));
                break;
            case R.id.finish_btn:
                finish();
                break;
            case R.id.share_btn:
                shareImage();
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
        mAdapter.mAnswerClicked = false;
        invalidNextBtn();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnItemClicked(Respuesta res, int correctPos) {
        validNahuatlNextBtn(mGame.btnColor);
        if(res.isCorrecta.equals("SI"))
            mCorrectCounter ++;
        else {
            InGameAdapter.ViewHolder holder = (InGameAdapter.ViewHolder)mRecyclerView.findViewHolderForAdapterPosition(correctPos);
            holder.mCheckImg.setImageResource(R.drawable.checked);
        }

        //Toast.makeText(mContext, "correct: " + mCorrectCounter, Toast.LENGTH_SHORT).show();
    }

    private void shareImage(){
        View view = findViewById(R.id.nahuatlismos_result);

        view.setDrawingCacheEnabled(true);
        Bitmap icon = Bitmap.createBitmap(view.getDrawingCache());
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);


        OutputStream outstream;
        try {
            outstream = getContentResolver().openOutputStream(uri);
            icon.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
            outstream.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Image"));
    }
}
