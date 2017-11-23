package com.success.dongho.naverlabsearch.ui.layout.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.success.dongho.naverlabsearch.R;


public class SearchEditText extends AppCompatEditText {

    private boolean mIsShowClear;
    private Drawable mClearDrawable;

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public synchronized boolean onEditorAction(TextView v,
                                                       int actionId, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });

        mClearDrawable = getResources().getDrawable(R.drawable.ic_edittext_clear);

        setHeight(mClearDrawable.getBounds().height());

        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
                mClearDrawable.getIntrinsicHeight());


        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();

                if (!mIsShowClear) {
                    return false;
                }

                if (action != MotionEvent.ACTION_UP) {
                    return false;
                }

                if (event.getX() > getWidth()
                        - getPaddingRight() - mClearDrawable.getIntrinsicWidth()) {

                    setText("");
                }
                return false;
            }
        });
        addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                setCompoundDrawables(null, null, mClearDrawable, null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if (length == 0) {
                    mIsShowClear = false;
                    setCompoundDrawables(null, null, null, null);
                } else {
                    mIsShowClear = true;
                    setCompoundDrawables(null, null, mClearDrawable, null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
        });


    }

}
