package com.clickaboom.letrasparavolar.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static com.clickaboom.letrasparavolar.activities.MainActivity.EXTRA_BOOK_ITEM;
import static com.clickaboom.letrasparavolar.activities.MainActivity.EXTRA_OPEN_GAMES;

/**
 * Created by karen on 21/06/17.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_BOOK_ITEM, getIntent().getSerializableExtra(EXTRA_BOOK_ITEM));
        intent.putExtra(EXTRA_OPEN_GAMES, getIntent().getBooleanExtra(EXTRA_OPEN_GAMES, false));

        getIntent().removeExtra(EXTRA_BOOK_ITEM);
        getIntent().removeExtra(EXTRA_OPEN_GAMES);

        startActivity(intent);

        finish();
    }
}