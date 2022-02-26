package com.evcharginapp.ui.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evcharginapp.R;


public class WebViewActivity extends BaseActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_webview);

    }



    @Override
    public void onBackPressed() {
        if (mWebView != null) {
            mWebView.destroy();
        }
        mWebView = null;
        super.onBackPressed();
    }

    @Override
    protected void initVar() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.clearCache(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);

        WebSettings settings = mWebView.getSettings();
        settings.setDomStorageEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);

        mWebView.loadUrl("https://datageniustech.com.au/terms-and-conditions.php");
    }

    @Override
    protected void initView() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.iv_back) {
            onBackPressed();
        }
    }
}