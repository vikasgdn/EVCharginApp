package com.evcharginapp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.evcharginapp.AppConstants.AppConstant;
import com.evcharginapp.MainActivity;
import com.evcharginapp.R;
import com.evcharginapp.apppreferences.AppPreference;
import com.evcharginapp.databinding.ActivityLoginBinding;
import com.evcharginapp.network.INetworkEvent;
import com.evcharginapp.network.NetworkModel;
import com.evcharginapp.network.NetworkService;
import com.evcharginapp.network.NetworkStatus;
import com.evcharginapp.network.NetworkURL;
import com.evcharginapp.utils.AppUtils;

import org.json.JSONObject;

public class LoginActivity extends BaseActivity implements INetworkEvent {

    private ActivityLoginBinding loginBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());
        AppPreference.INSTANCE.initSharedPreference(this);
        loginBinding.btnLogin.setOnClickListener(this);
        loginBinding.btnRegister.setOnClickListener(this);
    }


    @Override
   protected void initVar() {

    }

    @Override
    protected void initView() {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.btn_login:
                if (TextUtils.isEmpty(loginBinding.etEmail.getText().toString()))
                    AppUtils.toast(this,"Please Enter Valid Email");
                else if (TextUtils.isEmpty(loginBinding.etPassword.getText().toString()))
                    AppUtils.toast(this,"Please Enter Valid Password");
                else
                    getLoginAPI();
                break;
            case R.id.btn_register:
                Intent intent1=new Intent(this, RegisterActivity.class);
                startActivity(intent1);
                break;
        }
    }

    private void getLoginAPI() {
        if (NetworkStatus.isNetworkConnected(this)) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user_id", loginBinding.etEmail.getText().toString());
                jsonObject.put("salted_password", loginBinding.etPassword.getText().toString());
                NetworkService networkService = new NetworkService(jsonObject, NetworkURL.LOGIN_URL, AppConstant.METHOD_POST, this, this);
                networkService.call(new NetworkModel());
            }
            catch (Exception e) { e.printStackTrace(); }
        } else
            AppUtils.toast(this, getString(R.string.internet_error));
    }


    @Override
    public void onNetworkCallInitiated(String service) {

        loginBinding.pbProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response)
    {
        Log.e("RESPONSE SUCCESS","=== > "+response);
        loginBinding.pbProgress.setVisibility(View.GONE);
        try {
            JSONObject jsonObject=new JSONObject(response);
            if(jsonObject.optInt("status")==200)
            {
                JSONObject dataOBJ= jsonObject.optJSONObject("data");
                AppPreference.INSTANCE.setUserName(dataOBJ.getString("user_name"));
                AppPreference.INSTANCE.setOemId(dataOBJ.getString("oem_id"));
                AppPreference.INSTANCE.setUId(dataOBJ.getString("uid"));
                AppPreference.INSTANCE.setSessionId(dataOBJ.getString("session_id"));
                AppPreference.INSTANCE.setUserId(dataOBJ.getString("user_id"));
                AppPreference.INSTANCE.setLoginStatus(true);
                Intent intent=new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                AppUtils.toast(this,jsonObject.optString("msg"));
            }
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        Log.e("RESPONSE ERROR","=== > "+errorMessage);
        loginBinding.pbProgress.setVisibility(View.GONE);
    }
}