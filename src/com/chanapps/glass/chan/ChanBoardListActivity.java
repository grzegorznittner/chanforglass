package com.chanapps.glass.chan;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import com.chanapps.glass.chan.model.Board;
import com.chanapps.glass.chan.model.BoardList;
import com.chanapps.glass.chan.model.CursorLoadCallback;
import com.chanapps.glass.chan.util.CardCursorAdapter;
import com.chanapps.glass.chan.util.CardCursorScrollAdapter;
import com.chanapps.glass.chan.util.JSONLoaderCallbacks;
import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollView;

public class ChanBoardListActivity extends Activity {

    private static final String TAG = ChanBoardListActivity.class.getSimpleName();
    private static final int LOADER_ID = 1;
    private static final String BOARD_TEXT_FORMAT = "/%1$s/";

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
        mAdapter.setIdColumn(BoardList.BOARD_COLUMN);
        mAdapter.setNewCardCallback(new CardCursorAdapter.NewCardCallback() {
            @Override
            public Card newCard(Cursor cursor) {
                Card card = new Card(ChanBoardListActivity.this);
                card.setText(String.format(BOARD_TEXT_FORMAT, cursor.getString(cursor.getColumnIndex(BoardList.BOARD_COLUMN))));
                card.setFootnote(cursor.getString(cursor.getColumnIndex(BoardList.TITLE_COLUMN)));
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
                String board = cursor.getString(cursor.getColumnIndex(BoardList.BOARD_COLUMN));
                Intent intent = new Intent(ChanBoardListActivity.this, ChanBoardActivity.class);
                intent.putExtra(BoardList.BOARD_COLUMN, board);
                startActivity(intent);
            }
        });
        mCallbacks = new JSONLoaderCallbacks(this, mAdapter, mProgressBar, mCardScrollView,
                new CursorLoadCallback() {
                    @Override
                    public Cursor loadCursor() {
                        return new BoardList().loadCursor();
                    }
                }, LOADER_ID);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_ID, null, mCallbacks);
    }

}