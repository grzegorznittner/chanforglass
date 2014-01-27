package com.chanapps.glass.chan.view;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.chanapps.glass.chan.R;
import com.chanapps.glass.chan.model.Board;
import com.chanapps.glass.chan.model.ChanThread;
import com.chanapps.glass.chan.model.Text;
import com.chanapps.glass.chan.util.CardCursorAdapter;
import com.chanapps.glass.chan.util.CardImageLoader;
import com.chanapps.glass.chan.util.ViewCursorAdapter;
import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;

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

    public ChanThreadView(Context context) {
        mContext = context;
    }

    public ViewCursorAdapter.NewViewCallback newViewCallback() {
        return new ViewCursorAdapter.NewViewCallback() {
            @Override
            public View newView(Cursor cursor) {
                long tim = cursor.getLong(cursor.getColumnIndex(Board.TIM_COLUMN));
                if (tim > 0) {
                    ViewGroup layout = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.card_with_full_image, null);
                    ImageView image = (ImageView)layout.findViewById(R.id.image);
                    String board = cursor.getString(cursor.getColumnIndex(Board.BOARD_COLUMN));
                    String url = String.format(Board.THUMBNAIL_FORMAT, board, tim);
                    ImageLoader.getInstance().displayImage(url, image);
                    TextView text = (TextView)layout.findViewById(R.id.text);
                    TextView footnote = (TextView)layout.findViewById(R.id.footnote);
                    text.setText(formattedSubCom(cursor));
                    footnote.setText(formattedFootnote(cursor));
                    return layout;
                }
                else {
                    Card card = new Card(mContext);
                    card.setText(formattedSubCom(cursor));
                    card.setFootnote(formattedFootnote(cursor));
                    return card.toView();
                }
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

    private String formattedFootnote(Cursor cursor) {
        String board = cursor.getString(cursor.getColumnIndex(ChanThread.BOARD_COLUMN));
        long no = cursor.getLong(cursor.getColumnIndex(ChanThread.NO_COLUMN));
        long resto = cursor.getLong(cursor.getColumnIndex(ChanThread.RESTO_COLUMN));
        long replies = cursor.getLong(cursor.getColumnIndex(ChanThread.REPLIES_COLUMN));
        long images = cursor.getLong(cursor.getColumnIndex(ChanThread.IMAGES_COLUMN));
        long threadNo = resto == 0 ? no : resto;
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
            if (resto == 0)
                return mContext.getString(R.string.thread_with_image_footnote_format,
                        board, threadNo, replies, images, fdisplaysize, fdesc, ext);
            else
                return mContext.getString(R.string.post_with_image_footnote_format,
                    board, threadNo, resto, fdisplaysize, fdesc, ext);
        }
        else {
            if (resto == 0)
                return mContext.getString(R.string.thread_footnote_format, board, threadNo, replies, images);
            else
                return mContext.getString(R.string.post_footnote_format, board, threadNo, resto);
        }
    }

}
