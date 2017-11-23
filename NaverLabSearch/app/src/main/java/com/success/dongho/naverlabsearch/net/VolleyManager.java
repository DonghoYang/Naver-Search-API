package com.success.dongho.naverlabsearch.net;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public final class VolleyManager {
    private static VolleyManager _volley;
    private static boolean _initialize;
    private final RequestQueue _requestQueue;
    private static Object _lock = new Object();

    public static void initialize(Context context) {
        
        if (_initialize)
            return;
        _volley = new VolleyManager(context);
        _initialize = true;
    }

    public static VolleyManager instance() {
        synchronized (_lock) {
            if (_volley == null) {
                throw new IllegalStateException("VolleyManager not initialized");
            }
            return _volley;
        }
    }

    private VolleyManager(Context context) {
        this._requestQueue = Volley.newRequestQueue(context);
    }

    public Request<?> addQ(Request<?> req) {
        return _requestQueue.add(req);
    }



}
