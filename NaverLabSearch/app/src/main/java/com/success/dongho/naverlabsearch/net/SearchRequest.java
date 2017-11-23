
package com.success.dongho.naverlabsearch.net;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.success.dongho.naverlabsearch.data.SearchResult;
import com.success.dongho.naverlabsearch.util.BuildMode;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SearchRequest<T> extends Request<T> {
    private static final String TAG = SearchRequest.class.getSimpleName();
    private static final boolean DEBUG = BuildMode.ENABLE_DEBUG;

    private static final String DEFAULT_ENCODING = "utf-8";

    private final Listener<T> mListener;
    private final Class<T> mType;
    private String mParamEncoding;
    private final Map<String, String> mParams;
    private final Map<String, String> mExtensionHeaders;

    private SearchRequestErrorListener mErrorListener;

    public SearchRequest(SearchRequestSpec spec, Listener<T> listener,
                         SearchRequestErrorListener errorListener, Class<T> type) {

        this(spec.method(), spec.buildUri(), listener, errorListener,
                type, spec.params(), spec.customHeaders());
    }

    public SearchRequest(int method, String url, Listener<T> listener,
                         SearchRequestErrorListener errorListener,
                         Class<T> type, Map<String, String> params, Map<String, String> headers) {
        super(method, url, errorListener);

        mListener = listener;
        mType = type;
        mParamEncoding = DEFAULT_ENCODING;
        mErrorListener = errorListener;

        if (params == null || params.isEmpty()) {
            mParams = new HashMap<String, String>();
        } else {
            mParams = params;
        }

        mExtensionHeaders = new HashMap<String, String>();
        if (headers != null && !headers.isEmpty()) {
            mExtensionHeaders.putAll(headers);
        }

        setRetryPolicy(new DefaultRetryPolicy(
                10000,
                10,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {

        InputStream stream = new ByteArrayInputStream(response.data);
        String jsonString = convertString(stream);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        T result = gson.fromJson(jsonString, mType);

        return Response.success(result,
                HttpHeaderParser.parseCacheHeaders(response));
    }

    private final String convertString(InputStream in) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));
            StringBuffer out = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }

            reader.close();
            if (DEBUG) {
                Log.e("parser", out.toString());
            }

            return out.toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        if (DEBUG) {
            Log.e(TAG, "parseNetworkError() >>");
            Log.e(TAG, " error:" + volleyError);
            if (volleyError.networkResponse != null) {
                Log.e(TAG, "http code: " + volleyError.networkResponse.statusCode);
                Log.e(TAG, " data:" + volleyError.networkResponse);
            }
        }
        return super.parseNetworkError(volleyError);
    }

    @Override
    public void deliverError(VolleyError error) {
        handleError(error);
    }

    @Override
    protected String getParamsEncoding() {
        return mParamEncoding;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if (DEBUG) {
            Log.e(TAG, "getParams()");
            Log.e(TAG, ">>> " + mParams.toString());
        }
        return mParams;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }

        headers.putAll(mExtensionHeaders);
        if (DEBUG) {
            Log.e(TAG, "getHeaders() >>> " + headers.toString());
        }

        return headers;
    }

    private void handleError(VolleyError error) {

        if (error.networkResponse == null || error.networkResponse.data == null) {
            mErrorListener.onErrorResponse(error, null);
            return;
        }
        InputStream stream = new ByteArrayInputStream(error.networkResponse.data);
        String jsonString = convertString(stream);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        SearchResult.SearchErrorResult result = gson.fromJson(jsonString, SearchResult.SearchErrorResult.class);

        mErrorListener.onErrorResponse(error, result);
    }
}
