package com.parsin.bletool.Test;

/**
 * Created by root on 4/12/17.
 */

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.parsin.bletool.R;
import com.parsin.bletool.Utils.Timer;
import com.parsin.bletool.Utils.Utils;
import com.parsin.bletool.internal.Constants;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TrackFragment extends Fragment {
    private static final String TAG = TrackFragment.class.getSimpleName();

    private WebView mWebView;
    private TextView currLocTextView;
    private Button mButton;

    private int rssi = 0;
    private HashMap<String, ArrayList<Integer>> hashMap;
    private JSONObject wifiFingerprint;
    private String currLocation;
    private String locationName = "locationName";
    private String userName = "hadi";
    private String groupName;
    private Timer timer;
    public static boolean isTrackTimerOn = false;

    private HashMap<String, ArrayList<Integer>> weightHashMap;

    // Required empty public constructor

    public TrackFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hashMap = new HashMap<>();
        timer = new Timer(sendPayloadTask, "TrackThread", Constants.SEND_PAYLOAD_PERIOD, false);
        weightHashMap = new HashMap<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track, container, false);
        mWebView = (WebView) rootView.findViewById(R.id.web_view);
        currLocTextView = (TextView) rootView.findViewById(R.id.labelLocationName);
        mButton = (Button) rootView.findViewById(R.id.button);
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(getActivity()), "Android");
        mWebView.loadUrl("file:///android_asset/leaflet/test-map.html");

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        groupName = sharedPreferences.getString(Constants.GROUP_NAME, Constants.DEFAULT_GROUP);
        userName = sharedPreferences.getString(Constants.USER_NAME, Constants.DEFAULT_USERNAME);

        mButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mWebView.getVisibility() == View.GONE) {
                    mWebView.setVisibility(View.VISIBLE);
                } else mWebView.setVisibility(View.GONE);

                return false;
            }
        });
        EventBus.getDefault().register(this);
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        log("track visibility: " + String.valueOf(isVisibleToUser));
        if (isVisibleToUser) {
            if (getActivity() != null) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
                groupName = sharedPreferences.getString(Constants.GROUP_NAME, Constants.DEFAULT_GROUP);
                userName = sharedPreferences.getString(Constants.USER_NAME, Constants.DEFAULT_USERNAME);
                if (!EventBus.getDefault().isRegistered(this))
                    EventBus.getDefault().register(this);
                changeTimerState();
            }
        } else {
//            super.onDetach();
            if (getActivity() != null) {
                changeTimerState();
            }
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        }
    }

    // Logging message in form of Toasts
    private void log(String message) {
        Log.d(TAG, message);
    }

    private class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void sendToAndroid(String text) {
            //TODO  save text
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

    @Subscribe
    public void onTrackEvent(HashMap<String, ArrayList<Integer>> hashMap) {
        log("event has come");
        synchronized (this) {
            this.hashMap = new HashMap<>(hashMap);
        }
        if (!isTrackTimerOn)
            changeTimerState();
    }

    private void changeTimerState() {
        log("changeTimerState");
        if (isTrackTimerOn) {
            isTrackTimerOn = false;
            timer.stopTimer();
        } else {
            isTrackTimerOn = true;
            timer.startTimer();
        }
    }

    private Runnable sendPayloadTask = new Runnable() {
        @Override
        public void run() {
            log(Thread.currentThread().getName());
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            try {
                JSONObject wifiResults;
                JSONArray wifiResultsArray = new JSONArray();
                Long timeStamp = System.currentTimeMillis() / 1000;
                for (String s : hashMap.keySet()) {
                    int median = Utils.getMedian(hashMap.get(s));
                    log("sendPayloadTask: median: " + median + " " + s + " : " + hashMap.get(s));
                    wifiResults = new JSONObject();
                    wifiResults.put("mac", s);
                    wifiResults.put("rssi", getRssiUpdateWeight(s, median));
                    wifiResultsArray.put(wifiResults);
                }
                log("\n");
                wifiFingerprint = new JSONObject();
                wifiFingerprint.put("group", groupName);
                wifiFingerprint.put("username", userName);
                wifiFingerprint.put("location", locationName);
                wifiFingerprint.put("time", timeStamp);
                wifiFingerprint.put("wifi-fingerprint", wifiResultsArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, String.valueOf(wifiFingerprint));
            Request request = new Request.Builder()
                    .url(Constants.DEFAULT_SERVER + "track")
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String body = response.body().string();
                    log(body);
                    if (response.isSuccessful()) {
                        Log.d(TAG, body);
                        try {
                            JSONObject json = new JSONObject(body);
                            currLocation = json.getString("knn");
                            updateMap(currLocation);
                            log("currLocation : " + currLocation);
                        } catch (JSONException e) {
                            Log.e(TAG, "Failed to extract location from response: " + body, e);
                        }
                    } else {
                        Log.e(TAG, "Unsuccessful request: " + body);
                    }
                }
            });
        }
    };

    private void updateMap(String currLocation) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currLocTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.currentLocationColor));
                currLocTextView.setText(currLocation);
                String js_location = String.format("javascript:update_map(\'%s\')", currLocation);
                mWebView.loadUrl(js_location);
            }
        });
    }

    private int getRssiUpdateWeight(String mac, int median) {
        int WEIGHTED_AVERAGE_LIST_SIZE = Constants.weightArr.length;
        int maxIdx = 20;
        double res = 0;
        double wightSum = 0;
        int arrSize = 0;
        ArrayList<Integer> resArr = new ArrayList<>();
        ArrayList<Integer> arrayList = new ArrayList<>();

        for (String s : weightHashMap.keySet()){
            log("weightHashMap : " + s + " " + Arrays.toString(weightHashMap.get(s).toArray()));
        }

        if (!weightHashMap.containsKey(mac)) {
            for (int i = 0; i < WEIGHTED_AVERAGE_LIST_SIZE - 1; i++) {
                resArr.add(maxIdx);
            }
            resArr.add(0, median);
        }else {
            arrayList = new ArrayList<>(weightHashMap.get(mac));
            arrSize = arrayList.size();
            resArr = new ArrayList<>(arrayList.subList(0, arrSize - 1));
            resArr.add(0, median);
        }
        weightHashMap.put(mac, resArr);

        for (int i = 0; i < WEIGHTED_AVERAGE_LIST_SIZE; i++) {
            if(resArr.get(i) != maxIdx) {
                wightSum = wightSum + Constants.weightArr[i];
                res = res + resArr.get(i) * Constants.weightArr[i];
            }
        }
        res = res / wightSum;
        return (int) res;
    }
}

