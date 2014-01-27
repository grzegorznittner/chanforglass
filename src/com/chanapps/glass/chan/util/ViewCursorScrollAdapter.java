package com.chanapps.glass.chan.util;

import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;

/**
* Created with IntelliJ IDEA.
* User: johnarleyburns
* Date: 1/19/14
* Time: 5:22 PM
* To change this template use File | Settings | File Templates.
*/
public class ViewCursorScrollAdapter extends CardScrollAdapter implements ViewCursorAdapter {

    private Cursor mCursor;
    private String mIdColumn;
    private NewViewCallback mNewViewCallback;

    @Override
    public int findIdPosition(Object id) {
        if (mCursor == null)
            return -1;
        int i = 0;
        while (mCursor.moveToPosition(i)) {
            String board = mCursor.getString(mCursor.getColumnIndex(mIdColumn));
            if (board != null && board.equals(id))
                return i;
            i++;
        }
        return -1;
    }

    @Override
    public int findItemPosition(Object item) {
        if (mCursor == null)
            return -1;
        int i = 0;
        while (mCursor.moveToPosition(i)) {
            Object item2 = getItem(i);
            if (item2 != null && item2.equals(item))
                return i;
            i++;
        }
        return -1;
    }

    @Override
    public int getCount() {
        if (mCursor == null)
            return 0;
        else
            return mCursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        if (mCursor.moveToPosition(position))
            return mCursor.getString(mCursor.getColumnIndex(mIdColumn));
        else
            return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mCursor == null)
            return null;
        // ignore convertView
        Object item = getItem(position);
        if (item == null)
            return null;
        View view = getView(position);
        setItemOnCard(item, view);
        return getView(position);
    }

    private View getView(int position) {
        if (mCursor == null)
            return null;
        if (!mCursor.moveToPosition(position))
            return null;
        return newView(mCursor);
    }

    @Override
    public Cursor swapCursor(Cursor data) {
        Cursor oldCursor = mCursor;
        mCursor = data;
        return oldCursor;
    }

    @Override
    public void setIdColumn(String column) {
        mIdColumn = column;
    }

    @Override
    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public void setNewViewCallback(NewViewCallback callback) {
        mNewViewCallback = callback;
    }

    private View newView(Cursor cursor) {
        return mNewViewCallback.newView(cursor);
    }
}
