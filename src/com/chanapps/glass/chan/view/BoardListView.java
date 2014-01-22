package com.chanapps.glass.chan.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import com.chanapps.glass.chan.ChanThreadActivity;
import com.chanapps.glass.chan.R;
import com.chanapps.glass.chan.model.Board;
import com.chanapps.glass.chan.model.BoardList;
import com.chanapps.glass.chan.util.CardCursorAdapter;
import com.chanapps.glass.chan.util.CardCursorScrollAdapter;
import com.google.android.glass.app.Card;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 1/22/14
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class BoardListView {

    private static final String BOARD_TEXT_FORMAT = "/%1$s/ %2$s";

    private Context mContext;

    public BoardListView(Context context) {
        mContext = context;
    }

    public CardCursorAdapter.NewCardCallback newCardCallback() {
        return new CardCursorAdapter.NewCardCallback() {
            @Override
            public Card newCard(Cursor cursor) {
                Card card = new Card(mContext);
                card.setText(String.format(BOARD_TEXT_FORMAT,
                        cursor.getString(cursor.getColumnIndex(BoardList.BOARD_COLUMN)),
                        cursor.getString(cursor.getColumnIndex(BoardList.TITLE_COLUMN))));
                card.setFootnote(mContext.getString(R.string.app_name));
                return card;
            }
        };
    }

}
