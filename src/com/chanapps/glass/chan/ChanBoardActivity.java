package com.chanapps.glass.chan;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import com.chanapps.glass.chan.model.Board;
import com.chanapps.glass.chan.model.BoardList;
import com.chanapps.glass.chan.model.CursorLoadCallback;
import com.chanapps.glass.chan.model.Text;
import com.chanapps.glass.chan.util.CardCursorAdapter;
import com.chanapps.glass.chan.util.CardCursorScrollAdapter;
import com.chanapps.glass.chan.util.CardImageLoader;
import com.chanapps.glass.chan.util.JSONLoaderCallbacks;
import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

public class ChanBoardActivity extends Activity {

    private static final boolean DEBUG = true;

    private static final int LOADER_ID = 1;
    private static final String TAG = ChanBoardActivity.class.getSimpleName();
    //private static final String SUB_FORMAT = "<b>%1$s</b>";
    private static final String SUB_FORMAT = "%1$s";
    private static final String COM_FORMAT = "%1$s";
    //private static final String SUB_AND_COM_FORMAT = "<b>%1$s</b> %2$s";
    private static final String SUB_AND_COM_FORMAT = "%1$s ~ %2$s";

    private ProgressBar mProgressBar;
    private CardScrollView mCardScrollView;
    private CardCursorScrollAdapter mAdapter;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    private String mBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(BoardList.BOARD_COLUMN))
            mBoard = savedInstanceState.getString(BoardList.BOARD_COLUMN);
        else if (getIntent() != null && getIntent().hasExtra(BoardList.BOARD_COLUMN))
            mBoard = getIntent().getStringExtra(BoardList.BOARD_COLUMN);
        else {
            Log.e(TAG, "no board code found");
            finish();
            return;
        }

        CardImageLoader.init(this);

        View rootLayout = getLayoutInflater().inflate(R.layout.card_scroll_with_progress_layout, null);
        mProgressBar = (ProgressBar)rootLayout.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        setContentView(rootLayout);

        mAdapter = new CardCursorScrollAdapter();
        mAdapter.setIdColumn(Board.BOARD_COLUMN);
        mAdapter.setNewCardCallback(new CardCursorAdapter.NewCardCallback() {
            @Override
            public Card newCard(Cursor cursor) {
                final Card card = new Card(ChanBoardActivity.this);
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
                text = Text.filter(text);
                card.setText(text);

                String board = cursor.getString(cursor.getColumnIndex(Board.BOARD_COLUMN));
                //long no = cursor.getLong(cursor.getColumnIndex(Board.NO_COLUMN));
                int pos = cursor.getPosition();
                int count = cursor.getCount();
                int replies = cursor.getInt(cursor.getColumnIndex(Board.REPLIES_COLUMN));
                int images = cursor.getInt(cursor.getColumnIndex(Board.IMAGES_COLUMN));
                card.setFootnote(getString(R.string.board_footnote_format, board, pos + 1, count, replies, images));

                long tim = cursor.getLong(cursor.getColumnIndex(Board.TIM_COLUMN));
                if (tim > 0) {
                    String url = String.format(Board.THUMBNAIL_FORMAT, board, tim);
                    CardImageLoader.loadCardImage(card, Card.ImageLayout.LEFT, url, mCardScrollView);
                }
                return card;
            }
        });

        mCardScrollView = (CardScrollView)rootLayout.findViewById(R.id.card_scroll_view);
        mCardScrollView.setAdapter(mAdapter);
        mCardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mAdapter.getCursor();
                if (cursor == null || !cursor.moveToPosition(position))
                    return;
                String board = cursor.getString(cursor.getColumnIndex(Board.BOARD_COLUMN));
                long no = cursor.getLong(cursor.getColumnIndex(Board.NO_COLUMN));
                Intent intent = new Intent(ChanBoardActivity.this, ChanThreadActivity.class);
                intent.putExtra(Board.BOARD_COLUMN, board);
                intent.putExtra(Board.NO_COLUMN, no);
                startActivity(intent);
            }
        });
        mCallbacks = new JSONLoaderCallbacks(this, mAdapter, mProgressBar, mCardScrollView,
                new CursorLoadCallback() {
                    @Override
                    public Cursor loadCursor() {
                        return new Board().loadPageCursor(mBoard);
                        //return new Board().loadCatalogCursor(mBoard);
                    }
                }, LOADER_ID);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_ID, null, mCallbacks);
    }

}