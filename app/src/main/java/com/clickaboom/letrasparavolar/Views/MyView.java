package com.clickaboom.letrasparavolar.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.clickaboom.letrasparavolar.R;

/**
 * Created by Karencita on 13/05/2017.
 */

public class MyView extends View {
    Rect rect;
    BitmapDrawable mDrawable;

    public MyView(Context context) {
        super(context);
        rect = new Rect(0, 0, 400, 240);
        mDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.sub_header_back);
        mDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mDrawable.setBounds(rect);

        this.setBackgroundResource(R.drawable.sub_header_back);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDrawable.draw(canvas);
    }
}