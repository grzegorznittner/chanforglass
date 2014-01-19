package com.chanapps.glass.chan;

import android.app.Activity;
import android.app.LoaderManager;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import com.chanapps.glass.chan.util.CardCursorAdapter;
import com.chanapps.glass.chan.util.CardCursorScrollAdapter;
import com.chanapps.glass.chan.util.JSONLoaderCallbacks;
import com.chanapps.glass.chan.util.JSONType;
import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

public class ChanBoardListActivity extends Activity {

    private static final String TAG = ChanBoardListActivity.class.getSimpleName();
    private static final String BOARD_LIST_URL = "https://a.4cdn.org/boards.json";
    private static final int BOARD_LIST_INITIAL_CAPACITY = 70;
    private static final int LOADER_ID = 1;
    private static final String BOARDS_KEY = "boards";
    private static final String BOARD_COLUMN = "board";
    private static final String TITLE_COLUMN = "title";
    private static final String WS_BOARD_COLUMN = "ws_board";
    private static final String[] BOARD_LIST_COLUMNS = {
            BOARD_COLUMN,
            TITLE_COLUMN,
            WS_BOARD_COLUMN
    };
    private static final JSONType[] BOARD_LIST_TYPES = {
            JSONType.STRING,
            JSONType.STRING,
            JSONType.INTEGER
    };

    private ProgressBar mProgressBar;
    private CardScrollView mCardScrollView;
    private CardCursorScrollAdapter mAdapter;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootLayout = getLayoutInflater().inflate(R.layout.card_scroll_with_progress_layout, null);
        mProgressBar = (ProgressBar)rootLayout.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        setContentView(rootLayout);

        mAdapter = new CardCursorScrollAdapter();
        mAdapter.setIdColumn(BOARD_COLUMN);
        mAdapter.setNewCardCallback(new CardCursorAdapter.NewCardCallback() {
            @Override
            public Card newCard(Cursor cursor) {
                Card card = new Card(ChanBoardListActivity.this);
                card.setText(String.format("/%1$s/", cursor.getString(cursor.getColumnIndex(BOARD_COLUMN))));
                card.setFootnote(cursor.getString(cursor.getColumnIndex(TITLE_COLUMN)));
                return card;
            }
        });

        mCardScrollView = (CardScrollView)rootLayout.findViewById(R.id.card_scroll_view);
        mCardScrollView.setAdapter(mAdapter);
        mCallbacks = new JSONLoaderCallbacks(this, mAdapter, mProgressBar, mCardScrollView, BOARD_LIST_URL, BOARD_LIST_COLUMNS, BOARD_LIST_TYPES,
                BOARDS_KEY, BOARD_LIST_INITIAL_CAPACITY, LOADER_ID);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_ID, null, mCallbacks);
    }

}