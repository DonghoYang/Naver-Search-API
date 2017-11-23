package com.success.dongho.naverlabsearch.data;

import com.google.gson.Gson;

/**
 * Created by 1002594 on 2017. 11. 22..
 */

public class SearchJsonObject {

    @Override
    public String toString() {
        return new Gson().toJson(this).toString();
    }
}
