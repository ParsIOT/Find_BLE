package com.parsin.bletool.View;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.parsin.bletool.R;
import com.parsin.bletool.View.Fragment.TrackFragment;

import java.util.ArrayList;
import java.util.Arrays;


public class LocationLearnedActivity extends AppCompatActivity {
    private static final String TAG = LocationLearnedActivity.class.getSimpleName();
    WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_learned);

        mWebView = (WebView) findViewById(R.id.location_learned_web_view);
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(this), "AndroidLocation");
        mWebView.loadUrl("file:///android_asset/leaflet/test-locations.html");

        Intent intent = getIntent();
        ArrayList<String> locations = intent.getStringArrayListExtra("locations");


        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                for (String s : locations) {
                    String js_location = String.format("javascript:showLocation(\'%s\')", s);
                    mWebView.loadUrl(js_location);
                }
            }
        });


        Log.d(TAG, "onCreate: locations " + Arrays.toString(locations.toArray()));
    }


    private class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void sendToAndroid(String text) {
            Intent intent = new Intent(LocationLearnedActivity.this, MainActivity.class);
            intent.putExtra("location_selected", text);
            startActivity(intent);
            finish();
        }

        @JavascriptInterface
        public String getFromAndroid() {
            return "This is from android.";
        }

        @JavascriptInterface
        public void startMap() {
            Intent mIntent = new Intent();
            ComponentName component = new ComponentName(
                    "com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity");
            mIntent.setComponent(component);
            startActivity(mIntent);
        }
    }

}
