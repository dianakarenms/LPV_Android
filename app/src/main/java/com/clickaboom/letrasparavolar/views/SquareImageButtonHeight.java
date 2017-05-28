package com.clickaboom.letrasparavolar.views;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by webdev_t5 on 13/10/15.
 */
public class SquareImageButtonHeight extends android.support.v7.widget.AppCompatImageButton {

    public SquareImageButtonHeight(Context context) {
        super(context);
    }

    public SquareImageButtonHeight(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SquareImageButtonHeight(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredHeight());
    }
}
