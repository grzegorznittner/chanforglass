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

    public static final String PAGE_URL_FORMAT = "https://a.4cdn.org/%1$s/0.json";
    public static final String CATALOG_URL_FORMAT = "https://a.4cdn.org/%1$s/catalog.json";
    public static final String THUMBNAIL_FORMAT = "https://t.4cdn.org/%1$s/thumb/%2$ds.jpg";
    public static final int INITIAL_PAGE_CAPACITY = 20;
    public static final int INITIAL_CATALOG_CAPACITY = 170;
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

    public Cursor loadPageCursor(String board) {
        String url = String.format(PAGE_URL_FORMAT, board);

        MatrixCursor cursor = new MatrixCursor(COLUMNS, INITIAL_PAGE_CAPACITY);
        JSONObject rootObject = null;
        try {
            rootObject = JSONUtils.readJsonObject(url);
            if (rootObject == null)
                return cursor;
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

    public Cursor loadCatalogCursor(String board) {
        String url = String.format(CATALOG_URL_FORMAT, board);
        MatrixCursor cursor = new MatrixCursor(COLUMNS, INITIAL_CATALOG_CAPACITY);
        JSONArray rootArray = null;
        try {
            rootArray = JSONUtils.readJsonArray(url);
            if (rootArray == null)
                return cursor;
            for (int i = 0; i < rootArray.length(); i++) {
                JSONObject pageObject = rootArray.getJSONObject(i);
                JSONArray threadsArray = pageObject.getJSONArray(THREADS_KEY);
                for (int j=0; j < threadsArray.length(); j++) {
                    JSONObject threadObject = threadsArray.getJSONObject(j);
                    Object[] row = new Object[COLUMNS.length];
                    row[0] = board;
                    for (int k = 1; k < COLUMNS.length; k++) {
                        String key = COLUMNS[k];
                        JSONType type = TYPES[k];
                        Object obj = JSONUtils.mapJson(threadObject, key, type);
                        row[k] = obj;
                    }
                    cursor.addRow(row);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception reading json: " + rootArray, e);
        }
        return cursor;
    }

}
