package com.chanapps.glass.chan;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.chanapps.glass.chan.model.ChanThread;
import com.chanapps.glass.chan.util.CardImageLoader;
import com.chanapps.glass.chan.view.ChanImageView;

public class ChanImageActivity extends Activity {

    private static final boolean DEBUG = true;

    private static final int LOADER_ID = 1;
    private static final String TAG = ChanImageActivity.class.getSimpleName();

    private ProgressBar mProgressBar;
    private String mBoard;
    private long mTim;
    private String mExt;
    private ChanImageView mChanImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(ChanThread.BOARD_COLUMN))
            mBoard = savedInstanceState.getString(ChanThread.BOARD_COLUMN);
        else if (getIntent() != null && getIntent().hasExtra(ChanThread.BOARD_COLUMN))
            mBoard = getIntent().getStringExtra(ChanThread.BOARD_COLUMN);
        else {
            Log.e(TAG, "no board code found");
            finish();
            return;
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(ChanThread.TIM_COLUMN))
            mTim = savedInstanceState.getLong(ChanThread.TIM_COLUMN);
        else if (getIntent() != null && getIntent().hasExtra(ChanThread.TIM_COLUMN))
            mTim = getIntent().getLongExtra(ChanThread.TIM_COLUMN, 0);
        else {
            Log.e(TAG, "no tim found");
            finish();
            return;
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(ChanThread.EXT_COLUMN))
            mExt = savedInstanceState.getString(ChanThread.EXT_COLUMN);
        else if (getIntent() != null && getIntent().hasExtra(ChanThread.EXT_COLUMN))
            mExt = getIntent().getStringExtra(ChanThread.EXT_COLUMN);
        else {
            Log.e(TAG, "no ext found");
            finish();
            return;
        }

        CardImageLoader.init(this);

        View rootLayout = getLayoutInflater().inflate(R.layout.card_scroll_layout, null);
        mProgressBar = (ProgressBar)rootLayout.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        setContentView(rootLayout);

        mChanImageView = new ChanImageView(this);
        mChanImageView.displayImage(mBoard, mTim, mExt);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mChanImageView = null;
    }

}