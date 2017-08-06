package com.parsin.bletool.View.Fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parsin.bletool.Model.Advertisement;
import com.parsin.bletool.Controller.OnFragmentInteractionListener;
import com.parsin.bletool.R;
import com.parsin.bletool.Utils.Server.ParsinServer;
import com.parsin.bletool.Utils.Server.SendOptionEnum;
import com.parsin.bletool.Utils.StaticObjects;
import com.parsin.bletool.Utils.Utils;
import com.parsin.bletool.internal.Constants;
import com.parsin.bletool.internal.FindUtils;
import com.parsin.bletool.internal.wifi.WifiIntentReceiver;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by root on 4/12/17.
 */


public class TrackFragment extends Fragment {

    private static final String TAG = com.parsin.bletool.Test.TrackFragment.class.getSimpleName();

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


    private ArrayList<Advertisement> advertisements;
    //FIXME time of showing notif is not adopted with THRESH
    private int trackNotifyTRESH = 1;
    private int trackCounter;

    private ParsinServer parsin;




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
        sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        strGroup = sharedPreferences.getString(Constants.GROUP_NAME, Constants.DEFAULT_GROUP);
        strParsinServer = sharedPreferences.getString(StaticObjects.PARSIN_SERVER_NAME, StaticObjects.ParsinServerIp);
        strServer = sharedPreferences.getString(Constants.SERVER_NAME, Constants.DEFAULT_SERVER);
        strUsername = sharedPreferences.getString(Constants.USER_NAME, Constants.DEFAULT_USERNAME);
        trackVal = sharedPreferences.getInt(Constants.TRACK_INTERVAL, Constants.DEFAULT_TRACKING_INTERVAL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track, container, false);
        currLocView = (TextView) rootView.findViewById(R.id.labelLocationName);

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

        trackCounter = trackNotifyTRESH;

        advertisements = new ArrayList<>();

        handler.post(runnableCode);

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.TRACK_BCAST));
        return rootView;
    }


    // Getting the CurrentLocation from the received broadcast
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver(){
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
            strPreLocation = currLocation;

        }
    };

    private void notifying() {
        if (!strPreLocation.equals("")) {
            if (strPreLocation.equals(currLocation)) {
                if (trackCounter <= 0) {
                    for (Advertisement advertisement : advertisements) {
                        Log.d("resJSONreqFull", "resJSONreqx");
                        float x_y[] = Utils.getIntXY(currLocation);
                        if (!adverHasShown.contains(advertisement.getId())) {
                            if (Utils.inSquare(x_y[0], x_y[1], advertisement)) {
                                Log.d("Notification adv :",advertisement.getTitle());
                                Utils.notifyThis(advertisement.getId(),advertisement.getTitle(), advertisement.getText(),advertisement.getImg(), getActivity());
                                adverHasShown.add(advertisement.getId());
                            }
                        }
                    }
                    trackCounter = trackNotifyTRESH;
                }
                trackCounter--;
            } else
                trackCounter = trackNotifyTRESH;
        }
    }

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG,"run " + "salam");
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
                mContext.startService(intent);

            } else if (Build.VERSION.SDK_INT < 23) {
                if (FindUtils.isWiFiAvailable(mContext)) {
                    Intent intent = new Intent(mContext, WifiIntentReceiver.class);
                    intent.putExtra("event", Constants.TRACK_TAG);
                    intent.putExtra("groupName", strGroup);
                    intent.putExtra("userName", strUsername);
                    intent.putExtra("serverName", strServer);
                    intent.putExtra("locationName", sharedPreferences.getString(Constants.LOCATION_NAME, ""));
                    mContext.startService(intent);
                }
            } else {
                return;
            }
            handler.postDelayed(runnableCode, trackVal * 1000);
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
            if (mContext != null) {
                handler.post(runnableCode);
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


    public class WebAppInterface {
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


    ////////////////////////////// BLE /////////////////////////////////////////



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