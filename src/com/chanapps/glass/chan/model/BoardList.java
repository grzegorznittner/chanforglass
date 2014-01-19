package com.chanapps.glass.chan.model;

import android.database.Cursor;
import com.chanapps.glass.chan.util.JSONType;
import com.chanapps.glass.chan.util.JSONUtils;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 1/19/14
 * Time: 7:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class BoardList {
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
        return JSONUtils.loadCursorFromJson(URL, COLUMNS, TYPES, BOARDS_KEY, INITIAL_CAPACITY);
    }

}
