package com.evcharginapp.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.evcharginapp.AppConstants.AppConstant;
import com.evcharginapp.apppreferences.AppPreference;
import com.evcharginapp.ui.activity.LoginActivity;

import org.json.JSONObject;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class NetworkService  {
    private static final MediaType JSON = MediaType.parse("application/json");
    private String mOrderStatus = "";
    private String service;
    private String mMethod;
    private INetworkEvent networkEvent;
    private Context context;
    private JSONObject jsonObject;

    public NetworkService(JSONObject jsonObject,String serviceName, String method, INetworkEvent networkEvent, Context context) {
        service = serviceName;
        mMethod = method;
        this.networkEvent = networkEvent;
        this.context = context;
        this.jsonObject=jsonObject;
    }
    public void call(NetworkModel input) {
        new NetworkTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, input);
    }

    private class NetworkTask extends AsyncTask<NetworkModel, Void, String> {
        boolean isError = false;
        String message = "";

        @Override
        protected void onPreExecute() {
            if (networkEvent != null) {
                networkEvent.onNetworkCallInitiated(service);
            }
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected String doInBackground(NetworkModel... networkModels) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            NetworkModel input = networkModels[0];
            int responseCode = -1;
            try {
                final OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(120, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        .build();


                String service = NetworkService.this.service.split(",")[0];
                String jsonReq = input.getJsonBody();


                RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());


                Request request = null;
                if (mMethod.equalsIgnoreCase(AppConstant.METHOD_POST)) {
                    request = new Request.Builder()
                            .url(service)
                            .addHeader("token", AppPreference.INSTANCE.getAccessToken())
                            .addHeader("oem_id", AppPreference.INSTANCE.getGetOemId())
                            .post(requestBody)
                            .build();

                } else if (mMethod.equalsIgnoreCase(AppConstant.METHOD_GET)) {
                    request = new Request.Builder()
                            .url(service)
                            .addHeader("token", AppPreference.INSTANCE.getAccessToken())
                            .addHeader("oem_id", AppPreference.INSTANCE.getGetOemId())
                            .build();

                }

                Log.e("URL====> ",""+service+" || "+jsonObject);
                Response response = client.newCall(request).execute();

                Log.e("NW RESPONSE====> ",""+service+" || "+response.toString());
                if (response.code() >= 200 && response.code() < 500) {
                    isError = false;
                    ResponseBody responseBody = response.body();
                    String s = responseBody.string();
                    return s;
                }
                else {
                    isError = true;
                    message = ""+response.message();

                }
            } catch (Exception e) {
                isError = true;
                message = "Oops! Your internet seems to be on a power nap. Please check your internet settings.";

                e.printStackTrace();
            }
            return responseCode + " " + message;
        }

        @Override
        protected void onPostExecute(String s) {
            if (networkEvent != null) {
                try {
                    if (isError)
                    {
                        networkEvent.onNetworkCallError(service, message);
                    }
                    else {
                        networkEvent.onNetworkCallCompleted("",service, s);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }

}
