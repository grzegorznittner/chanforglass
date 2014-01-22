package com.chanapps.glass.chan.view;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import com.chanapps.glass.chan.R;
import com.chanapps.glass.chan.model.Board;
import com.chanapps.glass.chan.model.ChanThread;
import com.chanapps.glass.chan.model.Text;
import com.chanapps.glass.chan.util.CardCursorAdapter;
import com.chanapps.glass.chan.util.CardImageLoader;
import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

import java.text.DecimalFormat;

public class ChanThreadView {

    //private static final String SUB_FORMAT = "<b>%1$s</b>";
    private static final String SUB_FORMAT = "%1$s";
    private static final String COM_FORMAT = "%1$s";
    //private static final String SUB_AND_COM_FORMAT = "<b>%1$s</b> %2$s";
    private static final String SUB_AND_COM_FORMAT = "%1$s ~ %2$s";
    private static final long KB = 1024;
    private static final long MB = KB * KB;
    private static final String MB_FORMAT = "0.0";
    private static final String KB_FORMAT = "0";

    private Context mContext;
    private CardScrollView mCardScrollView;
    private int mSelectedPosition = -1;

    public ChanThreadView(Context context, CardScrollView cardScrollView) {
        mContext = context;
        mCardScrollView = cardScrollView;
    }

    public CardCursorAdapter.NewCardCallback newCardCallback() {
        return new CardCursorAdapter.NewCardCallback() {
            @Override
            public Card newCard(Cursor cursor) {
                final Card card = new Card(mContext);
                String sub = cursor.getString(cursor.getColumnIndex(ChanThread.SUB_COLUMN));
                String com = cursor.getString(cursor.getColumnIndex(ChanThread.COM_COLUMN));
                String text;
                if (sub != null && !sub.isEmpty() && com != null && !com.isEmpty())
                    text = String.format(SUB_AND_COM_FORMAT, sub, com);
                else if (sub != null && !sub.isEmpty())
                    text = String.format(SUB_FORMAT, sub);
                else if (com != null && !com.isEmpty())
                    text = String.format(COM_FORMAT, com);
                else
                    text = "";
                text = Text.filter(text);
                card.setText(text);

                String board = cursor.getString(cursor.getColumnIndex(ChanThread.BOARD_COLUMN));
                long no = cursor.getLong(cursor.getColumnIndex(ChanThread.NO_COLUMN));
                long resto = cursor.getLong(cursor.getColumnIndex(ChanThread.RESTO_COLUMN));
                long threadNo = resto == 0 ? no : resto;
                int pos = cursor.getPosition();
                int count = cursor.getCount();
                long fsize = cursor.getLong(cursor.getColumnIndex(ChanThread.FSIZE_COLUMN));
                String ext = cursor.getString(cursor.getColumnIndex(ChanThread.EXT_COLUMN));
                if (fsize > 0) {
                    String fdisplaysize;
                    String fdesc;
                    if (fsize > MB) {
                        fdisplaysize = new DecimalFormat(MB_FORMAT).format((float) fsize / (float) MB);
                        fdesc = mContext.getString(R.string.mb_abbrev);
                    }
                    else {
                        fdisplaysize = new DecimalFormat(KB_FORMAT).format((float) fsize / (float) KB);
                        fdesc = mContext.getString(R.string.kb_abbrev);
                    }
                    if (ext != null && ext.startsWith("."))
                        ext = ext.replaceFirst("\\.", "");
                    card.setFootnote(mContext.getString(R.string.thread_with_image_footnote_format,
                            board, threadNo, pos + 1, count, fdisplaysize, fdesc, ext));
                }
                else {
                    card.setFootnote(mContext.getString(R.string.thread_footnote_format, board, threadNo, pos + 1, count));
                }
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
