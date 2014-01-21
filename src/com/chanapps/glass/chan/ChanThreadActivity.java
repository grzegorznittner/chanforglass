package com.chanapps.glass.chan;

import android.app.Activity;
import android.app.LoaderManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.chanapps.glass.chan.model.Board;
import com.chanapps.glass.chan.model.ChanThread;
import com.chanapps.glass.chan.model.CursorLoadCallback;
import com.chanapps.glass.chan.model.Text;
import com.chanapps.glass.chan.util.CardCursorAdapter;
import com.chanapps.glass.chan.util.CardCursorScrollAdapter;
import com.chanapps.glass.chan.util.CardImageLoader;
import com.chanapps.glass.chan.util.JSONLoaderCallbacks;
import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

import java.text.DecimalFormat;

public class ChanThreadActivity extends Activity {

    private static final boolean DEBUG = true;

    private static final int LOADER_ID = 1;
    private static final String TAG = ChanThreadActivity.class.getSimpleName();
    //private static final String SUB_FORMAT = "<b>%1$s</b>";
    private static final String SUB_FORMAT = "%1$s";
    private static final String COM_FORMAT = "%1$s";
    //private static final String SUB_AND_COM_FORMAT = "<b>%1$s</b> %2$s";
    private static final String SUB_AND_COM_FORMAT = "%1$s ~ %2$s";
    private static final long KB = 1024;
    private static final long MB = KB * KB;
    private static final String MB_FORMAT = "0.0";
    private static final String KB_FORMAT = "0";

    private ProgressBar mProgressBar;
    private CardScrollView mCardScrollView;
    private CardCursorScrollAdapter mAdapter;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    private String mBoard;
    private long mNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(Board.BOARD_COLUMN))
            mBoard = savedInstanceState.getString(Board.BOARD_COLUMN);
        else if (getIntent() != null && getIntent().hasExtra(Board.BOARD_COLUMN))
            mBoard = getIntent().getStringExtra(Board.BOARD_COLUMN);
        else {
            Log.e(TAG, "no board code found");
            finish();
            return;
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(Board.NO_COLUMN))
            mNo = savedInstanceState.getLong(Board.NO_COLUMN);
        else if (getIntent() != null && getIntent().hasExtra(Board.NO_COLUMN))
            mNo = getIntent().getLongExtra(Board.NO_COLUMN, 0);
        else {
            Log.e(TAG, "no thread number found");
            finish();
            return;
        }

        CardImageLoader.init(this);

        View rootLayout = getLayoutInflater().inflate(R.layout.card_scroll_with_progress_layout, null);
        mProgressBar = (ProgressBar)rootLayout.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        setContentView(rootLayout);

        mAdapter = new CardCursorScrollAdapter();
        mAdapter.setIdColumn(ChanThread.NO_COLUMN);
        mAdapter.setNewCardCallback(new CardCursorAdapter.NewCardCallback() {
            @Override
            public Card newCard(Cursor cursor) {
                final Card card = new Card(ChanThreadActivity.this);
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
                        fdisplaysize = new DecimalFormat(MB_FORMAT).format((float)fsize / (float)MB);
                        fdesc = getString(R.string.mb_abbrev);
                    }
                    else {
                        fdisplaysize = new DecimalFormat(KB_FORMAT).format((float)fsize / (float)KB);
                        fdesc = getString(R.string.kb_abbrev);
                    }
                    if (ext != null && ext.startsWith("."))
                        ext = ext.replaceFirst("\\.", "");
                    card.setFootnote(getString(R.string.thread_with_image_footnote_format,
                            board, threadNo, pos + 1, count, fdisplaysize, fdesc, ext));
                }
                else {
                    card.setFootnote(getString(R.string.thread_footnote_format, board, threadNo, pos + 1, count));
                }

                long tim = cursor.getLong(cursor.getColumnIndex(ChanThread.TIM_COLUMN));
                // if (tim > 0) {
                if (tim > 0 && resto == 0) {
                    String url = String.format(ChanThread.THUMBNAIL_FORMAT, board, tim);
                    Card.ImageLayout imageLayout = resto == 0 ? Card.ImageLayout.FULL : Card.ImageLayout.LEFT;
                    CardImageLoader.loadCardImage(card, imageLayout, url, mCardScrollView);
                }
                return card;
            }
        });

        mCardScrollView = (CardScrollView)rootLayout.findViewById(R.id.card_scroll_view);
        mCardScrollView.setAdapter(mAdapter);
        mCallbacks = new JSONLoaderCallbacks(this, mAdapter, mProgressBar, mCardScrollView,
                new CursorLoadCallback() {
                    @Override
                    public Cursor loadCursor() {
                        return new ChanThread().loadCursor(mBoard, mNo);
                    }
                }, LOADER_ID);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_ID, null, mCallbacks);
    }

}