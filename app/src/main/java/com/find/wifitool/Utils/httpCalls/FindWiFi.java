package com.find.wifitool.Utils.httpCalls;

import com.squareup.okhttp.Callback;

import org.json.JSONObject;

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
