package com.chanapps.glass.chan.view;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
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
import com.chanapps.glass.chan.util.CardCursorScrollAdapter;
import com.chanapps.glass.chan.util.CardImageLoader;
import com.chanapps.glass.chan.util.ViewCursorAdapter;
import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DecimalFormat;

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
    private static final long KB = 1024;
    private static final long MB = KB * KB;
    private static final String MB_FORMAT = "0.0";
    private static final String KB_FORMAT = "0";

    private Context mContext;

    public BoardView(Context context) {
        mContext = context;
    }

    public ViewCursorAdapter.NewViewCallback newViewCallback() {
        return new ViewCursorAdapter.NewViewCallback() {
            @Override
            public View newView(Cursor cursor) {
                ViewGroup layout = (ViewGroup)LayoutInflater.from(mContext).inflate(R.layout.card_with_full_image, null);
                TextView text = (TextView)layout.findViewById(R.id.text);
                TextView footnote = (TextView)layout.findViewById(R.id.footnote);
                ImageView image = (ImageView)layout.findViewById(R.id.image);

                text.setText(formattedSubCom(cursor));
                footnote.setText(formattedFootnote(cursor));
                long tim = cursor.getLong(cursor.getColumnIndex(Board.TIM_COLUMN));
                if (tim > 0) {
                    String board = cursor.getString(cursor.getColumnIndex(Board.BOARD_COLUMN));
                    String url = String.format(Board.THUMBNAIL_FORMAT, board, tim);
                    ImageLoader.getInstance().displayImage(url, image);
                }
                return layout;
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
        String board = cursor.getString(cursor.getColumnIndex(Board.BOARD_COLUMN));
        int replies = cursor.getInt(cursor.getColumnIndex(Board.REPLIES_COLUMN));
        int images = cursor.getInt(cursor.getColumnIndex(Board.IMAGES_COLUMN));

        long fsize = cursor.getLong(cursor.getColumnIndex(Board.FSIZE_COLUMN));
        String ext = cursor.getString(cursor.getColumnIndex(Board.EXT_COLUMN));
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
            return mContext.getString(R.string.board_with_image_footnote_format,
                    board, replies, images, fdisplaysize, fdesc, ext);
        }
        else {
            return mContext.getString(R.string.board_footnote_format, board, replies, images);
        }
    }
    
}
