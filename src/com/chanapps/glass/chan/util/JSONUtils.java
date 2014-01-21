package com.chanapps.glass.chan.util;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
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

public class JSONUtils {

    private static final String TAG = JSONUtils.class.getSimpleName();
    private static final String CONTENT_TYPE_HEADER = "Content-type";
    private static final String JSON_MIME_TYPE = "application/json";

    public static Cursor loadCursorFromJson(String url, String[] columns, JSONType[] types, String rootKey, int initialCapacity) {
        MatrixCursor cursor = new MatrixCursor(columns, initialCapacity);
        JSONObject rootObject;
        try {
            rootObject = JSONUtils.readJsonObject(url);
            if (rootObject == null)
                return cursor;
            JSONArray jsonArray = rootObject.getJSONArray(rootKey);
            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject oneObject = jsonArray.getJSONObject(i);
                Object[] row = new Object[columns.length];
                for (int j = 0; j < columns.length; j++) {
                    String key = columns[j];
                    JSONType type = types[j];
                    Object obj = mapJson(oneObject, key, type);
                    row[j] = obj;
                }
                cursor.addRow(row);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception reading json", e);
        }
        return cursor;
    }

    public static Object mapJson(JSONObject oneObject, String key, JSONType type) {
        try {
            switch (type) {
                default:
                case STRING:
                    return oneObject.getString(key);
                case INTEGER:
                    return oneObject.getInt(key);
                case LONG:
                    return oneObject.getLong(key);
                case BOOLEAN:
                    return oneObject.getBoolean(key);
                case OPT_STRING:
                    return oneObject.optString(key);
                case OPT_INTEGER:
                    return oneObject.optInt(key);
                case OPT_LONG:
                    return oneObject.optLong(key);
                case OPT_BOOLEAN:
                    return oneObject.optBoolean(key);
            }
        }
        catch (JSONException e) {
            Log.e(TAG, "JSONException parsing object=" + oneObject + " key=" + key + " type=" + type, e);
            return null;
        }
    }

    public static JSONObject readJsonObject(String url) {
        JSONObject jsonObject = null;
        try {
            String json = readJson(url);
            if (json != null && !json.isEmpty())
                jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException reading json", e);
        }
        return jsonObject;
    }

    public static JSONArray readJsonArray(String url) {
        JSONArray jsonArray = null;
        try {
            String json = readJson(url);
            if (json != null && !json.isEmpty())
                jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException reading json", e);
        }
        return jsonArray;
    }

    private static String readJson(String url) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient(new BasicHttpParams());
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader(CONTENT_TYPE_HEADER, JSON_MIME_TYPE);
        String line = "";
        String json = null;
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_NOT_MODIFIED) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                while ((line = reader.readLine()) != null)
                    builder.append(line);
                json = builder.toString();
            } else {
                Log.e(TAG, "Invalid status=" + statusCode + " getting url=" + url + "");
            }
        } catch (ClientProtocolException e) {
            Log.e(TAG, "ClientProtocolException reading json last line=" + line, e);
        } catch (IOException e) {
            Log.e(TAG, "IOException reading json last line=" + line, e);
        }
        return json;
    }

}
