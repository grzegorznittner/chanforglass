package com.chanapps.glass.chan.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.chanapps.glass.chan.R;

public class SimulatedScrollBar extends View {

    private static final String TAG = SimulatedScrollBar.class.getSimpleName();
    private static final boolean DEBUG = false;
    private static final int WHITE_SCROLLBAR_COLOR = 0xfffefefe;

    private int mScrollPosition;
    private int mNumItems;
    private Paint mPaint;
    private float mInnerWidth;
    private float mInnerHeight;
    private int mOffsetX;
    private int mWidth;
    private int mHeight;

    public SimulatedScrollBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SimulatedScrollBar,
                0, 0);

        try {
            mScrollPosition = a.getInteger(R.styleable.SimulatedScrollBar_scrollPosition, 0);
            mNumItems = a.getInteger(R.styleable.SimulatedScrollBar_numItems, 0);
        } finally {
            a.recycle();
        }
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(WHITE_SCROLLBAR_COLOR);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public int getScrollPosition() {
        return mScrollPosition;
    }

    public void setScrollPosition(int scrollPosition) {
        mScrollPosition = scrollPosition;
        invalidate();
        requestLayout();
    }
    
    public int getNumItems() {
        return mNumItems;
    }

    public void setNumItems(int numItems) {
        mNumItems = numItems;
        invalidate();
        requestLayout();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float xpad = (float)(getPaddingLeft() + getPaddingRight());
        float ypad = (float)(getPaddingTop() + getPaddingBottom());
        mInnerWidth = (float)w - xpad;
        mInnerHeight = (float)h - ypad;
        if (DEBUG) Log.i(TAG, "onSizeChanged() mInnerWidth=" + mInnerWidth + " mInnerHeight=" + mInnerHeight);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float widthFraction = mNumItems > 1 ? 1.0f / (float)mNumItems : 0;
        float scrollFraction = mNumItems > 1 ? (float)mScrollPosition / (float)mNumItems : 0;
        mOffsetX = (int)(mInnerWidth * scrollFraction);
        mWidth = (int)(mInnerWidth * widthFraction);
        mHeight = (int)mInnerHeight;
        Rect rect = new Rect(mOffsetX, 0, mOffsetX + mWidth, mHeight);
        if (DEBUG) Log.i(TAG, "onDraw() mOffsetX=" + mOffsetX + " mWidth=" + mWidth + " mHeight=" + mHeight
                + " mScrollPosition=" + mScrollPosition + " mNumItems=" + mNumItems);
        canvas.drawRect(rect, mPaint);
    }

}
