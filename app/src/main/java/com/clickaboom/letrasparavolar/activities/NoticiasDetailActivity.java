package com.clickaboom.letrasparavolar.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clickaboom.letrasparavolar.R;
import com.clickaboom.letrasparavolar.models.noticias.Noticia;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;

/**
 * Created by Karencita on 15/05/2017.
 */

public class NoticiasDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "com.lpv.noticiasDetail";
    private static final String EXTRA_NOTICIA = "noticia";
    private Context mContext;

    public static Intent newIntent(Context packageContext, Noticia noticia) {
        Intent i = new Intent(packageContext, NoticiasDetailActivity.class);
        i.putExtra(EXTRA_NOTICIA, noticia);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticia_detail);
        mContext = this;

        Noticia noticia = (Noticia) getIntent().getSerializableExtra(EXTRA_NOTICIA);

        // Menu drawer onclicklistener
        findViewById(R.id.drawer_button).setVisibility(View.INVISIBLE);

        // Set toolbar_asistant gone
        findViewById(R.id.toolbar_asistant).setVisibility(View.GONE);
        findViewById(R.id.toolbar).findViewById(R.id.drawer_button).setVisibility(View.GONE);

        // BackBtn
        LinearLayout backBtn = (LinearLayout) findViewById(R.id.back_btn);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Title
        setHtmlText(noticia.title, (TextView) findViewById(R.id.titleTxt));

        // Date
        setHtmlText(noticia.date, (TextView) findViewById(R.id.dateTxt));

        // Description
        setHtmlText(noticia.description, (TextView) findViewById(R.id.descriptionTxt));

        // Image
        ImageView coverImg = (ImageView) findViewById(R.id.coverImg);
        if(noticia.image == null) {
            coverImg.setVisibility(View.GONE);
        } else {
            String imgUrl = noticia.image;
            Picasso.with(mContext)
                    .load(imgUrl)
                    .resize(300,300)
                    .centerInside()
                    .into(coverImg);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
        }
    }

    public static void setHtmlText(String text, TextView textView) {
//        text = text.replaceAll("&"+"nbsp;", " ");
//        text = text.replaceAll(String.valueOf((char) 160), " ");
        //text = text.replaceAll("\u00a0","");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            text = Html.fromHtml(text,  Html.FROM_HTML_MODE_COMPACT).toString().replaceAll("&nbsp;"," ");
        } else {
            text = Html.fromHtml(text).toString().replaceAll("&nbsp;"," ");
        }
            text = html2text(text);
            textView.setText(text);
    }

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }
}
