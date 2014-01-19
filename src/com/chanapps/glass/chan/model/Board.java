package com.chanapps.glass.chan.model;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;
import com.chanapps.glass.chan.util.JSONType;
import com.chanapps.glass.chan.util.JSONUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Board {

    private static final String TAG = Board.class.getSimpleName();

    public static final String URL_FORMAT = "https://a.4cdn.org/%1$s/0.json";
    public static final int INITIAL_CAPACITY = 20;
    public static final String THREADS_KEY = "threads";
    public static final String POSTS_KEY = "posts";
    public static final String BOARD_COLUMN = "board";
    public static final String NO_COLUMN = "no";
    public static final String SUB_COLUMN = "sub";
    public static final String COM_COLUMN = "com";
    public static final String TIM_COLUMN = "tim";
    public static final String REPLIES_COLUMN = "replies";
    public static final String IMAGES_COLUMN = "images";
    public static final String[] COLUMNS = {
            BOARD_COLUMN,
            NO_COLUMN,
            SUB_COLUMN,
            COM_COLUMN,
            TIM_COLUMN,
            REPLIES_COLUMN,
            IMAGES_COLUMN
    };
    public static final JSONType[] TYPES = {
            JSONType.STRING,
            JSONType.LONG,
            JSONType.OPT_STRING,
            JSONType.OPT_STRING,
            JSONType.OPT_LONG,
            JSONType.OPT_INTEGER,
            JSONType.OPT_INTEGER
    };

    public Cursor loadCursor(String board) {
        String url = String.format(URL_FORMAT, board);

        MatrixCursor cursor = new MatrixCursor(COLUMNS, INITIAL_CAPACITY);
        JSONObject rootObject = JSONUtils.readJson(url);
        if (rootObject == null)
            return cursor;
        try {
            JSONArray threadsArray = rootObject.getJSONArray(THREADS_KEY);
            //Log.e(TAG, "reading threadsArray=" + threadsArray);
            for (int i=0; i < threadsArray.length(); i++) {
                JSONObject threadObject = threadsArray.getJSONObject(i);
                //Log.e(TAG, "reading threadObject=" + threadObject);
                JSONArray postsArray = threadObject.getJSONArray(POSTS_KEY);
                //Log.e(TAG, "reading postsArray=" + postsArray);
                JSONObject firstPost = postsArray.getJSONObject(0);
                //Log.e(TAG, "reading firstPost=" + firstPost);
                Object[] row = new Object[COLUMNS.length];
                row[0] = board;
                for (int j = 1; j < COLUMNS.length; j++) {
                    String key = COLUMNS[j];
                    JSONType type = TYPES[j];
                    Object obj = JSONUtils.mapJson(firstPost, key, type);
                    row[j] = obj;
                }
                cursor.addRow(row);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception reading json: " + rootObject, e);
        }
        return cursor;
    }

}
