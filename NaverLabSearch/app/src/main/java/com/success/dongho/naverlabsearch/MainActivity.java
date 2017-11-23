package com.success.dongho.naverlabsearch;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.success.dongho.naverlabsearch.ui.adapter.SearchFragmentAdapter;
import com.success.dongho.naverlabsearch.ui.layout.RecyclerViewFragment;
import com.success.dongho.naverlabsearch.util.BuildMode;
import com.success.dongho.naverlabsearch.util.ViewUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final boolean DEBUG = BuildMode.ENABLE_DEBUG;

    private static final int INVALID = -1;

    private static final int TAB_WEB = 0;
    private static final int TAB_IMAGE = 1;

    private ViewPager mViewPager;
    private SearchFragmentAdapter mSearchAdapter;

    private LinearLayout mHorizontalTabBar;
    private LinearLayout mVerticalTabBar;

    private TextView mTabLeft;
    private TextView mTabRight;
    private TextView mTabUp;
    private TextView mTabDown;

    private EditText mSearchEdit;

    private ArrayList<View> mHorizontalTabView = new ArrayList<View>();
    private ArrayList<View> mVerticalTabView = new ArrayList<View>();

    private int mTabNorColor;
    private int mTabSelColor;

    private int mCurrentTab = INVALID;

    private View.OnClickListener mTabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int selIndex = (int)view.getTag();
            updateViewTextColor(mHorizontalTabView, selIndex);
            updateViewTextColor(mVerticalTabView, selIndex);

            mViewPager.setCurrentItem(selIndex);
        }
    };

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mCurrentTab = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                handleSearch(textView.getText().toString());

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        deleteFile(ViewUtils.IMAGE_RESULT_NAME);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mHorizontalTabBar.setVisibility(View.VISIBLE);
            mVerticalTabBar.setVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            mVerticalTabBar.setVisibility(View.VISIBLE);
            mHorizontalTabBar.setVisibility(View.GONE);
        } else {

        }
    }

    public RecyclerViewFragment getCurrentFocusFragment() {

        if (mCurrentTab == INVALID
                ||mSearchAdapter == null || mSearchAdapter.getCount() == 0) {
            return null;
        }

        RecyclerViewFragment fragment = (RecyclerViewFragment) mSearchAdapter.getItem(mCurrentTab);
        if (fragment == null) {
            return null;
        }

        return fragment;
    }

    private void initView() {
        mViewPager = findViewById(R.id.main_viewpager);
        mSearchAdapter = new SearchFragmentAdapter(getSupportFragmentManager());
        ArrayList<String> data = new ArrayList<>();
        data.add(SearchFragmentAdapter.WEB_SEARCH);
        data.add(SearchFragmentAdapter.IMAGE_SEARCH);
        mSearchAdapter.updateData(data);

        mTabNorColor = getResources().getColor(R.color.tab_text_nor);
        mTabSelColor = getResources().getColor(R.color.tab_text_sel);

        mViewPager.setAdapter(mSearchAdapter);
        mCurrentTab = 0;
        mViewPager.setCurrentItem(mCurrentTab);
        mViewPager.addOnPageChangeListener(mPageChangeListener);

        mSearchEdit = findViewById(R.id.search_edit);
        mSearchEdit.setImeOptions(EditorInfo.IME_ACTION_SEARCH|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mSearchEdit.setOnEditorActionListener(mEditorActionListener);

        mHorizontalTabBar = findViewById(R.id.horizontal_tab);
        mVerticalTabBar = findViewById(R.id.vertical_tab);

        mTabLeft = findViewById(R.id.btn_left);
        mTabRight= findViewById(R.id.btn_right);
        mTabUp = findViewById(R.id.btn_up);
        mTabDown = findViewById(R.id.btn_down);

        ViewUtils.setTextViewTextColor(mTabLeft, mTabSelColor);
        ViewUtils.setTextViewTextColor(mTabUp, mTabSelColor);

        mTabLeft.setOnClickListener(mTabClickListener);
        mTabLeft.setTag(TAB_WEB);

        mTabRight.setOnClickListener(mTabClickListener);
        mTabRight.setTag(TAB_IMAGE);

        mHorizontalTabView.add(mTabLeft);
        mHorizontalTabView.add(mTabRight);

        mTabUp.setOnClickListener(mTabClickListener);
        mTabUp.setTag(TAB_WEB);

        mTabDown.setOnClickListener(mTabClickListener);
        mTabDown.setTag(TAB_IMAGE);

        mVerticalTabView.add(mTabUp);
        mVerticalTabView.add(mTabDown);
    }

    private void handleSearch(String query) {
        if (DEBUG) {
            Log.d(TAG, "handleSearch() query=" + query);
        }

        if (TextUtils.isEmpty(query)) {
            Toast.makeText(this, R.string.input_warring, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mSearchAdapter == null) {
            return;
        }

        for (int i = 0; i < mSearchAdapter.getCount(); i++) {
            Fragment f = mSearchAdapter.getItem(i);
            if (f instanceof RecyclerViewFragment) {
                ((RecyclerViewFragment)f).requestSearchData(query);
            }
        }
    }

    private void updateViewTextColor(ArrayList<View> views, int position) {
        for (View v: views) {
            int index = (int)v.getTag();
            int color = (index == position) ? mTabSelColor : mTabNorColor;
            ViewUtils.setTextViewTextColor(v, color);
        }
    }
}
