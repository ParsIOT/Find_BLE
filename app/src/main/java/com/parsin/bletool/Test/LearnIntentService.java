package com.parsin.bletool.Test;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.parsin.bletool.Utils.Utils;
import com.parsin.bletool.internal.Constants;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import static com.parsin.bletool.Utils.Utils.hashMap;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class LearnIntentService extends IntentService {
    private static final String TAG = LearnIntentService.class.getSimpleName();
    private JSONObject wifiFingerprint = new JSONObject();

    private String groupName = " ";
    private String userName = " ";
    private String locationName = " ";
    private String strServer;
    private int msgSentCounter;
    private HashMap<String, ArrayList<Integer>> weightHashMap;


    public LearnIntentService() {
        super("LearnIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log("onCreate");
        msgSentCounter = 0;
        weightHashMap = new HashMap<>();

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        locationName = intent.getStringExtra("location");
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        groupName = sharedPreferences.getString(Constants.GROUP_NAME, Constants.DEFAULT_GROUP);
        userName = sharedPreferences.getString(Constants.USER_NAME, Constants.DEFAULT_USERNAME);
        strServer = sharedPreferences.getString(Constants.SERVER_NAME, Constants.DEFAULT_SERVER);

        for (int i = 1; i <= Constants.HOW_MANY_LEARNING_DEFAULT; i++) {
            log(String.valueOf(i));
            sendPayloadTask.run();
        }
        LearnFragment.updateUI(-1);

        log("service is going to be finished");
    }

    private void log(String message) {
        Log.d(TAG, message);
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
                    log("onHandleIntent: median: " + median + " " + s + " : " + hashMap.get(s));
                    wifiResults = new JSONObject();
                    wifiResults.put("mac", s);
                    wifiResults.put("rssi", median);
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
                    .url(Constants.DEFAULT_SERVER + "learn")
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Failed request: " + e.getMessage());
                    log("Can't connect to server.");
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    String body = response.body().string();
                    Log.d(TAG, "Learning step was successful:\n" + body);
                }
            });
            log("payload has been sent");
            ++msgSentCounter;
            EventBus.getDefault().post(msgSentCounter);
            LearnFragment.updateUI(msgSentCounter);
            log("event posted");
            try {
                Thread.sleep(Constants.SEND_PAYLOAD_PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("learnIntentService onDestroy");
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
