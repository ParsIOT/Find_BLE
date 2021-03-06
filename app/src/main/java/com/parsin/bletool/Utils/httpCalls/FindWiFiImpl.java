package com.parsin.bletool.Utils.httpCalls;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;



import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class FindWiFiImpl implements FindWiFi {

    private static final String TAG = FindWiFiImpl.class.getSimpleName();
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    // private variables
    private static final int GET = 0;
    private static final int PUT = 1;
    private static final int POST = 2;
    private static final int DELETE = 3;

    private final Context ctx;
    private final OkHttpClient httpClient;

    // Constructor
    public FindWiFiImpl(Context context) {
        this.ctx = context;
        this.httpClient = new OkHttpClient();
    }

    @Override
    public void findTrack(Callback callback, String serverAddr, JSONObject requestBody) {
        new AuthTask("track", serverAddr, POST, requestBody.toString(), callback).execute();
//        new AuthTask1("track", serverAddr, POST, requestBody.toString(), callback).doInBackground();

    }

    @Override
    public void findLearn(Callback callback, String serverAddr, JSONObject requestBody) {
        new AuthTask("learn", serverAddr, POST, requestBody.toString(), callback).execute();
//        new AuthTask1("learn", serverAddr, POST, requestBody.toString(), callback).doInBackground();
    }

    private class AuthTask extends AsyncTask<Void, Void, Void> {
        private final String urlPart;
        private final int method;
        private final String json;
        private final String serverAddr;
        private final Callback callback;


        AuthTask(String urlPart, String serverAddr, int method, String json, Callback callback) {
            this.urlPart = urlPart;
            this.serverAddr = serverAddr;
            this.method = method;
            this.json = json;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Request.Builder requestBuilder = new Request.Builder()
                        .url(serverAddr + urlPart);
                switch (method) {
                    case PUT:
                        requestBuilder.put(RequestBody.create(MEDIA_TYPE_JSON, json));
                        break;
                    case POST:
                        requestBuilder.post(RequestBody.create(MEDIA_TYPE_JSON, json));
                        break;
                    case DELETE:
                        requestBuilder.delete(RequestBody.create(MEDIA_TYPE_JSON, json));
                        break;
                    default: break;
                }
                Request request = requestBuilder.build();
                httpClient.newCall(request).enqueue(new HttpCallback(callback));
            } catch (Exception e) {
                Log.e(TAG, "IOException", e);
            }
            return null;
        }
    }


    private class AuthTask1 {
        private final String urlPart;
        private final int method;
        private final String json;
        private final String serverAddr;
        private final Callback callback;

        AuthTask1(String urlPart, String serverAddr, int method, String json, Callback callback) {
            this.urlPart = urlPart;
            this.serverAddr = serverAddr;
            this.method = method;
            this.json = json;
            this.callback = callback;
        }


        protected Void doInBackground() {

            try {
//                Utils.semaphore.acquire();
                Request.Builder requestBuilder = new Request.Builder()
                        .url(serverAddr + urlPart);
                switch (method) {
                    case PUT:
                        requestBuilder.put(RequestBody.create(MEDIA_TYPE_JSON, json));
                        break;
                    case POST:
                        requestBuilder.post(RequestBody.create(MEDIA_TYPE_JSON, json));
                        break;
                    case DELETE:
                        requestBuilder.delete(RequestBody.create(MEDIA_TYPE_JSON, json));
                        break;
                    default: break;
                }
                Request request = requestBuilder.build();
                httpClient.newCall(request).enqueue(new HttpCallback(callback));
//                Log.e(TAG, "sendPayload: lock is being acquire");


            } catch (Exception e) {
                Log.e(TAG, "IOException", e);
            }
            return null;
        }
    }
}
