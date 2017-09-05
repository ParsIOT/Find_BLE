package com.parsin.bletool.Utils.httpCalls;

import android.os.Handler;
import android.os.Looper;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;


public class HttpCallback implements Callback {

    private static final String TAG = HttpCallback.class.getSimpleName();

    // private variables
    private final Callback delegate;
    private final Handler handler;

    //Constructor
    public HttpCallback(Callback delegate) {
        this.delegate = delegate;
        this.handler = new Handler(Looper.getMainLooper());
//        this.handler = new Handler();
    }

    @Override
    public void onFailure(Call call, IOException e) {
        /*handler.post(new Runnable() {
            @Override
            public void run() {
                delegate.onFailure(request, e);
            }
        });*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                delegate.onFailure(call, e);
            }
        }).start();
    }

    @Override
    public void onResponse(Call call, final Response response) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    delegate.onResponse(call, response);
                } catch (IOException e) {
                    delegate.onFailure(null, e);
                }
            }
        }).start();
/*        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    delegate.onResponse(response);
                } catch (IOException e) {
                    delegate.onFailure(null, e);
                }
            }
        });*/
    }
}
