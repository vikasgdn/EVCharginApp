package com.evcharginapp.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.evcharginapp.AppConstants.AppConstant;
import com.evcharginapp.MainActivity;
import com.evcharginapp.R;
import com.evcharginapp.apppreferences.AppPreference;
import com.evcharginapp.databinding.ActivityLoginBinding;
import com.evcharginapp.databinding.ActivityRegisterBinding;
import com.evcharginapp.network.INetworkEvent;
import com.evcharginapp.network.NetworkModel;
import com.evcharginapp.network.NetworkService;
import com.evcharginapp.network.NetworkStatus;
import com.evcharginapp.network.NetworkURL;
import com.evcharginapp.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends BaseActivity implements INetworkEvent {

    private ActivityRegisterBinding registerBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(registerBinding.getRoot());
        initView();
        initVar();
    }


    @Override
    protected void initVar() {
        registerBinding.btnRegister.setOnClickListener(this);
        registerBinding.btnLogin.setOnClickListener(this);
        registerBinding.llTermcondition.setOnClickListener(this);    }

    @Override
    protected void initView() {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId())
        {
            case R.id.btn_register:
                if (TextUtils.isEmpty(registerBinding.etName.getText().toString()))
                    AppUtils.toast(this, "Please Enter Valid Name");
                else if (TextUtils.isEmpty(registerBinding.etEmail.getText().toString()) || !AppUtils.isValidEmail(registerBinding.etEmail.getText().toString()))
                    AppUtils.toast(this, "Please Enter Valid Email");
                else if (TextUtils.isEmpty(registerBinding.etPassword.getText().toString()))
                    AppUtils.toast(this, "Please Enter Valid Password");
                else if (registerBinding.etPassword.getText().toString().length() < 6 || registerBinding.etPassword.getText().toString().length() > 20)
                    AppUtils.toast(this, "Password length must be 6-20 characters");
                else if (!registerBinding.etPassword.getText().toString().matches(".*[0-9].*"))
                    AppUtils.toast(this, "Password should contain at least 1 digit");
                else if (!registerBinding.etPassword.getText().toString().matches(".*[a-zA-Z].*"))
                    AppUtils.toast(this, "Password should contain at least 1 alphabet");
                else if (TextUtils.isEmpty(registerBinding.etConfirmPassword.getText().toString()))
                    AppUtils.toast(this, "Please Enter Confirm Password");
                else if (!registerBinding.etPassword.getText().toString().equals(registerBinding.etConfirmPassword.getText().toString()))
                    AppUtils.toast(this, "Password and Confirm Password must be same");
                else if (!registerBinding.cbTermcondition.isChecked())
                    AppUtils.toast(this, "Please accept term & condition");
                else {
                    AppUtils.hideKeyboard(this);
                    getRegisterAPI();
                }
                break;
            case R.id.btn_login:
                Intent intent=new Intent(this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.ll_termcondition:
                String url = "https://datageniustech.com.au/terms-and-conditions.php";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;

        }
    }


    private void getRegisterAPI() {
        if (NetworkStatus.isNetworkConnected(this)) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", registerBinding.etName.getText().toString());
                jsonObject.put("email", registerBinding.etEmail.getText().toString());
                jsonObject.put("password", registerBinding.etPassword.getText().toString());
                NetworkService networkService = new NetworkService(jsonObject, NetworkURL.REGISTER_URL, AppConstant.METHOD_POST, this, this);
                networkService.call(new NetworkModel());
            }
            catch (Exception e) { e.printStackTrace(); }
        } else
            AppUtils.toast(this, getString(R.string.internet_error));
    }



    @Override
    public void onNetworkCallInitiated(String service) {
        registerBinding.pbProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNetworkCallCompleted(String type, String service, String response) {
        Log.e("RESPONSE SUCCESS","=== > "+response);
        registerBinding.pbProgress.setVisibility(View.GONE);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.optInt("status")==200)
            {
                AppUtils.toast(this,jsonObject.optString("msg"));
                setdataBlank();
                //  Intent intent=new Intent(this,LoginActivity.class);
                //startActivity(intent);
                // finish();
            }
            else
            {
                AppUtils.toast(this,jsonObject.optString("msg"));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setdataBlank() {
        registerBinding.etName.setText("");
        registerBinding.etEmail.setText("");
        registerBinding.etConfirmPassword.setText("");
        registerBinding.etPassword.setText("");
        registerBinding.cbTermcondition.setChecked(false);
    }


    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        Log.e("RESPONSE ERROR","=== > "+errorMessage);
        registerBinding.pbProgress.setVisibility(View.GONE);
        AppUtils.toast(this,"Opps Something went wrong..");
    }
}