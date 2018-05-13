package com.richydave.photogallerydemo;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class PhotoViewPager extends ViewPager {

    private boolean swiped;

    public PhotoViewPager(Context context) {
        super(context);
    }

    public PhotoViewPager(Context context, AttributeSet attr) {
        super(context, attr);
        this.swiped = true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.swiped && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.swiped && super.onTouchEvent(event);
    }

    public void setSwiped(boolean canSwipe) {
        this.swiped = canSwipe;
    }

}
