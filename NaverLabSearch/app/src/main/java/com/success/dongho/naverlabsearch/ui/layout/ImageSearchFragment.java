package com.success.dongho.naverlabsearch.ui.layout;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.success.dongho.naverlabsearch.R;
import com.success.dongho.naverlabsearch.data.SearchRequestParam;
import com.success.dongho.naverlabsearch.data.SearchResult;
import com.success.dongho.naverlabsearch.data.SearchResult.SearchImageResult;
import com.success.dongho.naverlabsearch.net.SearchRequest;
import com.success.dongho.naverlabsearch.net.SearchRequestErrorListener;
import com.success.dongho.naverlabsearch.net.SearchRequestSpec;
import com.success.dongho.naverlabsearch.net.VolleyManager;
import com.success.dongho.naverlabsearch.ui.adapter.OnLoadMoreListener;
import com.success.dongho.naverlabsearch.ui.adapter.SearchImageAdapter;
import com.success.dongho.naverlabsearch.util.BuildMode;
import com.success.dongho.naverlabsearch.util.ViewUtils;

import static com.success.dongho.naverlabsearch.net.SearchRequestSpec.SEARCH_IMAGE_SORT_SIM;


public class ImageSearchFragment extends RecyclerViewFragment {
    private static final String TAG = WebSearchFragment.class.getSimpleName();
    private static final boolean DEBUG = BuildMode.ENABLE_DEBUG;

    private static final int RESULT_DISPLAY_MAX = 40;
    private static final int RESULT_START_INDEX = 1;

    private static final int GRID_SPAN_COUNT = 2;

    private Context mContext;

    private SearchImageAdapter mAdapter;

    private String mQueryText;
    private int mSearchStartIndex = 1;
    private SearchImageResult mImageData;

    private SearchRequest<?> mRequest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        String jsonString = ViewUtils.jsonStringReadToFile(mContext, ViewUtils.IMAGE_RESULT_NAME);

        if (TextUtils.isEmpty(jsonString)) {
            return;
        }

        mImageData = ViewUtils.jsonStringToObject(jsonString, SearchImageResult.class);

        if (mAdapter != null) {
            mAdapter.clear();
            mAdapter.addItems(mImageData);
            mSearchStartIndex = mImageData.start;
            mRecyclerView.scrollToPosition(mImageData.currentPos);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelRequest(mRequest);
        mContext.deleteFile(ViewUtils.IMAGE_RESULT_NAME);
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
        requestImageSearch();
        return true;
    }

    private void initView() {
        mContext = getActivity();
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, GRID_SPAN_COUNT);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAdapter.getItemViewType(position) == SearchImageAdapter.VIEW_TYPE_LOADING) {
                    return GRID_SPAN_COUNT;
                }

                return 1;
            }
        });

        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new SearchImageAdapter(mRecyclerView, mContext);

        mRecyclerView.setAdapter(mAdapter);

        OnLoadMoreListener listener = new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addLoadingView();
                        mSearchStartIndex += RESULT_DISPLAY_MAX;
                        requestImageSearch();
                    }
                });
            }
        };

        mAdapter.setLoadMoreListener(listener);
    }

    private void requestImageSearch() {
        if (TextUtils.isEmpty(mQueryText)) {
            return;
        }
        cancelRequest(mRequest);

        SearchRequestSpec spec = SearchRequestSpec.genGet(SearchImageResult.class);
        spec.param(SearchRequestParam.QUERY, mQueryText);
        spec.param(SearchRequestParam.DISPLAY, RESULT_DISPLAY_MAX);
        spec.param(SearchRequestParam.START, mSearchStartIndex);
        spec.param(SearchRequestParam.SORT, SEARCH_IMAGE_SORT_SIM);
        mRequest = new SearchRequest<SearchImageResult>(spec, new Response.Listener<SearchImageResult>() {
            @Override
            public void onResponse(SearchImageResult response) {
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
                response.query = mQueryText;
                mAdapter.addItems(response);
            }
        }, new SearchRequestErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error, SearchResult.SearchErrorResult result) {
                if (DEBUG) {
                    Log.e(TAG, "onErrorResponse()" + error + ", result=" + result);
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
        }, SearchImageResult.class);

        VolleyManager.instance().addQ(mRequest);
    }

    private void cancelRequest(SearchRequest<?> req) {
        if (req != null) {
            req.cancel();
            req = null;
        }
    }
}
