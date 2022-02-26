package com.evcharginapp.apppreferences;

import android.content.Context;
import android.content.SharedPreferences;
import okhttp3.HttpUrl;

public enum AppPreference {
    INSTANCE;
    
    private static final String AppPreferenceName = "EVCharging";
    SharedPreferences.Editor mEditor;
    SharedPreferences mSharedPref;



    public void initSharedPreference(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppPreferenceName, 0);
        this.mSharedPref = sharedPreferences;
        this.mEditor = sharedPreferences.edit();
    }


    public void clearPreferences() {
        this.mEditor.clear();
        this.mEditor.commit();
    }

    public void setLoginStatus(boolean logined) {
        this.mEditor.putBoolean("is_login", logined);
        this.mEditor.commit();
    }

    public boolean getLoginStatus() {
        return this.mSharedPref.getBoolean("is_login", false);
    }

    public void setAccessToken(String token)
    {
        this.mEditor.putString("token", token);
        this.mEditor.commit();
    }

    public String getAccessToken() {
        return this.mSharedPref.getString("token", "be6a388c-ef44-11eb-a8dc-e6e3bf15298e");
    }

    public void setUserName(String username)
    {
        this.mEditor.putString("username", username);
        this.mEditor.commit();
    }
    public String getGetUserName() {
        return this.mSharedPref.getString("username", "");
    }

    public void setOemId(String oemid)
    {
        this.mEditor.putString("oemid", oemid);
        this.mEditor.commit();
    }

    public String getGetOemId() {
        return this.mSharedPref.getString("oemid", "");
    }

    public void setUId(String uid)
    {
        this.mEditor.putString("uid", uid);
        this.mEditor.commit();
    }

    public String getGetUId() {
        return this.mSharedPref.getString("uid", "");
    }

    public void setSessionId(String sessionId)
    {
        this.mEditor.putString("sessionId", sessionId);
        this.mEditor.commit();
    }

    public String getGetSessionId() {
        return this.mSharedPref.getString("sessionId", "");
    }

    public void setUserId(String userId)
    {
        this.mEditor.putString("userId", userId);
        this.mEditor.commit();
    }

    public String getGetUserId() {
        return this.mSharedPref.getString("userId", "");
    }


}
