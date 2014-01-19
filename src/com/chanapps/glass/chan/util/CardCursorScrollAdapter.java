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
public class CardCursorScrollAdapter extends CardScrollAdapter implements CardCursorAdapter {

    private Cursor mCursor;
    private String mIdColumn;
    private NewCardCallback mNewCardCallback;

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
            Card card = getCard(i);
            if (card != null && card.equals(item))
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
        return getCard(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mCursor == null)
            return null;
        // ignore convertView
        Card card = getCard(position);
        if (card == null)
            return null;
        View view = card.toView();
        setItemOnCard(card, view);
        return view;
    }

    private Card getCard(int position) {
        if (mCursor == null)
            return null;
        if (!mCursor.moveToPosition(position))
            return null;
        return newCard(mCursor);
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
    public void setNewCardCallback(NewCardCallback callback) {
        mNewCardCallback = callback;
    }

    private Card newCard(Cursor cursor) {
        return mNewCardCallback.newCard(cursor);
    }
}
