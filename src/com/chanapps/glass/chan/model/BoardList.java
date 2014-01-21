package com.chanapps.glass.chan.model;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;
import com.chanapps.glass.chan.util.AlphanumComparator;
import com.chanapps.glass.chan.util.JSONType;
import com.chanapps.glass.chan.util.JSONUtils;
import com.chanapps.glass.chan.util.Stringy;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 1/19/14
 * Time: 7:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class BoardList {

    private static final String TAG = BoardList.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static final String URL = "https://a.4cdn.org/boards.json";
    public static final int INITIAL_CAPACITY = 70;
    public static final String BOARDS_KEY = "boards";
    public static final String BOARD_COLUMN = "board";
    public static final String TITLE_COLUMN = "title";
    private static final String WS_BOARD_COLUMN = "ws_board";
    public static final String[] COLUMNS = {
            BOARD_COLUMN,
            TITLE_COLUMN,
            WS_BOARD_COLUMN
    };
    public static final JSONType[] TYPES = {
            JSONType.STRING,
            JSONType.STRING,
            JSONType.INTEGER
    };

    public Cursor loadCursor() {
        List<BoardRow> rows = loadListFromJson(URL, COLUMNS, TYPES, BOARDS_KEY, INITIAL_CAPACITY);
        if (DEBUG) Log.i(TAG, "first row:" + rows.get(0));
        Collections.sort(rows, new AlphanumComparator<BoardRow>());
        if (DEBUG) Log.i(TAG, "first row after sort:" + rows.get(0));
        MatrixCursor cursor =  new MatrixCursor(COLUMNS, rows.size());
        for (BoardRow row : rows)
            cursor.addRow(row.mRow);
        return cursor;
    }

    public static List<BoardRow> loadListFromJson(String url, String[] columns, JSONType[] types, String rootKey, int initialCapacity) {
        List<BoardRow> rows = new ArrayList<BoardRow>(initialCapacity);
        JSONObject rootObject;
        try {
            rootObject = JSONUtils.readJsonObject(url);
            if (rootObject == null)
                return rows;
            JSONArray jsonArray = rootObject.getJSONArray(rootKey);
            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject oneObject = jsonArray.getJSONObject(i);
                Object[] row = new Object[columns.length];
                for (int j = 0; j < columns.length; j++) {
                    String key = columns[j];
                    JSONType type = types[j];
                    Object obj = JSONUtils.mapJson(oneObject, key, type);
                    row[j] = obj;
                }
                rows.add(new BoardRow(row));
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception reading json", e);
        }
        return rows;
    }

    static public class BoardRow implements Stringy {
        public BoardRow(Object[] row) {
            mRow = row;
        }
        public Object[] mRow;
        @Override
        public String toString() {
            if (mRow != null && mRow.length > 0 && mRow[0] != null && mRow[0] instanceof String)
                return (String)mRow[0];
            else
                return "";
        }
    }

}
