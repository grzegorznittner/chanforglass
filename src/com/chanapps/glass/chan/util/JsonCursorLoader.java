package com.chanapps.glass.chan.util;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import com.chanapps.glass.chan.ChanBoardListActivity;
import com.chanapps.glass.chan.model.CursorLoadCallback;

/**
* Created with IntelliJ IDEA.
* User: johnarleyburns
* Date: 1/19/14
* Time: 3:54 PM
* To change this template use File | Settings | File Templates.
*/
public class JsonCursorLoader extends CursorLoader {
    private Cursor mData;
    private CursorLoadCallback mCursorLoadCallback;

    public JsonCursorLoader(Context ctx, CursorLoadCallback cursorLoadCallback) {
        super(ctx);
        mCursorLoadCallback = cursorLoadCallback;
    }

    @Override
    public Cursor loadInBackground() {
        return mCursorLoadCallback.loadCursor();
    }

    @Override
    public void deliverResult(Cursor data) {
        if (isReset()) {
            releaseResources(data);
            return;
        }
        Cursor oldData = mData;
        mData = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        }
        if (mObserver == null) {
            mObserver = new CursorObserver();
            // TODO: register the observer
        }
        if (takeContentChanged() || mData == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if (mData != null) {
            releaseResources(mData);
            mData = null;
        }
        if (mObserver != null) {
            // TODO: unregister the observer
            mObserver = null;
        }
    }

    @Override
    public void onCanceled(Cursor data) {
        super.onCanceled(data);
        releaseResources(data);
    }

    private void releaseResources(Cursor data) {
    }

    private CursorObserver mObserver;

    public static class CursorObserver {
    }

}
