package com.parsin.bletool.View.Fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parsin.bletool.Model.Advertisement;
import com.parsin.bletool.Controller.OnFragmentInteractionListener;
import com.parsin.bletool.Model.DaoSession;
import com.parsin.bletool.Model.LocationValidation;
import com.parsin.bletool.R;
import com.parsin.bletool.Utils.MyApp;
import com.parsin.bletool.Utils.Server.ParsinServer;
import com.parsin.bletool.Utils.Server.SendOptionEnum;
import com.parsin.bletool.Utils.StaticObjects;
import com.parsin.bletool.Utils.Utils;
import com.parsin.bletool.View.LocationLearnedActivity;
import com.parsin.bletool.View.LogDbActivity;
import com.parsin.bletool.internal.Constants;
import com.parsin.bletool.internal.FindUtils;
import com.parsin.bletool.internal.wifi.WifiIntentReceiver;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by root on 4/12/17.
 */


public class TrackFragment extends Fragment {

    private static final String TAG = TrackFragment.class.getSimpleName();

    //   TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    //private variables
    private OnFragmentInteractionListener mListener;
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private String strUsername;
    private String strServer;
    private String strParsinServer;
    private String strGroup;
    private String strPreLocation = "";  // We don't need any location value fr Tracking
    private String currLocation;
    private int trackVal;

    private TextView currLocView;
    private WebView mWebView;
    private Button mButton;

    private boolean getNotifFromServer = true;
    Handler handler = new Handler();
    private List<Integer> adverHasShown = new ArrayList<>();
    private HashMap<String, ArrayList<Integer>> hashMap;

    private ArrayList<Advertisement> advertisements;
    //FIXME time of showing notif is not adopted with THRESH
    private int trackNotifyTRESH = 1;

