package com.chanapps.glass.chan.util;

import android.database.Cursor;
import com.chanapps.glass.chan.ChanBoardListActivity;
import com.google.android.glass.app.Card;

/**
* Created with IntelliJ IDEA.
* User: johnarleyburns
* Date: 1/19/14
* Time: 5:22 PM
* To change this template use File | Settings | File Templates.
*/
public interface CardCursorAdapter {
    Cursor swapCursor(Cursor data);
    Cursor getCursor();
    void setIdColumn(String column);
    void setNewCardCallback(NewCardCallback callback);
    public interface NewCardCallback {
        Card newCard(Cursor cursor);
    }
}
