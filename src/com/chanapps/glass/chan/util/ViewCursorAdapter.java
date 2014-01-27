package com.chanapps.glass.chan.util;

import android.database.Cursor;
import android.view.View;
import com.google.android.glass.app.Card;

/**
* Created with IntelliJ IDEA.
* User: johnarleyburns
* Date: 1/19/14
* Time: 5:22 PM
* To change this template use File | Settings | File Templates.
*/
public interface ViewCursorAdapter extends SwapCursorAdapter {
    Cursor getCursor();
    void setIdColumn(String column);
    void setNewViewCallback(NewViewCallback callback);
    public interface NewViewCallback {
        View newView(Cursor cursor);
    }
}
