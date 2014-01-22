package com.chanapps.glass.chan.view;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import com.chanapps.glass.chan.R;
import com.chanapps.glass.chan.model.Board;
import com.chanapps.glass.chan.model.Text;
import com.chanapps.glass.chan.util.CardCursorAdapter;
import com.chanapps.glass.chan.util.CardCursorScrollAdapter;
import com.chanapps.glass.chan.util.CardImageLoader;
import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 1/22/14
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class BoardView {

    private static final String TAG = BoardView.class.getSimpleName();
    private static final boolean DEBUG = true;

    //private static final String SUB_FORMAT = "<b>%1$s</b>";
    private static final String SUB_FORMAT = "%1$s";
    private static final String COM_FORMAT = "%1$s";
    //private static final String SUB_AND_COM_FORMAT = "<b>%1$s</b> %2$s";
    private static final String SUB_AND_COM_FORMAT = "%1$s ~ %2$s";

    private Context mContext;
    private CardScrollView mCardScrollView;
    private int mSelectedPosition = -1;

    public BoardView(Context context, CardScrollView cardScrollView) {
        mContext = context;
        mCardScrollView = cardScrollView;
    }

    public CardCursorAdapter.NewCardCallback newCardCallback() {
        return new CardCursorAdapter.NewCardCallback() {
            @Override
            public Card newCard(Cursor cursor) {
                final Card card = new Card(mContext);
                card.setText(formattedSubCom(cursor));

                String board = cursor.getString(cursor.getColumnIndex(Board.BOARD_COLUMN));
                int pos = cursor.getPosition();
                int count = cursor.getCount();
                int replies = cursor.getInt(cursor.getColumnIndex(Board.REPLIES_COLUMN));
                int images = cursor.getInt(cursor.getColumnIndex(Board.IMAGES_COLUMN));
                card.setFootnote(mContext.getString(R.string.board_footnote_format, board, pos + 1, count, replies, images));
                /*
                long tim = cursor.getLong(cursor.getColumnIndex(Board.TIM_COLUMN));
                if (tim > 0 && mCardScrollView != null && mCardScrollView.getSelectedItemPosition() == pos) {
                    String url = String.format(Board.THUMBNAIL_FORMAT, board, tim);
                    CardImageLoader.loadCardImage(card, Card.ImageLayout.FULL, url, mCardScrollView);
                }
                */
                return card;
            }
        };
    }

    public String formattedSubCom(Cursor cursor) {
        String sub = cursor.getString(cursor.getColumnIndex(Board.SUB_COLUMN));
        String com = cursor.getString(cursor.getColumnIndex(Board.COM_COLUMN));
        String text;
        if (sub != null && !sub.isEmpty() && com != null && !com.isEmpty())
            text = String.format(SUB_AND_COM_FORMAT, sub, com);
        else if (sub != null && !sub.isEmpty())
            text = String.format(SUB_FORMAT, sub);
        else if (com != null && !com.isEmpty())
            text = String.format(COM_FORMAT, com);
        else
            text = "";
        return Text.filter(text);
    }

    /*
    public AdapterView.OnItemSelectedListener onItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == mSelectedPosition)
                    return;
                mSelectedPosition = position;
                if (mCardScrollView != null)
                    mCardScrollView.updateViews(true);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }
    */

}
