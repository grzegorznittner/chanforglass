package com.chanapps.glass.chan;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 1/18/14
 * Time: 9:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChanBoardListActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ChanBoardListActivity.class.getSimpleName();
    private static final String CONTENT_TYPE_HEADER = "Content-type";
    private static final String JSON_MIME_TYPE = "application/json";
    private static final String BOARD_LIST_URL = "https://a.4cdn.org/boards.json";
    private static final int LOADER_ID = 1;

    private CardScrollView mCardScrollView;
    private BoardListCardScrollAdapter mAdapter;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Card loadingCard = new Card(this);
        loadingCard.setText("Loading...");
        setContentView(loadingCard.toView());

        //createCards();
        mAdapter = new BoardListCardScrollAdapter();
        mCardScrollView = new CardScrollView(this);
        mCardScrollView.setAdapter(mAdapter);
        mCallbacks = this;
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_ID, null, mCallbacks);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID:
                return new BoardListCursorLoader(this);
        }
        return null;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_ID:
                // The asynchronous load is complete and the data
                // is now available for use. Only now can we associate
                // the queried Cursor with the SimpleCursorAdapter.
                mAdapter.swapCursor(data);
                mCardScrollView.activate();
                setContentView(mCardScrollView);
                break;
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private class BoardListCardScrollAdapter extends CardScrollAdapter {

        private Cursor mCursor;

        @Override
        public int findIdPosition(Object id) {
            if (mCursor == null)
                return -1;
            int i = 0;
            while (mCursor.moveToPosition(i)) {
                String board = mCursor.getString(mCursor.getColumnIndex(BOARD_COLUMN));
                if (board != null && board.equals(id))
                    return i;
                i++;
            }
            return -1;
        }

        @Override
        public int findItemPosition(Object item) {
            if (mCursor == null)
                return -1;
            int i = 0;
            while (mCursor.moveToPosition(i)) {
                Card card = getCard(i);
                if (card != null && card.equals(item))
                    return i;
                i++;
            }
            return -1;
        }

        @Override
        public int getCount() {
            if (mCursor == null)
                return 0;
            else
                return mCursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            return getCard(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (mCursor == null)
                return null;
            // ignore convertView
            Card card = getCard(position);
            if (card == null)
                return null;
            View view = card.toView();
            setItemOnCard(card, view);
            return view;
        }

        private Card getCard(int position) {
            if (mCursor == null)
                return null;
            if (!mCursor.moveToPosition(position))
                return null;
            String board = mCursor.getString(mCursor.getColumnIndex(BOARD_COLUMN));
            String title = mCursor.getString(mCursor.getColumnIndex(TITLE_COLUMN));
            //int ws_board = data.getInt(data.getColumnIndex(WS_BOARD_COLUMN));
            Card card = new Card(ChanBoardListActivity.this);
            card.setText("/" + board + "/");
            card.setFootnote(title);
            return card;
        }

        public void swapCursor(Cursor data) {
            mCursor = data;
        }

    }

    private static String BOARDS_KEY = "boards";
    private static String BOARD_COLUMN = "board";
    private static String TITLE_COLUMN = "title";
    private static String WS_BOARD_COLUMN = "ws_board";

    private static final int BOARD_LIST_CAPACITY = 70;
    private static final String[] BOARD_LIST_COLUMNS = {
        BOARD_COLUMN,
        TITLE_COLUMN,
        WS_BOARD_COLUMN
    };

    static public class BoardListCursorLoader extends CursorLoader {

        // We hold a reference to the Loader’s data here.
        private Cursor mData;

        public BoardListCursorLoader(Context ctx) {
            // Loaders may be used across multiple Activitys (assuming they aren't
            // bound to the LoaderManager), so NEVER hold a reference to the context
            // directly. Doing so will cause you to leak an entire Activity's context.
            // The superclass constructor will store a reference to the Application
            // Context instead, and can be retrieved with a call to getContext().
            super(ctx);
        }

        /****************************************************/
        /** (1) A task that performs the asynchronous load **/
        /****************************************************/

        @Override
        public Cursor loadInBackground() {
            // This method is called on a background thread and should generate a
            // new set of data to be delivered back to the client.
            // TODO: Perform the query here and add the results to 'data'.
            return parseJson();
        }

        private Cursor testCursor() {
            MatrixCursor cursor = new MatrixCursor(BOARD_LIST_COLUMNS, BOARD_LIST_CAPACITY);
            Object[] row = { "3", "3DGC", 1 };
            Object[] row1 = { "a", "Anime & Manga", 1 };
            Object[] row2 = { "adv", "Advice", 1 };
            cursor.addRow(row);
            cursor.addRow(row1);
            cursor.addRow(row2);
            return cursor;
        }

        private Cursor parseJson() {
            MatrixCursor cursor = new MatrixCursor(BOARD_LIST_COLUMNS);
            String json = readJson();
            try {
                JSONObject rootObject = new JSONObject(json);
                JSONArray jsonArray = rootObject.getJSONArray(BOARDS_KEY);
                for (int i=0; i < jsonArray.length(); i++) {
                    JSONObject oneObject = jsonArray.getJSONObject(i);
                    String board = oneObject.getString(BOARD_COLUMN);
                    String title = oneObject.getString(TITLE_COLUMN);
                    int wsBoard = oneObject.getInt(WS_BOARD_COLUMN);
                    Object[] row = { board, title, wsBoard };
                    cursor.addRow(row);
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception reading json: " + json, e);
            }
            return cursor;
        }

        private String readJson() {
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient(new BasicHttpParams());
            HttpGet httpGet = new HttpGet(BOARD_LIST_URL);
            httpGet.setHeader(CONTENT_TYPE_HEADER, JSON_MIME_TYPE);
            String line = "";
            try {
                HttpResponse response = client.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_NOT_MODIFIED) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } else {
                    Log.e(TAG, "Invalid status=" + statusCode + " getting url=" + BOARD_LIST_URL + "");
                }
            } catch (ClientProtocolException e) {
                Log.e(TAG, "ClientProtocolException reading json last line=" + line, e);
            } catch (IOException e) {
                Log.e(TAG, "IOException reading json last line=" + line, e);
            }
            return builder.toString();
        }

        /********************************************************/
        /** (2) Deliver the results to the registered listener **/
        /********************************************************/

        @Override
        public void deliverResult(Cursor data) {
            if (isReset()) {
                // The Loader has been reset; ignore the result and invalidate the data.
                releaseResources(data);
                return;
            }

            // Hold a reference to the old data so it doesn't get garbage collected.
            // We must protect it until the new data has been delivered.
            Cursor oldData = mData;
            mData = data;

            if (isStarted()) {
                // If the Loader is in a started state, deliver the results to the
                // client. The superclass method does this for us.
                super.deliverResult(data);
            }

            // Invalidate the old data as we don't need it any more.
            if (oldData != null && oldData != data) {
                releaseResources(oldData);
            }
        }

        /*********************************************************/
        /** (3) Implement the Loader’s state-dependent behavior **/
        /*********************************************************/

        @Override
        protected void onStartLoading() {
            if (mData != null) {
                // Deliver any previously loaded data immediately.
                deliverResult(mData);
            }

            // Begin monitoring the underlying data source.
            if (mObserver == null) {
                mObserver = new BoardListObserver();
                // TODO: register the observer
            }

            if (takeContentChanged() || mData == null) {
                // When the observer detects a change, it should call onContentChanged()
                // on the Loader, which will cause the next call to takeContentChanged()
                // to return true. If this is ever the case (or if the current data is
                // null), we force a new load.
                forceLoad();
            }
        }

        @Override
        protected void onStopLoading() {
            // The Loader is in a stopped state, so we should attempt to cancel the
            // current load (if there is one).
            cancelLoad();

            // Note that we leave the observer as is. Loaders in a stopped state
            // should still monitor the data source for changes so that the Loader
            // will know to force a new load if it is ever started again.
        }

        @Override
        protected void onReset() {
            // Ensure the loader has been stopped.
            onStopLoading();

            // At this point we can release the resources associated with 'mData'.
            if (mData != null) {
                releaseResources(mData);
                mData = null;
            }

            // The Loader is being reset, so we should stop monitoring for changes.
            if (mObserver != null) {
                // TODO: unregister the observer
                mObserver = null;
            }
        }

        @Override
        public void onCanceled(Cursor data) {
            // Attempt to cancel the current asynchronous load.
            super.onCanceled(data);

            // The load has been canceled, so we should release the resources
            // associated with 'data'.
            releaseResources(data);
        }

        private void releaseResources(Cursor data) {
            // For a simple List, there is nothing to do. For something like a Cursor, we
            // would close it in this method. All resources associated with the Loader
            // should be released here.
        }

        /*********************************************************************/
        /** (4) Observer which receives notifications when the data changes **/
        /*********************************************************************/

        // NOTE: Implementing an observer is outside the scope of this post (this example
        // uses a made-up "SampleObserver" to illustrate when/where the observer should
        // be initialized).

        // The observer could be anything so long as it is able to detect content changes
        // and report them to the loader with a call to onContentChanged(). For example,
        // if you were writing a Loader which loads a list of all installed applications
        // on the device, the observer could be a BroadcastReceiver that listens for the
        // ACTION_PACKAGE_ADDED intent, and calls onContentChanged() on the particular
        // Loader whenever the receiver detects that a new application has been installed.
        // Please don’t hesitate to leave a comment if you still find this confusing! :)
        private BoardListObserver mObserver;
    }

    public static class BoardListObserver {

    }

}