package com.evcharginapp.ui.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

import com.evcharginapp.R;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    protected ProgressDialog mProgressDialog;
    private ProgressBar progressBar;

    /* access modifiers changed from: protected */
    protected abstract void initVar();

    /* access modifiers changed from: protected */
    protected abstract void initView();

    public void onClick(View view) {
    }

}
