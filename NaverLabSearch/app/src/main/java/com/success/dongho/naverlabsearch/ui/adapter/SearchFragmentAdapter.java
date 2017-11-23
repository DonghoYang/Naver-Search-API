package com.success.dongho.naverlabsearch.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.SparseArray;

import com.success.dongho.naverlabsearch.ui.layout.ImageSearchFragment;
import com.success.dongho.naverlabsearch.ui.layout.RecyclerViewFragment;
import com.success.dongho.naverlabsearch.ui.layout.WebSearchFragment;
import com.success.dongho.naverlabsearch.util.BuildMode;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


public class SearchFragmentAdapter extends FragmentPagerAdapter {
    private static final String TAG = SearchFragmentAdapter.class.getSimpleName();
    private static final boolean DEBUG = BuildMode.ENABLE_DEBUG;

    public static final String WEB_SEARCH = "web_search";
    public static final String IMAGE_SEARCH = "image_search";

    private static final ConcurrentHashMap<String, Class<? extends RecyclerViewFragment>> codePool;
    static {
        codePool = new ConcurrentHashMap<String, Class<? extends RecyclerViewFragment>>();

        codePool.put(WEB_SEARCH, WebSearchFragment.class);
        codePool.put(IMAGE_SEARCH, ImageSearchFragment.class);
    }

    private ArrayList<String> mData = new ArrayList<>();
    final SparseArray<Fragment> mTabFragments = new SparseArray<>();

    public SearchFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = mTabFragments.get(position);
        if (fragment == null) {
            fragment = getFragment(position);
            mTabFragments.put(position, fragment);
        }
        return fragment;
    }

    private Fragment getFragment(int index) {
        RecyclerViewFragment fragment = null;

        Class<? extends RecyclerViewFragment> fragmentType = getType(mData.get(index));
        if (fragmentType == null) {
            return null;
        }
        try {
            fragment = fragmentType.newInstance();
            Bundle args = new Bundle();

            fragment.setArguments(args);
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "getFragment() exception:" + e);
            }
        }
        return fragment;
    }

    public static final Class<? extends RecyclerViewFragment> getType(final String code) {
        if (codePool.containsKey(code)) {
            return codePool.get(code);
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public void updateData(ArrayList<String> data) {
        mData.clear();
        mData.addAll(data);
    }
}
