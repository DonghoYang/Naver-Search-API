package com.success.dongho.naverlabsearch.ui.layout;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.success.dongho.naverlabsearch.R;
import com.success.dongho.naverlabsearch.data.SearchRequestParam;
import com.success.dongho.naverlabsearch.data.SearchResult;
import com.success.dongho.naverlabsearch.net.SearchRequest;
import com.success.dongho.naverlabsearch.net.SearchRequestErrorListener;
import com.success.dongho.naverlabsearch.net.SearchRequestSpec;
import com.success.dongho.naverlabsearch.net.VolleyManager;
import com.success.dongho.naverlabsearch.ui.adapter.OnLoadMoreListener;
import com.success.dongho.naverlabsearch.ui.adapter.SearchWebAdapter;
import com.success.dongho.naverlabsearch.util.BuildMode;
import com.success.dongho.naverlabsearch.util.ViewUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.success.dongho.naverlabsearch.data.SearchResult.SearchWebResult;
import static com.success.dongho.naverlabsearch.net.SearchRequestSpec.SEARCH_IMAGE_SORT_SIM;

public class WebSearchFragment extends RecyclerViewFragment {
    private static final String TAG = WebSearchFragment.class.getSimpleName();
    private static final boolean DEBUG = BuildMode.ENABLE_DEBUG;

    private static final int RESULT_DISPLAY_MAX = 20;
    private static final int RESULT_START_INDEX = 1;

    private Context mContext;

    private SearchWebAdapter mAdapter;

    private String mQueryText;
    private int mSearchStartIndex = 1;

    private SearchRequest<?> mRequest;

    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelRequest(mRequest);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    @Override
    public boolean requestSearchData(String query) {
        mAdapter.clear();
        mQueryText = query;
        mSearchStartIndex = RESULT_START_INDEX;
        requestWebSearch();
        return true;
    }

    private void initView() {
        mContext = getActivity();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mAdapter = new SearchWebAdapter(mRecyclerView, mContext);

        mRecyclerView.setAdapter(mAdapter);

        OnLoadMoreListener listener = new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addLoadingView();
                        mSearchStartIndex += RESULT_DISPLAY_MAX;
                        requestWebSearch();
                    }
                });
            }
        };

        mAdapter.setLoadMoreListener(listener);
    }

    private void requestWebSearch() {
        if (TextUtils.isEmpty(mQueryText)) {
            return;
        }
        cancelRequest(mRequest);

        SearchRequestSpec spec = SearchRequestSpec.genGet(SearchWebResult.class);
        spec.param(SearchRequestParam.QUERY, mQueryText);
        spec.param(SearchRequestParam.DISPLAY, RESULT_DISPLAY_MAX);
        spec.param(SearchRequestParam.START, mSearchStartIndex);
        spec.param(SearchRequestParam.SORT, SEARCH_IMAGE_SORT_SIM);
        mRequest = new SearchRequest<SearchResult.SearchWebResult>(spec, new Response.Listener<SearchResult.SearchWebResult>() {
            @Override
            public void onResponse(SearchWebResult response) {
                if (DEBUG) {
                    Log.d(TAG, "onResponse() response = " + response);
                }
                if (response == null) {
                    return;
                }

                if (response.total == 0 && isShown()) {
                    Toast.makeText(mContext, R.string.search_warring, Toast.LENGTH_SHORT).show();
                }
                if (mAdapter.isMoreLoading()) {
                    mAdapter.removeLoadingView();
                    mAdapter.setFinishMoreLoading();
                }
                mAdapter.addItems(response);
            }
        },new SearchRequestErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error, SearchResult.SearchErrorResult result) {
                if (DEBUG) {
                    Log.e(TAG, "onErrorResponse()" + error + ", result = " +result);
                }

                if (mAdapter.isMoreLoading()) {
                    mAdapter.removeLoadingView();
                    mAdapter.setFinishMoreLoading();
                }
                if (result == null) {
                    return;
                }
                ViewUtils.makeErrorToast(mContext, result.errorCode, result.errorMessage);
            }
        }, SearchWebResult.class);

        VolleyManager.instance().addQ(mRequest);
    }

    private void cancelRequest(SearchRequest<?> req) {
        if (req != null) {
            req.cancel();
            req = null;
        }
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
}
