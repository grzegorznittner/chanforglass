package com.chanapps.glass.chan.util;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import com.chanapps.glass.chan.model.CursorLoadCallback;
import com.google.android.glass.widget.CardScrollView;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 1/19/14
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class JSONLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
    private Context mContext;
    private ProgressBar mProgressBar;
    private CardCursorAdapter mAdapter;
    private CardScrollView mCardScrollView;
    private CursorLoadCallback mCursorLoadCallback;
    private int mLoaderId;

    public JSONLoaderCallbacks(Context context,
                               CardCursorAdapter adapter, ProgressBar progressBar, CardScrollView cardScrollView,
                               CursorLoadCallback cursorLoadCallback, int loaderId) {
        mContext = context;
        mAdapter = adapter;
        mProgressBar = progressBar;
        mCardScrollView = cardScrollView;
        mCursorLoadCallback = cursorLoadCallback;
        mLoaderId = loaderId;
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == mLoaderId)
            return new JsonCursorLoader(mContext, mCursorLoadCallback);
        else
            return null;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == mLoaderId) {
            mAdapter.swapCursor(data);
            mProgressBar.setVisibility(View.GONE);
            mCardScrollView.activate();
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}
