package com.clickaboom.letrasparavolar.activities;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.adapters.InGameAdapter;
import com.clickaboom.letrasparavolar.models.ImgUrlResponse;
import com.clickaboom.letrasparavolar.models.game.Game;
import com.clickaboom.letrasparavolar.models.game.Pregunta;
import com.clickaboom.letrasparavolar.models.game.ResNahuatlismos;
import com.clickaboom.letrasparavolar.models.game.ResResultadoTest;
import com.clickaboom.letrasparavolar.models.game.Respuesta;
import com.clickaboom.letrasparavolar.models.game.Resultado;
import com.clickaboom.letrasparavolar.models.game.ResultadoTest;
import com.clickaboom.letrasparavolar.network.ApiConfig;
import com.clickaboom.letrasparavolar.network.ApiSingleton;
import com.clickaboom.letrasparavolar.network.GsonRequest;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.data;
import static com.clickaboom.letrasparavolar.network.DownloadFile.isStoragePermissionGranted;

public class JuegosInGameActivity extends AppCompatActivity
        implements View.OnClickListener, InGameAdapter.OnClickListener {

    private static final String TAG = "com.lpv.GamesActivity";
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
    private HashMap<Integer, Integer> mCurioAnswers;

    public static Intent newIntent(Context packageContext, Game game) {
        Intent i = new Intent(packageContext, JuegosInGameActivity.class);
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
        mCurioAnswers = new HashMap<Integer, Integer>();

        // Menu drawer onclicklistener
        findViewById(R.id.drawer_button).setVisibility(View.INVISIBLE);

        // Set toolbar_asistant title
        ((TextView)findViewById(R.id.toolbar_title)).setText(mGame.gameType);
        findViewById(R.id.toolbar_prev_btn).setVisibility(View.GONE);
        findViewById(R.id.toolbar_next_btn).setVisibility(View.GONE);

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
        mAdapter.setGameType(mGame.gameType);
        mRecyclerView.setAdapter(mAdapter);

        // Modal buttons controller
        mRepeatBtn = (Button) findViewById(R.id.repeat_btn);
        mFinishBtn = (Button) findViewById(R.id.repeat_btn);
        mShareBtn = (Button) findViewById(R.id.share_btn);
        if(mGame.gameType.equals(JuegosMenuActivity.JUEGO_B)) {
            mRepeatBtn.setText("Hacer otro test");
            mShareBtn.setText("Compartir resultado");
            findViewById(R.id.nahuatlismos_result).setVisibility(View.GONE);
            findViewById(R.id.curioseando_result).setVisibility(View.VISIBLE);
        }

        // Setup nextbtn
        mNextBtn = (Button) findViewById(R.id.next_btn);
        invalidNextBtn();

        if(mGame.gameType.equals(JuegosMenuActivity.JUEGO_A))
            loadNahuatlismosQuestions();
        else
            loadCurioseandoQuestions(mGame.id);

    }

    private void invalidNextBtn() {
        mNextBtn.setClickable(false);
        mNextBtn.setBackgroundColor(getResources().getColor(R.color.gray));
    }

    private void validNextBtn(int btnColor) {
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
                    if(mGame.gameType.equals(JuegosMenuActivity.JUEGO_A)) {
                        TextView tv = (TextView) modalView.findViewById(R.id.puntua_txt);
                        if (tv != null)
                            tv.setText(String.valueOf(mCorrectCounter));
                    } else {
                        loadCurioseandoResult(getResultId());
                    }
                }
                break;
            case R.id.repeat_btn:
                if(mGame.gameType.equals(JuegosMenuActivity.JUEGO_A)) {
                    mCorrectCounter = 0;
                    mQuestionIndex = 0;
                    findViewById(R.id.modal_game_over).setVisibility(View.GONE);
                    setQuestion(mPregList.get(mQuestionIndex));
                } else {
                    finish();
                }
                break;
            case R.id.finish_btn:
                finish();
                break;
            case R.id.share_btn:
                if (isStoragePermissionGranted(JuegosInGameActivity.this)) {
                    shareImage();
                }
                break;
        }
    }

    private int getResultId() {
        Map.Entry<Integer, Integer> maxEntry = null;
        for (Map.Entry<Integer, Integer> entry : mCurioAnswers.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
                Log.d(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
        }
        return  maxEntry.getKey();
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

    private void loadCurioseandoQuestions(int testId) {
        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(mContext)
                .addToRequestQueue(new GsonRequest(ApiConfig.curioseandoTestQuestions + "?test=" + testId,
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

    private void loadCurioseandoResult(int resId) {
        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(mContext)
                .addToRequestQueue(new GsonRequest(ApiConfig.curioseandoTestResult + "?resultado=" + resId,
                        ResResultadoTest.class,
                        Request.Method.GET,
                        null, null,
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                Log.d(TAG, response.toString());
                                List<ResultadoTest> res = ((ResResultadoTest) response).data;
                                ResultadoTest resultado = res.get(0);

                                RelativeLayout modalView = (RelativeLayout) findViewById(R.id.modal_game_over);
                                NoticiasDetailActivity.setHtmlText(resultado.resultado, ((TextView) modalView.findViewById(R.id.res_title)));
                                NoticiasDetailActivity.setHtmlText(resultado.descripcion, ((TextView) modalView.findViewById(R.id.description_txt)));

                                String imgUrl = ApiConfig.juegosImg + resultado.imagen;
                                Picasso.with(mContext)
                                        .load(imgUrl)
                                        .resize(400,400)
                                        .centerInside()
                                        .into((ImageView) modalView.findViewById(R.id.res_img));
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
        validNextBtn(mGame.btnColor);
        if(mGame.gameType.equals(JuegosMenuActivity.JUEGO_A)) {
            if (res.isCorrecta.equals("SI"))
                mCorrectCounter++;
            else {
                InGameAdapter.NahuatlismosHolder holder = (InGameAdapter.NahuatlismosHolder) mRecyclerView.findViewHolderForAdapterPosition(correctPos);
                holder.mCheckImg.setImageResource(R.drawable.checked);
            }
        } else if(mGame.gameType.equals(JuegosMenuActivity.JUEGO_B)) {
            for(Resultado answer: res.resultados) {
                int newValue = 0;
                if(mCurioAnswers.get(answer.resultadosCurioseandoId) != null)
                    newValue = mCurioAnswers.get(answer.resultadosCurioseandoId) + answer.valor; // Sum previous value with new retrieved value

                mCurioAnswers.put(answer.resultadosCurioseandoId, newValue);
            }
        }
    }

    private void shareImage(){
        View view;
        /*if(mGame.gameType.equals(JuegosMenuActivity.JUEGO_A))
            view = findViewById(R.id.nahuatlismos_result);
        else
            view = findViewById(R.id.curioseando_result);*/
        view  = findViewById(R.id.main_container);

        findViewById(R.id.back_btn).setVisibility(View.GONE);
        view.setDrawingCacheEnabled(true);
        Bitmap icon = Bitmap.createBitmap(view.getDrawingCache());

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
        findViewById(R.id.back_btn).setVisibility(View.VISIBLE);

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
        PackageManager pm = getApplicationContext().getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        boolean isFacebookAppInstalled = false;
        for (final ResolveInfo app : activityList) {
            if ((app.activityInfo.name).contains("com.facebook.composer.shareintent")) {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(
                        activity.applicationInfo.packageName,
                        activity.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                shareIntent.setComponent(name);
                startActivity(shareIntent);
                isFacebookAppInstalled = true;
            }
        }

        if(!isFacebookAppInstalled) {
//            dataImgToImgUrl(uploadImages(mContext, uri));
//            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + "https://beebom-redkapmedia.netdna-ssl.com/wp-content/uploads/2016/01/Reverse-Image-Search-Engines-Apps-And-Its-Uses-2016.jpg";
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
//            startActivity(intent);
            Toast.makeText(mContext, "Necesitas instalar la aplicación de Facebook para compartir", Toast.LENGTH_LONG).show();
        }

        /*try {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            share.setPackage("com.facebook.katana");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(share);
        } catch (Exception e) {
            // If we failed (not native FB app installed), try share through SEND
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            share.putExtra(Intent.EXTRA_STREAM, uri);
//            startActivity(Intent.createChooser(share, "Compartir resultado"));
            startActivity(share);
        }*/


//        share.putExtra(Intent.EXTRA_STREAM, uri);
//        startActivity(Intent.createChooser(share, "Compartir resultado"));
    }

    private void dataImgToImgUrl(String uri) {
        Map<String, String> params = new HashMap<>();
        params.put("image", uri);

        // Access the RequestQueue through your singleton class.
        ApiSingleton.getInstance(mContext)
                .addToRequestQueue(new GsonRequest("http://data-uri-to-img-url.herokuapp.com/images.json",
                        ImgUrlResponse.class,
                        Request.Method.POST,
                        null, params,
                        new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                Log.d(TAG, response.toString());
                                String url = ((ImgUrlResponse) response).url;
                                String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + url;
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
                                startActivity(intent);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }));
    }

    private String uploadImages(Context context, Uri filePath) {
        String imageString = "", imgType;
        imgType = getMimeType(context, filePath);

        try {
            //Getting the Bitmap from Gallery
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), filePath);
            imageString = getBase64StringImage(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "data:" + imgType + ";base64," + imageString;
    }

    public static String getBase64StringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static String getMimeType(Context context, Uri uriImage) {
        String strMimeType = "";
        Cursor cursor = context.getContentResolver().query(uriImage,
                new String[]{MediaStore.MediaColumns.MIME_TYPE},
                null, null, null);

        if (cursor != null && cursor.moveToNext()) {
            strMimeType = cursor.getString(0);
        }

        if (strMimeType.isEmpty()) {
            String type = null;
            String extension = MimeTypeMap.getFileExtensionFromUrl(String.valueOf(uriImage));
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
            return type;
        }

        if (strMimeType.isEmpty()) {
            if (uriImage.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                ContentResolver cr = context.getContentResolver();
                strMimeType = cr.getType(uriImage);
            } else {
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uriImage
                        .toString());
                strMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        fileExtension.toLowerCase());
            }
        }
        return strMimeType;
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
