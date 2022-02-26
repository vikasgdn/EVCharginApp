package com.evcharginapp.network;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Vikas on 3/24/2015.
 */
public class NetworkModel {

    public String getJsonBody() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }
}
