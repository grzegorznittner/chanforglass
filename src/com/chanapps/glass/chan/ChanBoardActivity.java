package com.chanapps.glass.chan;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import com.chanapps.glass.chan.model.Board;
import com.chanapps.glass.chan.model.BoardList;
import com.chanapps.glass.chan.model.CursorLoadCallback;
import com.chanapps.glass.chan.util.CardImageLoader;
import com.chanapps.glass.chan.util.JSONLoaderCallbacks;
import com.chanapps.glass.chan.util.ViewCursorScrollAdapter;
import com.chanapps.glass.chan.view.BoardView;
import com.chanapps.glass.chan.view.SimulatedScrollBar;
import com.google.android.glass.widget.CardScrollView;

public class ChanBoardActivity extends Activity {

    private static final boolean DEBUG = true;

    private static final int LOADER_ID = 1;
    private static final String TAG = ChanBoardActivity.class.getSimpleName();

    private TextToSpeech mSpeech;
    private ProgressBar mProgressBar;
    private CardScrollView mCardScrollView;
    private SimulatedScrollBar mSimulatedScrollBar;
    private ViewCursorScrollAdapter mAdapter;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    private String mBoard;
    private BoardView mBoardView;
    private int mCurrentPosition = -1;

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

        mSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // Do nothing.
            }
        });
        CardImageLoader.init(this);

        View rootLayout = getLayoutInflater().inflate(R.layout.card_scroll_layout, null);
        mProgressBar = (ProgressBar)rootLayout.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        setContentView(rootLayout);

        mCardScrollView = (CardScrollView)rootLayout.findViewById(R.id.card_scroll_view);

        mBoardView = new BoardView(this);
        mAdapter = new ViewCursorScrollAdapter();
        mAdapter.setIdColumn(Board.BOARD_COLUMN);
        mAdapter.setNewViewCallback(mBoardView.newViewCallback());

        mSimulatedScrollBar = (SimulatedScrollBar)rootLayout.findViewById(R.id.simulated_scroll_bar);

        mCardScrollView.setAdapter(mAdapter);
        mCardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentPosition = position;
                openOptionsMenu();
            }
        });
        mCardScrollView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mSimulatedScrollBar != null)
                    mSimulatedScrollBar.setScrollPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mCallbacks = new JSONLoaderCallbacks(this, mAdapter, mProgressBar, mCardScrollView, mSimulatedScrollBar,
                new CursorLoadCallback() {
                    @Override
                    public Cursor loadCursor() {
                        //return new Board().loadPageCursor(mBoard);
                        return new Board().loadCatalogCursor(mBoard);
                    }
                }, LOADER_ID);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_ID, null, mCallbacks);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.board_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.view_image);
        item.setVisible(currentSelectionIsImage());
        return super.onPrepareOptionsMenu(menu);
    }

    private boolean currentSelectionIsImage() {
        if (mCardScrollView == null || mAdapter == null)
            return false;
        int pos = mCardScrollView.getSelectedItemPosition();
        if (pos < 0)
            return false;
        Cursor cursor = mAdapter.getCursor();
        if (cursor == null || !cursor.moveToPosition(pos))
            return false;
        long fsize = cursor.getLong(cursor.getColumnIndex(Board.FSIZE_COLUMN));
        if (fsize <= 0)
            return false;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Cursor cursor;
        switch (item.getItemId()) {
            case R.id.view_thread:
                cursor = mAdapter.getCursor();
                if (cursor == null || !cursor.moveToPosition(mCurrentPosition))
                    return true;
                String board = cursor.getString(cursor.getColumnIndex(Board.BOARD_COLUMN));
                long no = cursor.getLong(cursor.getColumnIndex(Board.NO_COLUMN));
                Intent intent = new Intent(this, ChanThreadActivity.class);
                intent.putExtra(Board.BOARD_COLUMN, board);
                intent.putExtra(Board.NO_COLUMN, no);
                startActivity(intent);
                return true;
            case R.id.view_image:
                cursor = mAdapter.getCursor();
                if (cursor == null || !cursor.moveToPosition(mCurrentPosition))
                    return true;
                board = cursor.getString(cursor.getColumnIndex(Board.BOARD_COLUMN));
                long tim = cursor.getLong(cursor.getColumnIndex(Board.TIM_COLUMN));
                String ext = cursor.getString(cursor.getColumnIndex(Board.EXT_COLUMN));
                intent = new Intent(this, ChanImageActivity.class);
                intent.putExtra(Board.BOARD_COLUMN, board);
                intent.putExtra(Board.TIM_COLUMN, tim);
                intent.putExtra(Board.EXT_COLUMN, ext);
                startActivity(intent);
                return true;
            case R.id.read_more:
                cursor = mAdapter.getCursor();
                if (cursor == null || !cursor.moveToPosition(mCurrentPosition))
                    return true;
                String text = mBoardView.formattedSubCom(cursor);
                intent = new Intent(this, ChanTextActivity.class);
                intent.putExtra(ChanTextActivity.TEXT, text);
                startActivity(intent);
                return true;
            case R.id.read_aloud:
                cursor = mAdapter.getCursor();
                if (cursor == null || !cursor.moveToPosition(mCurrentPosition))
                    return true;
                text = mBoardView.formattedSubCom(cursor);
                mSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBoardView = null;
        if (mSpeech != null)
            mSpeech.shutdown();
        mSpeech = null;
    }

}