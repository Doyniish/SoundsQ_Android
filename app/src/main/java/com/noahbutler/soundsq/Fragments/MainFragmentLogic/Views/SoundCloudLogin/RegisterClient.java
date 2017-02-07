package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.SoundCloudLogin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.noahbutler.soundsq.Activities.LaunchActivity;

/**
 * Created by gildaroth on 11/22/16.
 */

public class RegisterClient extends WebViewClient {

    /*************/
    /* DEBUG TAG */
    private static final String TAG = "Web Client";

    Activity activity;

    /* Url to load inside our web view in our app */
    private String registerUrl;

    public RegisterClient(Activity activity) {
        this.activity = activity;
    }

    /**
     * This method allows the web client to know that it should load this url inside of the app and
     * not move out of the app to display it.
     *
     * @param url
     */
    public void setUrl(String url) {
        this.registerUrl = url;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

        return false;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (Uri.parse(url).getHost().equals(registerUrl)) {
            // This is my web site, so do not override; let my WebView load the page
            Log.e(TAG, "Should override: " + url);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.e(TAG, "Loading url:" + url);
    }

    @Override
    public void onReceivedSslError(final WebView view, final SslErrorHandler handler, SslError error) {
        Log.d("CHECK", "onReceivedSslError");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        AlertDialog alertDialog = builder.create();
        String message = "Certificate error.";
        switch (error.getPrimaryError()) {
            case SslError.SSL_UNTRUSTED:
                message = "The certificate authority is not trusted.";
                break;
            case SslError.SSL_EXPIRED:
                message = "The certificate has expired.";
                break;
            case SslError.SSL_IDMISMATCH:
                message = "The certificate Hostname mismatch.";
                break;
            case SslError.SSL_NOTYETVALID:
                message = "The certificate is not yet valid.";
                break;
        }
        message += " Do you want to continue anyway?";
        alertDialog.setTitle("SSL Certificate Error");
        alertDialog.setMessage(message);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("CHECK", "Button ok pressed");
                // Ignore SSL certificate errors
                handler.proceed();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("CHECK", "Button cancel pressed");
                handler.cancel();
            }
        });
        alertDialog.show();
    }
}