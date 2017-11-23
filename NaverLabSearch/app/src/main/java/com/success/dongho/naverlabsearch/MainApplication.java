package com.success.dongho.naverlabsearch;

import android.app.Application;

import com.success.dongho.naverlabsearch.net.VolleyManager;

/**
 * Created by 1002594 on 2017. 11. 22..
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        VolleyManager.initialize(this);
    }
}
