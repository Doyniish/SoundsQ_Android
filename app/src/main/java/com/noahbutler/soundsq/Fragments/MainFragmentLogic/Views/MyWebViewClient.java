package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by gildaroth on 11/22/16.
 */

public class MyWebViewClient extends WebViewClient {

    /*************/
    /* DEBUG TAG */
    private static final String TAG = "Web Client";

    /* Url to load inside our web view in our app */
    private String registerUrl;

    /**
     * This method allows the web client to know that it should load this url inside of the app and
     * not move out of the app to display it.
     * @param url
     */
    public void setUrl(String url) {
        this.registerUrl = url;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (Uri.parse(url).getHost().equals(registerUrl)) {
            // This is my web site, so do not override; let my WebView load the page
            Log.e(TAG, "Should override: " + url);
            return false;
        }else {
            return true;
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.e(TAG, "Loading url:" + url);
    }
}