    private ParsinServer parsin;
    private TextView fieldTrackCounter;
    private LinearLayout trackCounterContainer;
    private int intTrackCounterDuration = 0;
    private int trackCounterAdv;
    private String imHere = null;
    private DaoSession mDaoSession;
    private int altBeaconCounter = 0;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    // Required empty public constructor
    public TrackFragment() {
        parsin = new ParsinServer();
        mContext = getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getNotifFromServer = true;
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        hashMap = new HashMap<>();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Checking if the Location service is enabled in case of Android M or above users
        if (!FindUtils.isLocationAvailable(mContext)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("Location service is not On. Users running Android M and above have to turn on location services for FIND to work properly");
            dialog.setPositiveButton("Enable Locations service", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(myIntent);
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    logMeToast("Thank you!! Getting things in place.");
                }
            });
            dialog.show();
        }
        mDaoSession = ((MyApp) getActivity().getApplication()).getDaoSession();
        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        strGroup = sharedPreferences.getString(Constants.GROUP_NAME, Constants.DEFAULT_GROUP);
        strParsinServer = sharedPreferences.getString(StaticObjects.PARSIN_SERVER_NAME, StaticObjects.ParsinServerIp);
        strServer = sharedPreferences.getString(Constants.SERVER_NAME, Constants.DEFAULT_SERVER);
        strUsername = String.valueOf(sharedPreferences.getString(Constants.USER_NAME, Constants.DEFAULT_USERNAME));
        trackVal = sharedPreferences.getInt(Constants.TRACK_INTERVAL, Constants.DEFAULT_TRACKING_INTERVAL);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track, container, false);
        currLocView = (TextView) rootView.findViewById(R.id.labelLocationName);
        fieldTrackCounter = (TextView) rootView.findViewById(R.id.fieldTrackCounterInteger);
        trackCounterContainer = (LinearLayout) rootView.findViewById(R.id.track_counter_container);
        fieldTrackCounter.setText(String.valueOf(intTrackCounterDuration));
        mWebView = (WebView) rootView.findViewById(R.id.web_view);
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(getActivity()), "Android");
        mWebView.loadUrl("file:///android_asset/leaflet/test-map.html");
        mButton = (Button) rootView.findViewById(R.id.button);
        adverHasShown = new ArrayList<>();
        mButton.setOnClickListener(v -> {
//            mWebView.loadUrl("javascript:update_map()");
            Toast.makeText(getActivity(), "Long Click to Toggle\nWebView & TextView", Toast.LENGTH_SHORT).show();
        });

        mButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mWebView.getVisibility() == View.GONE) {
                    mWebView.setVisibility(View.VISIBLE);
                } else mWebView.setVisibility(View.GONE);

                return false;
            }
        });
        trackCounterAdv = trackNotifyTRESH;
        advertisements = new ArrayList<>();

        Intent intent = getActivity().getIntent();
        if (intent.hasExtra("location_selected"))
            imHere = intent.getStringExtra("location_selected");


        /*
        handler.post(runnableCode);
        ATTENTION: commented for using altBeacon.
        */

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.TRACK_BCAST));


        EventBus.getDefault().register(this);

        return rootView;
    }

    // Getting the CurrentLocation from the received broadcast
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("tracking:", "New location.");
            currLocation = intent.getStringExtra("location");

            currLocView.setTextColor(getResources().getColor(R.color.currentLocationColor));
            currLocView.setText(currLocation);

            Log.d("your location:", currLocation);


            notifying();

            //Log.e("TRACK", currLocation);
            if (!strPreLocation.equals(currLocation)) {
                String js_location = String.format("javascript:update_map(\'%s\')", currLocation);
                mWebView.loadUrl(js_location);
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    changeTrackCounter();
                    Log.d(TAG, "run: log1");
                }
            });
            strPreLocation = currLocation;
            Log.d(TAG, "run: log3");

        }
    };

    private void notifying() {
        if (!strPreLocation.equals("")) {
            if (strPreLocation.equals(currLocation)) {
                if (trackCounterAdv <= 0) {
                    for (Advertisement advertisement : advertisements) {
                        Log.d("resJSONreqFull", "resJSONreqx");
                        float x_y[] = Utils.getIntXY(currLocation);
                        if (!adverHasShown.contains(advertisement.getId())) {
                            if (Utils.inSquare(x_y[0], x_y[1], advertisement)) {
                                Log.d("Notification adv :", advertisement.getTitle());
                                Utils.notifyThis(advertisement.getId(), advertisement.getTitle(), advertisement.getText(), advertisement.getImg(), getActivity());
                                adverHasShown.add(advertisement.getId());
                            }
                        }
                    }
                    trackCounterAdv = trackNotifyTRESH;
                }
                trackCounterAdv--;
            } else
                trackCounterAdv = trackNotifyTRESH;
        }
    }

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= 23) {
//                if (FindUtils.isWiFiAvailable(mContext) && FindUtils.hasAnyLocationPermission(mContext)) {
                sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
                strGroup = sharedPreferences.getString(Constants.GROUP_NAME, Constants.DEFAULT_GROUP);
                strParsinServer = sharedPreferences.getString(StaticObjects.PARSIN_SERVER_NAME, StaticObjects.ParsinServerIp);
                strServer = sharedPreferences.getString(Constants.SERVER_NAME, Constants.DEFAULT_SERVER);
                strUsername = sharedPreferences.getString(Constants.USER_NAME, Constants.DEFAULT_USERNAME);
                trackVal = sharedPreferences.getInt(Constants.TRACK_INTERVAL, Constants.DEFAULT_TRACKING_INTERVAL);

                Intent intent = new Intent(mContext, WifiIntentReceiver.class);
                intent.putExtra("event", Constants.TRACK_TAG);
                intent.putExtra("groupName", strGroup);
                intent.putExtra("userName", strUsername);
                intent.putExtra("serverName", strServer);
                intent.putExtra("locationName", sharedPreferences.getString(Constants.LOCATION_NAME, ""));
                intent.putExtra("hashMap", hashMap);
                mContext.startService(intent);
                Log.d(TAG, "run: log4");

            } else if (Build.VERSION.SDK_INT < 23) {
//                if (FindUtils.isWiFiAvailable(mContext)) { TODO: uncomment this later
                Intent intent = new Intent(mContext, WifiIntentReceiver.class);
                intent.putExtra("event", Constants.TRACK_TAG);
                intent.putExtra("groupName", strGroup);
                intent.putExtra("userName", strUsername);
                intent.putExtra("serverName", strServer);
                intent.putExtra("locationName", sharedPreferences.getString(Constants.LOCATION_NAME, ""));
                intent.putExtra("hashMap", hashMap);
                mContext.startService(intent);
                Log.d(TAG, "run: log5");
                Log.d(TAG, "run: " + currLocation);

            } else {
                return;
            }
            //handler.postDelayed(runnableCode, trackVal * 1000);
            //ATTENTION: commented for using Altbeacon.
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        handler.removeCallbacks(runnableCode);
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiver);
        /*getNotifFromServer = true;*/
        mListener = null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && mContext != null) {
            super.onDetach();
            try {
                Log.d(TAG, "setUserVisibleHint: this is test. remove me. false");
                EventBus.getDefault().unregister(this);
                /*handler.removeCallbacks(runnableCode);*/
                //ATTENTION: commented for using Altbeacon.
                LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiver);
                getNotifFromServer = true;

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (mContext != null) {
                Log.d(TAG, "setUserVisibleHint: this is test. remove me. true");
                if (!EventBus.getDefault().isRegistered(this))
                    EventBus.getDefault().register(this);
                /*handler.post(runnableCode);*/
                //ATTENTION: commented for using Altbeacon.
                LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver,
                        new IntentFilter(Constants.TRACK_BCAST));
                parsin.getAdvJSON(strParsinServer +
                        SendOptionEnum.Get_Advertisement.url(), advertisements);
            }
        }
    }

    private void logMeToast(String message) {
        Log.d(TAG, message);
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_get_locations:
                getLocationsFromServer();
                return true;
            case R.id.action_upload_db:
                Snackbar.make(getActivity().findViewById(android.R.id.content), "click to upload db ->", Snackbar.LENGTH_LONG)
                        .setAction("upload", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                uploadDb(getActivity().getDatabasePath("green.db").getAbsolutePath());
                            }
                        }).show();
                return true;
            case R.id.action_log_db:
                startActivity(new Intent(getActivity(), LogDbActivity.class));
                return true;
        }
        return false;
    }

    private void getLocationsFromServer() {
        SharedPreferences sharedPreferences;
        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        String url = sharedPreferences.getString(Constants.SERVER_NAME, Constants.DEFAULT_SERVER) + "locations?group=" +
                sharedPreferences.getString(Constants.GROUP_NAME, Constants.DEFAULT_GROUP);

        Log.d(TAG, "getLocationsFromServer: " + url);
        //String url = "http://104.237.255.199:18003/locations?group=arman_8_5_96_2";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.d(TAG, "onResponse from server: " + res);
                try {
                    ArrayList<String> locations = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(res);
                    JSONObject array = jsonObject.getJSONObject("locations");
                    Iterator iterator = array.keys();
                    while (iterator.hasNext()) {
                        String k = (String) iterator.next();
                        locations.add(k);
                        Log.d(TAG, "onResponse: key " + k);
                    }
                    Intent intent = new Intent(getActivity(), LocationLearnedActivity.class);
                    intent.putExtra("locations", locations);
                    startActivity(intent);
                    getActivity().finish();
                    /*for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String k = object.keys().next();
                        Log.d(TAG, "onResponse: key: " + k);*/
                    //mWebView.loadUrl(String.format("javascript:update_map(\'%s\')", k));


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public String uploadDb(String filename) {
        String url = "http://192.168.0.82:5000/upload_db";
        final String res = "";
        try {
            final MediaType MEDIA_TYPE = MediaType.parse("text/*");
            OkHttpClient client = new OkHttpClient();

            SharedPreferences sharedPreferences;
            sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
            String strGroup = sharedPreferences.getString(Constants.GROUP_NAME, Constants.DEFAULT_GROUP);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", filename, RequestBody.create(MEDIA_TYPE, new File(filename)))
                    .addFormDataPart("ips_group", strGroup)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(getActivity(), response.body().string(), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    private void changeTrackCounter() {
        if (intTrackCounterDuration <= Constants.DEFAULT_TRACKING_COUNTER && imHere != null) {
            intTrackCounterDuration++;
            fieldTrackCounter.setText(String.valueOf(intTrackCounterDuration));
            trackCounterContainer.setVisibility(View.VISIBLE);
            Log.d(TAG, "run: log2");
            SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String strDt = simpleDate.format(Calendar.getInstance().getTime());

            LocationValidation l = new LocationValidation(null, imHere, currLocation, strDt);
            mDaoSession.getLocationValidationDao().insert(l);
        } else {
            trackCounterContainer.setVisibility(View.GONE);
            imHere = null;
            intTrackCounterDuration = 0;
        }
    }

    @Subscribe
    public void onBeaconReceive(HashMap<String, ArrayList<Integer>> hashMap2){
        Log.d(TAG, "onBeaconReceive: this is for test :" + hashMap2.size());
        this.hashMap = new HashMap<>(hashMap2);
        for (String s : hashMap2.keySet())
            Log.d(TAG, "onBeaconReceive: " + s + " : " + hashMap2.get(s));
        handler.post(runnableCode);
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


}

/*
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            super.onDetach();
            try {
                handler.removeCallbacks(runnableCode);
                LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiver);
                getNotifFromServer = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            handler.post(runnableCode);
            LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver,
                    new IntentFilter(Constants.TRACK_BCAST));
        }
    }
*/