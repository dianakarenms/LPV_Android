package com.clickaboom.letrasparavolar.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.adapters.GamesListAdapter;
import com.clickaboom.letrasparavolar.models.defaults.ResDefaults;
import com.clickaboom.letrasparavolar.models.game.Game;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.clickaboom.letrasparavolar.network.GsonRequest;

import java.util.ArrayList;

public class ParticipaActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = "com.lpv.GamesActivity";
    public static final String JUEGO_A = "Nahuatlismos";
    public static final String JUEGO_B = "Curioseando";
    private Context mContext;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mManager;
    private GamesListAdapter mAdapter;
    private ArrayList<Game> mGamesList = new ArrayList<>();
    private EditText mNombre, mCorreo, mProcedencia, mMensaje;

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, ParticipaActivity.class);
        //i.putExtra(EXTRA_SEARCH_TYPE, searchType);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participa);
        mContext = this;

        // Menu drawer onclicklistener
        findViewById(R.id.drawer_button).setVisibility(View.INVISIBLE);

        // Set toolbar_asistant title
        ((TextView)findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.participa));
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

        mNombre = (EditText) findViewById(R.id.nombre_field);
        mCorreo = (EditText) findViewById(R.id.correo_field);
        mProcedencia = (EditText) findViewById(R.id.procedencia_field);
        mMensaje = (EditText) findViewById(R.id.mensaje_field);


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
            case R.id.enviar_btn:
                String name = mNombre.getText().toString();
                String mail = mCorreo.getText().toString();
                String from = mProcedencia.getText().toString();
                String message = mMensaje.getText().toString();

                boolean allSet = true;
                if(name.isEmpty()) {
                    mNombre.setError(getResources().getString(R.string.required_field));
                    allSet = false;
                }
                if(mail.isEmpty()) {
                    mCorreo.setError(getResources().getString(R.string.required_field));
                    allSet = false;
                }
                if(from.isEmpty()) {
                    mProcedencia.setError(getResources().getString(R.string.required_field));
                    allSet = false;
                }
                if(message.isEmpty()) {
                    mMensaje.setError(getResources().getString(R.string.required_field));
                    allSet = false;
                }

                if(allSet) {
                    sendParticipaRequest(name,
                            mail,
                            from,
                            message);
                } else
                    break;
                break;
        }
    }

    private void sendParticipaRequest(String nombre, String correo, String procedencia, String mensaje) {
        ApiSingleton.showProgressFlower(ParticipaActivity.this);

        String params = "?nombre=" + nombre + "&email=" + correo + "&escuela=" + procedencia + "&=" + mensaje.replace("\n", " ");
        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(mContext)
                .addToRequestQueue(new GsonRequest(ApiConfig.participa + params,
                        ResDefaults.class,
                        Request.Method.GET,
                        null, null,
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                ApiSingleton.hideProgressFlower();
                                Log.d(TAG, response.toString());
                                boolean res = ((ResDefaults) response).status;
                                if(res) {
                                    new AlertDialog.Builder(mContext)
                                            .setMessage(getResources().getString(R.string.mensaje_enviado))
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    ParticipaActivity.this.finish();
                                                }
                                            } )
                                            .show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                        ApiSingleton.hideProgressFlower();
                    }
                }));
    }
}
