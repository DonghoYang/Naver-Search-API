package com.success.dongho.naverlabsearch.util;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.success.dongho.naverlabsearch.R;
import com.success.dongho.naverlabsearch.data.SearchResult;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.success.dongho.naverlabsearch.net.error.SearchErrorCode.CODE_AUTHENTICATION_FAIL;
import static com.success.dongho.naverlabsearch.net.error.SearchErrorCode.CODE_INCORRECT_QUERY_REQUEST;
import static com.success.dongho.naverlabsearch.net.error.SearchErrorCode.CODE_INVALID_DISPLAY_VALUE;
import static com.success.dongho.naverlabsearch.net.error.SearchErrorCode.CODE_INVALID_SEARCH_API;
import static com.success.dongho.naverlabsearch.net.error.SearchErrorCode.CODE_INVALID_SORT_VALUE;
import static com.success.dongho.naverlabsearch.net.error.SearchErrorCode.CODE_INVALID_START_VALUE;
import static com.success.dongho.naverlabsearch.net.error.SearchErrorCode.CODE_MALFORMED_ENCODING;
import static com.success.dongho.naverlabsearch.net.error.SearchErrorCode.CODE_SYSTEM_ERROR;

public class ViewUtils {
    private static final String TAG = ViewUtils.class.getSimpleName();
    private static final boolean DEBUG = BuildMode.ENABLE_DEBUG;

    public static final String IMAGE_RESULT_NAME = "imgSearchResult";

    public static void setHtmlText(TextView v, String text) {
        if (v == null || text == null) {
            return;
        }

        v.setText(Html.fromHtml(text));
    }

    public static void setTextViewTextColor(View v, int color) {
        if (v instanceof TextView) {
            ((TextView) v).setTextColor(color);
        }
    }

    public static void jsonStringWriteToFile(Context context, String jsonString, String fileName) {
        if (context == null) {
            return;
        }

        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(jsonString.getBytes());
            fos.close();

        } catch (IOException e) {
            if (DEBUG) {
                Log.e(TAG, "jsonStringWriteToFile() exception=" + e);
            }
        }
    }

    public static String jsonStringReadToFile(Context context, String fileName) {
        String jsonString = "";

        if (context == null) {
            return jsonString;
        }

        try {
            FileInputStream fis = context.openFileInput(fileName);
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fis));
            String temp = "";
            while ((temp = bufferReader.readLine()) != null) {
                jsonString += temp;
            }

            bufferReader.close();
            fis.close();

        } catch (Exception e) {

        }
        return jsonString;
    }

    public static void makeErrorToast(Context context, String code, String msg) {
        if (context == null || TextUtils.isEmpty(code)) {
            return;
        }
        String errorMessage = "";

        switch (code) {
            case CODE_AUTHENTICATION_FAIL:
                errorMessage = context.getString(R.string.authentication_failed);
                break;
            case CODE_INCORRECT_QUERY_REQUEST:
                errorMessage = context.getString(R.string.incorrect_query_request);
                break;
            case CODE_INVALID_DISPLAY_VALUE:
                errorMessage = context.getString(R.string.invalid_display_value);
                break;
            case CODE_INVALID_START_VALUE:
                errorMessage = context.getString(R.string.invalid_start_value);
                break;
            case CODE_INVALID_SORT_VALUE:
                errorMessage = context.getString(R.string.invalid_sort_value);
                break;
            case CODE_MALFORMED_ENCODING:
                errorMessage = context.getString(R.string.malformed_encoding);
                break;
            case CODE_INVALID_SEARCH_API:
                errorMessage = context.getString(R.string.invalid_search_api);
                break;
            case CODE_SYSTEM_ERROR:
                errorMessage = context.getString(R.string.system_error);
                break;
        }

        if (TextUtils.isEmpty(errorMessage)) {
            return;
        }

        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
    }

    public static <T> T jsonStringToObject(String jsonString, Class<T> type) {
        if (TextUtils.isEmpty(jsonString)) {
            if (DEBUG) {
                Log.e(TAG, "jsonStringToObject() jsonString is null!");
            }
            return null;
        }
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return  gson.fromJson(jsonString, type);
    }
}
