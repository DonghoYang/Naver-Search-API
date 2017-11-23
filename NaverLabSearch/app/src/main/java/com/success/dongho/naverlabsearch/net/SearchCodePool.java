package com.success.dongho.naverlabsearch.net;

import android.util.Log;

import com.success.dongho.naverlabsearch.data.SearchResult;
import com.success.dongho.naverlabsearch.util.BuildMode;

import java.util.concurrent.ConcurrentHashMap;


public final class SearchCodePool {
    private static final String TAG = SearchCodePool.class.getSimpleName();
    private static final boolean DEBUG = BuildMode.ENABLE_DEBUG;

    public static final String api(Class<?> o) {
        if (DEBUG) {
            Log.d(TAG, "api(" + o.getSimpleName() + ")=" + codePool.get(o));
        }
        return codePool.get(o);
    }

    private static final ConcurrentHashMap<Class<?>, String> codePool;

    static {
        codePool = new ConcurrentHashMap<Class<?>, String>();

        codePool.put(SearchResult.SearchWebResult.class, "webkr.json");
        codePool.put(SearchResult.SearchImageResult.class, "image");
    }

    private SearchCodePool() {
    }
}
