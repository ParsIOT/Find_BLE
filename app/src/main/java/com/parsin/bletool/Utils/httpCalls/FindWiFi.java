package com.parsin.bletool.Utils.httpCalls;



import org.json.JSONObject;

import okhttp3.Callback;

/**
 * Asynchronous HTTP library for the Find API.
 */
public interface FindWiFi {

    /**
     * Track
     */
    void findTrack(Callback callback, String serverAddr, JSONObject requestBody);

    /**
     * Learn
     */
    void findLearn(Callback callback, String serverAddr, JSONObject requestBody);
}
