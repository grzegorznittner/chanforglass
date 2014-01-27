package com.chanapps.glass.chan.model;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;
import com.chanapps.glass.chan.util.JSONType;
import com.chanapps.glass.chan.util.JSONUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChanThread {

    private static final String TAG = ChanThread.class.getSimpleName();

    public static final String THREAD_URL_FORMAT = "https://a.4cdn.org/%1$s/res/%2$d.json";
    public static final String THUMBNAIL_FORMAT = "https://t.4cdn.org/%1$s/thumb/%2$ds.jpg";
    public static final int INITIAL_PAGE_CAPACITY = 20;
    public static final String POSTS_KEY = "posts";
    public static final String BOARD_COLUMN = "board";
    public static final String NO_COLUMN = "no";
    public static final String RESTO_COLUMN = "resto";
    public static final String SUB_COLUMN = "sub";
    public static final String COM_COLUMN = "com";
    public static final String TIM_COLUMN = "tim";
    public static final String REPLIES_COLUMN = "replies";
    public static final String IMAGES_COLUMN = "images";
    public static final String FILENAME_COLUMN = "filename";
    public static final String EXT_COLUMN = "ext";
    public static final String FSIZE_COLUMN = "fsize";
    public static final String[] COLUMNS = {
            BOARD_COLUMN,
            NO_COLUMN,
            RESTO_COLUMN,
            SUB_COLUMN,
            COM_COLUMN,
            TIM_COLUMN,
            REPLIES_COLUMN,
            IMAGES_COLUMN,
            FILENAME_COLUMN,
            EXT_COLUMN,
            FSIZE_COLUMN
    };
    public static final JSONType[] TYPES = {
            JSONType.STRING,
            JSONType.LONG,
            JSONType.LONG,
            JSONType.OPT_STRING,
            JSONType.OPT_STRING,
            JSONType.OPT_LONG,
            JSONType.OPT_LONG,
            JSONType.OPT_LONG,
            JSONType.OPT_STRING,
            JSONType.OPT_STRING,
            JSONType.OPT_LONG
    };

    public Cursor loadCursor(String board, long no) {
        String url = String.format(THREAD_URL_FORMAT, board, no);

        MatrixCursor cursor = new MatrixCursor(COLUMNS, INITIAL_PAGE_CAPACITY);
        JSONObject rootObject = null;
        try {
            rootObject = JSONUtils.readJsonObject(url);
            if (rootObject == null)
                return cursor;
            JSONArray postsArray = rootObject.getJSONArray(POSTS_KEY);
            for (int i=0; i < postsArray.length(); i++) {
                JSONObject postObject = postsArray.getJSONObject(i);
                Object[] row = new Object[COLUMNS.length];
                row[0] = board;
                for (int j = 1; j < COLUMNS.length; j++) {
                    String key = COLUMNS[j];
                    JSONType type = TYPES[j];
                    Object obj = JSONUtils.mapJson(postObject, key, type);
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
