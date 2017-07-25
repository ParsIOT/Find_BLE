package com.find.wifitool.Utils.httpCalls;

import android.os.Handler;
import android.os.Looper;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;



public class HttpCallback implements Callback {

    private static final String TAG = HttpCallback.class.getSimpleName();

    // private variables
    private final com.squareup.okhttp.Callback delegate;
    private final Handler handler;

    //Constructor
    public HttpCallback(com.squareup.okhttp.Callback delegate) {
        this.delegate = delegate;
        //this.handler = new Handler(Looper.getMainLooper());
        this.handler = new Handler();
    }

    @Override
    public void onFailure(final Request request, final IOException e) {
        /*handler.post(new Runnable() {
            @Override
            public void run() {
                delegate.onFailure(request, e);
            }
        });*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                delegate.onFailure(request, e);
            }
        }).start();
    }

    @Override
    public void onResponse(final Response response) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    delegate.onResponse(response);
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
