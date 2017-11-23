package com.success.dongho.naverlabsearch.ui.layout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

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
import com.success.dongho.naverlabsearch.ui.adapter.ImagePagerAdapter;
import com.success.dongho.naverlabsearch.util.BuildMode;
import com.success.dongho.naverlabsearch.util.ViewUtils;

import static com.success.dongho.naverlabsearch.net.SearchRequestSpec.SEARCH_IMAGE_SORT_SIM;

public class ImageDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = ImageDetailActivity.class.getSimpleName();
    private static final boolean DEBUG = BuildMode.ENABLE_DEBUG;

    private static final int MOVE_TYPE_LEFT     = 0;
    private static final int MOVE_TYPE_RIGHT    = 1;

    private Context mContext;
    private ViewPager mViewPager;
    private ImagePagerAdapter mPagerAdapter;
    private View mLeftBtn;
    private View mRightBtn;

    private String mFileName;
    private String mQueryText;
    private int mCurrentIndex;
    private SearchImageResult mImageData;

    private SearchRequest<?> mRequest;

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mCurrentIndex = position;
            mImageData.currentPos = position;
            handleLeftRightButton(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mContext = this;

        handleIntent();

        initView();

        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveData();
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();

        switch (id) {
            case R.id.left_icon:
                moveViewPagerItem(MOVE_TYPE_LEFT);
                break;
            case R.id.right_icon:
                moveViewPagerItem(MOVE_TYPE_RIGHT);
                break;
            default:
                break;
        }
    }

    private boolean moveViewPagerItem(int type) {
        int currentIndex = mViewPager.getCurrentItem();
        int total = mPagerAdapter.getCount();
        if (type == MOVE_TYPE_LEFT) {
            if (currentIndex == 0) return false;

            mViewPager.setCurrentItem(--currentIndex);
        } else if (type == MOVE_TYPE_RIGHT) {
            if (currentIndex == total - 1) {
                moreImageLoad();
                return false;
            }
            mViewPager.setCurrentItem(++currentIndex);
        } else {
            return false;
        }

        return true;
    }

    private void initView() {
        mViewPager = findViewById(R.id.pager);
        mPagerAdapter = new ImagePagerAdapter(mContext);
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.addOnPageChangeListener(mPageChangeListener);
        mLeftBtn = findViewById(R.id.left_icon);
        mLeftBtn.setOnClickListener(this);
        mRightBtn = findViewById(R.id.right_icon);
        mRightBtn.setOnClickListener(this);

    }

    private void handleIntent() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            mFileName = bundle.getString(SearchRequestParam.FILE_NAME);
            mCurrentIndex = bundle.getInt(SearchRequestParam.INDEX);
            mQueryText = bundle.getString(SearchRequestParam.QUERY);
        }
        if (DEBUG) {
            Log.d(TAG, "handleIntent() fileName=" + mFileName
                    + ", currentPos=" + mCurrentIndex + ", mQueryText=" + mQueryText);
        }
    }

    private void initData() {
        String jsonString = ViewUtils.jsonStringReadToFile(mContext, mFileName);

        mImageData = ViewUtils.jsonStringToObject(jsonString, SearchImageResult.class);

        if (mPagerAdapter != null) {
            mPagerAdapter.updateItem(mImageData);
        }

        if (mViewPager != null) {
            mViewPager.setCurrentItem(mCurrentIndex);
        }

        handleLeftRightButton(mCurrentIndex);
    }

    private void saveData() {
        deleteFile(ViewUtils.IMAGE_RESULT_NAME);
        ViewUtils.jsonStringWriteToFile(mContext, mImageData.toString(), ViewUtils.IMAGE_RESULT_NAME);
    }

    private void handleLeftRightButton(int position) {
        final int total = mPagerAdapter.getCount();

        if (position == 0) {
            mLeftBtn.setVisibility(View.GONE);
            mRightBtn.setVisibility(View.VISIBLE);
        } else if (position == (total - 1)) {
            moreImageLoad();
            mLeftBtn.setVisibility(View.VISIBLE);
            mRightBtn.setVisibility(View.GONE);
        } else {
            mLeftBtn.setVisibility(View.VISIBLE);
            mRightBtn.setVisibility(View.VISIBLE);
        }
    }

    private void moreImageLoad() {
        if (mImageData == null) {
            return;
        }

        if (mImageData.total == mPagerAdapter.getCount()) {
            if (DEBUG) {
                Log.w(TAG, "no more images to load!");
            }
            return;
        }

        if (TextUtils.isEmpty(mQueryText)) {
            return;
        }
        cancelRequest(mRequest);

        SearchRequestSpec spec = SearchRequestSpec.genGet(SearchImageResult.class);
        spec.param(SearchRequestParam.QUERY, mQueryText);
        spec.param(SearchRequestParam.DISPLAY, mImageData.display);
        spec.param(SearchRequestParam.START, mPagerAdapter.getCount() + 1);
        spec.param(SearchRequestParam.SORT, SEARCH_IMAGE_SORT_SIM);
        mRequest = new SearchRequest<SearchImageResult>(spec, new Response.Listener<SearchImageResult>() {
            @Override
            public void onResponse(SearchImageResult response) {
                if (DEBUG) {
                    Log.d(TAG, "onResponse() response=" + response);
                }
                if (response == null) {
                    return;
                }
                mImageData.total = response.total;
                mImageData.start = response.start;
                if (response.items != null) {
                    mImageData.items.addAll(response.items);
                }
                mPagerAdapter.addItems(mImageData);
                handleLeftRightButton(mCurrentIndex);
            }
        }, new SearchRequestErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error, SearchResult.SearchErrorResult result) {
                if (DEBUG) {
                    Log.e(TAG, "onErrorResponse() " + error + ", result=" + result);
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
