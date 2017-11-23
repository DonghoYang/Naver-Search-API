package com.success.dongho.naverlabsearch.net;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.success.dongho.naverlabsearch.data.SearchResult.SearchErrorResult;


public abstract class SearchRequestErrorListener implements Response.ErrorListener{
    public abstract void onErrorResponse(VolleyError error, SearchErrorResult result);
    @Override
    public final void onErrorResponse(VolleyError error) {}
}
