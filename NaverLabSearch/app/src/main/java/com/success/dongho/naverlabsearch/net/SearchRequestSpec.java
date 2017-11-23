
package com.success.dongho.naverlabsearch.net;

import android.text.TextUtils;

import com.android.volley.Request;
import com.success.dongho.naverlabsearch.data.SearchRequestParam;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class SearchRequestSpec {

    public static final String SEARCH_HOST              = "https://openapi.naver.com/v1/search";
    public static final String SEARCH_CLIENT_ID         = "KbtpXb10mCmy7rvSGAh0";
    public static final String SEARCH_CLIENT_SECRET     = "2HFJCsRuEz";

    public static final String SEARCH_IMAGE_SORT_SIM     = "sim";


    public static final int METHOD_GET      = Request.Method.GET;

    private String mUri;
    private final Map<String, String> mParams;
    private final Map<String, String> mCustomHeaders;

    private final int mMethod;
    private String mEncoding;
    private String mCodePath;

    private static final String PARAMETER_ENCODING = "utf-8";

    public static final SearchRequestSpec genGet(Class<?> code) {
        return new SearchRequestSpec(METHOD_GET, SearchCodePool.api(code));
    }

    public SearchRequestSpec(final int method, String code) {
        this(method, code, PARAMETER_ENCODING);
    }

    public String buildUri(String path) {
        return String.format("%s/%s", SEARCH_HOST, path);
    }

    public SearchRequestSpec(final int method, final String code, final String encoding) {
        mEncoding = encoding;
        mParams = new HashMap<String, String>();
        mMethod = method;
        mCodePath = code;
        mUri = buildUri(mCodePath);

        mCustomHeaders = new HashMap<String, String>();
        mCustomHeaders.put(SearchRequestParam.NAVER_CLIENT_ID, SEARCH_CLIENT_ID);
        mCustomHeaders.put(SearchRequestParam.NAVER_CLIENT_SECRET, SEARCH_CLIENT_SECRET);
    }

    public SearchRequestSpec param(String key, String value) {
        if (!TextUtils.isEmpty(value)) {
            mParams.put(key, value);
        }
        return this;
    }

    public SearchRequestSpec param(String key, int value) {
        return param(key, Integer.toString(value));
    }

    public final Map<String, String> params() {
        return mParams;
    }

    public final Map<String, String> customHeaders() {
        return mCustomHeaders;
    }

    public int method() {
        return mMethod;
    }

    public String buildUri() {
        if (mUri == null) {
            return null;
        }

        if (mMethod == METHOD_GET) {
            if (mParams.isEmpty()) {
                return mUri;
            }
            return appendUriQuery(mUri, buildParameters());
        } else {
            throw new IllegalArgumentException("Naver search support get method now");
        }
    }

    private String buildParameters() {
            StringBuilder encodedParams = new StringBuilder();
            try {
                for (Map.Entry<String, String> entry : mParams.entrySet()) {
                    encodedParams.append(entry.getKey());
                    encodedParams.append('=');
                    encodedParams.append(URLEncoder.encode(entry.getValue(), mEncoding));
                    encodedParams.append('&');
                }
                String binenc = encodedParams.toString();
                if (binenc.endsWith("&")) {
                    binenc = binenc.substring(0, binenc.length() - 1);
                }
                return binenc;
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Encoding not supported: " + mEncoding, e);
            }
    }

    private String appendUriQuery(String uri, String value) {
        String prefix = "?";

        if (uri.contains("?")) {
            prefix = "&";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(uri);

        sb.append(String.format("%s%s", prefix, value));

        return sb.toString();
    }
}
