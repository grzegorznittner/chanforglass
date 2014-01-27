package com.chanapps.glass.chan.util;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import com.chanapps.glass.chan.model.CursorLoadCallback;
import com.chanapps.glass.chan.view.SimulatedScrollBar;
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
    private SwapCursorAdapter mAdapter;
    private CardScrollView mCardScrollView;
    private SimulatedScrollBar mSimulatedScrollBar;
    private CursorLoadCallback mCursorLoadCallback;
    private int mLoaderId;

    public JSONLoaderCallbacks(Context context,
                               SwapCursorAdapter adapter, ProgressBar progressBar,
                               CardScrollView cardScrollView, SimulatedScrollBar simulatedScrollBar,
                               CursorLoadCallback cursorLoadCallback, int loaderId) {
        mContext = context;
        mAdapter = adapter;
        mProgressBar = progressBar;
        mCardScrollView = cardScrollView;
        mSimulatedScrollBar = simulatedScrollBar;
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
            if (mSimulatedScrollBar != null) {
                mSimulatedScrollBar.setNumItems(data.getCount());
                mSimulatedScrollBar.setScrollPosition(0);
            }
            mCardScrollView.activate();
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
        if (mSimulatedScrollBar != null) {
            mSimulatedScrollBar.setNumItems(0);
            mSimulatedScrollBar.setScrollPosition(0);
        }
    }

}
