package com.evcharginapp.network;

/**
 * Created by Vikas.
 */
public interface INetworkEvent {
    public void onNetworkCallInitiated(String service);

    public void onNetworkCallCompleted(String type, String service, String response);

    public void onNetworkCallError(String service, String errorMessage);
}
