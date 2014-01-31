package com.chanapps.glass.chan;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import com.chanapps.glass.chan.model.BoardList;
import com.chanapps.glass.chan.model.CursorLoadCallback;
import com.chanapps.glass.chan.util.CardCursorScrollAdapter;
import com.chanapps.glass.chan.util.JSONLoaderCallbacks;
import com.chanapps.glass.chan.view.BoardListView;
import com.chanapps.glass.chan.view.SimulatedScrollBar;
import com.google.android.glass.widget.CardScrollView;

public class ChanBoardListActivity extends Activity {

    private static final String TAG = ChanBoardListActivity.class.getSimpleName();
    private static final int LOADER_ID = 1;
    private static final String SHOW_NSFW = "showNSFW";

    private TextToSpeech mSpeech;
    private ProgressBar mProgressBar;
    private CardScrollView mCardScrollView;
    private SimulatedScrollBar mSimulatedScrollBar;
    private CardCursorScrollAdapter mAdapter;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    private int mCurrentPosition = -1;
    private boolean mShowNSFW = false;
    private BoardListView mBoardListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // Do nothing.
            }
        });

        mShowNSFW = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(SHOW_NSFW, false);

        mBoardListView = new BoardListView(this);

        View rootLayout = getLayoutInflater().inflate(R.layout.card_scroll_layout, null);
        mProgressBar = (ProgressBar)rootLayout.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);
        setContentView(rootLayout);

        mAdapter = new CardCursorScrollAdapter();
        mAdapter.setIdColumn(BoardList.BOARD_COLUMN);
        mAdapter.setNewCardCallback(mBoardListView.newCardCallback());

        mSimulatedScrollBar = (SimulatedScrollBar)rootLayout.findViewById(R.id.simulated_scroll_bar);

        mCardScrollView = (CardScrollView)rootLayout.findViewById(R.id.card_scroll_view);
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
                        return new BoardList().loadCursor(mShowNSFW);
                    }
                }, LOADER_ID);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_ID, null, mCallbacks);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.board_list_menu, menu);
        menu.findItem(R.id.show_nsfw).setVisible(!mShowNSFW);
        menu.findItem(R.id.hide_nsfw).setVisible(mShowNSFW);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Cursor cursor;
        switch (item.getItemId()) {
            case R.id.view_board:
                cursor = mAdapter.getCursor();
                if (cursor == null || !cursor.moveToPosition(mCurrentPosition))
                    return true;
                String board = cursor.getString(cursor.getColumnIndex(BoardList.BOARD_COLUMN));
                Intent intent = new Intent(ChanBoardListActivity.this, ChanBoardActivity.class);
                intent.putExtra(BoardList.BOARD_COLUMN, board);
                startActivity(intent);
                return true;
            case R.id.read_aloud:
                cursor = mAdapter.getCursor();
                if (cursor == null || !cursor.moveToPosition(mCurrentPosition))
                    return true;
                String text = cursor.getString(cursor.getColumnIndex(BoardList.TITLE_COLUMN));
                mSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                return true;
            case R.id.show_nsfw:
                mShowNSFW = true;
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(SHOW_NSFW, mShowNSFW).apply();
                getLoaderManager().restartLoader(LOADER_ID, null, mCallbacks);
                recreate();
                return true;
            case R.id.hide_nsfw:
                mShowNSFW = false;
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(SHOW_NSFW, mShowNSFW).apply();
                getLoaderManager().restartLoader(LOADER_ID, null, mCallbacks);
                recreate();
                return true;
            case R.id.about:
                Uri uri = Uri.parse(getString(R.string.about_url));
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
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
        if (mSpeech != null)
            mSpeech.shutdown();
        mSpeech = null;
    }

}