package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by gildaroth on 11/22/16.
 */

public class MyWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}
