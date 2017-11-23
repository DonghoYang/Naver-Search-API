package com.success.dongho.naverlabsearch.ui.layout;


import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.success.dongho.naverlabsearch.MainActivity;
import com.success.dongho.naverlabsearch.R;
import com.success.dongho.naverlabsearch.util.BuildMode;

public class RecyclerViewFragment extends Fragment {
    private static final String TAG = WebSearchFragment.class.getSimpleName();
    private static final boolean DEBUG = BuildMode.ENABLE_DEBUG;

    protected View mParentView;
    protected RecyclerView mRecyclerView;

    private int mOrientation;
    private int mTopMargin;
    private int mLeftMargin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mParentView = inflater.inflate(R.layout.layout_fragment, container, false);
        mRecyclerView = mParentView.findViewById(R.id.fragment_recycler);
        Resources res = getResources();
        mTopMargin = res.getDimensionPixelOffset(R.dimen.tabbar_height);
        mLeftMargin = res.getDimensionPixelOffset(R.dimen.tabbar_width);
        return mParentView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig != null && mOrientation != newConfig.orientation) {
            mOrientation = newConfig.orientation;
            updateRecyclerViewLayoutParam(newConfig.orientation);
        }
    }

    private void updateRecyclerViewLayoutParam(int orientation) {
        if (mRecyclerView == null) {
            if (DEBUG) {
                Log.e(TAG, "updateRecyclerViewLayoutParam() Recyclerview is null!");
            }
            return;
        }

        boolean needUpdate = false;
        int leftMargin = 0;
        int topMargin = 0;

        switch (orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                needUpdate = true;
                topMargin = mTopMargin;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                needUpdate = true;
                leftMargin = mLeftMargin;
                break;
            default:
                break;
        }
        if (!needUpdate) {
            return;
        }

        ViewGroup.LayoutParams lp = mRecyclerView.getLayoutParams();
        if (lp instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) lp).leftMargin = leftMargin;
            ((FrameLayout.LayoutParams) lp).topMargin = topMargin;
        }

        mRecyclerView.setLayoutParams(lp);
    }

    public boolean requestSearchData(String query) {
        return true;
    }

    public boolean isShown() {
        Activity activity = getActivity();
        if (activity == null) {
            return false;
        }

        if (activity instanceof MainActivity) {
            RecyclerViewFragment fragment = ((MainActivity)activity).getCurrentFocusFragment();
            return (fragment != null && fragment == this);
        }

        return false;
    }
}
