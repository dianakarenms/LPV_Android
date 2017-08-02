package com.clickaboom.letrasparavolar.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.adapters.GamesListAdapter;
import com.clickaboom.letrasparavolar.models.game.Game;

import java.util.ArrayList;

import static com.clickaboom.letrasparavolar.network.DownloadFile.isStoragePermissionGranted;

public class JuegosMenuActivity extends AppCompatActivity
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
        Intent i = new Intent(packageContext, JuegosMenuActivity.class);
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
        findViewById(R.id.leyendas_prev_btn).setVisibility(View.GONE);
        findViewById(R.id.leyendas_next_btn).setVisibility(View.GONE);

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
                -1,
                "Nahuatlismos",
                "Relaciona las palabras de origen prehispánico con su significado.",
                R.drawable.juego_1,
                JUEGO_A,
                R.color.nahuatlismos_btn));

        // Curioseando
         mGamesList.add(new Game(
                 -1,
                 "Curioseando",
                 "Contesta los diferentes tests que tenemos para ti.",
                 R.drawable.juego_2,
                 JUEGO_B,
                 R.color.curioseando_btn));

        // specify an adapter (see also next example)
        mAdapter = new GamesListAdapter(mGamesList, mContext);
        mRecyclerView.setAdapter(mAdapter);

        // Check if storage permission is granted
        // If not, the user won't be able to share his results until next game
        isStoragePermissionGranted(JuegosMenuActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
//                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);

            Toast.makeText(mContext, "Habilitando permisos...", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Restart the app since in order to change the ID the process
                    // has to be restarted. Next time you open the app,
                    // the new groupID is set and the permission is granted.
                    PackageManager packageManager = getPackageManager();
                    Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
                    ComponentName componentName = intent.getComponent();
                    Intent mainIntent = IntentCompat.makeRestartActivityTask(componentName);
                    mainIntent.putExtra(MainActivity.EXTRA_OPEN_GAMES, true);
                    startActivity(mainIntent);
                    System.exit(0);
                }
            }, 2000);

        } else {
            Toast.makeText(mContext,
                    "Habilite permiso de \"almacenamiento local\" para compartir los resultados del test.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
}
