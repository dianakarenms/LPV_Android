package com.clickaboom.letrasparavolar.views;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by webdev_t5 on 13/10/15.
 */
public class SquareImageButtonWidth extends android.support.v7.widget.AppCompatImageButton {

    public SquareImageButtonWidth(Context context) {
        super(context);
    }

    public SquareImageButtonWidth(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SquareImageButtonWidth(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
